package com.sigclc.backend.Reuniones.Services;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.sigclc.backend.Reuniones.DTOs.ReunionCreateDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionUpdateDTO;
import com.sigclc.backend.Reuniones.Mappers.ReunionesMapper;
import com.sigclc.backend.Reuniones.Models.ReunionesModel;
import com.sigclc.backend.Reuniones.Repository.IReunionesRepository;

@Service
public class ReunionesServiceImp implements IReunionesService {

    private static final Set<String> EXTENSIONES_PERMITIDAS =
            Set.of(".png", ".ppt", ".pdf", ".docx");

    @Autowired
    private IReunionesRepository reunionesRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private ReunionesMapper reunionesMapper;

    /* ===================== CRUD ===================== */

    @Override
    public ReunionResponseDTO crear(ReunionCreateDTO createDTO,
                                    List<MultipartFile> archivosSubidos) {

        Objects.requireNonNull(createDTO, "ReunionCreateDTO no puede ser null");

        validarCreate(createDTO);

        // 1. Mapear DTO -> entidad y guardar reunión
        ReunionesModel nuevaReunion = reunionesMapper.toEntityFromCreate(createDTO);
        reunionesRepository.save(nuevaReunion);

        ObjectId reunionId = nuevaReunion.getId();

        // 2. Guardar archivos (si vienen) y actualizar extensiones
        if (archivosSubidos != null && !archivosSubidos.isEmpty()) {
            guardarArchivosGridFs(reunionId, archivosSubidos);
            actualizarExtensionesDesdeGridFs(reunionId);
        }

        // 3. Devolver el resumen de la reunión recién creada
        return tomarUnicoOPrimero(reunionesRepository.resumenPorId(reunionId));
    }

    @Override
    public ReunionResponseDTO actualizar(String reunionIdHex,
                                         ReunionUpdateDTO updateDTO) {

        Objects.requireNonNull(reunionIdHex, "reunionId no puede ser null");
        Objects.requireNonNull(updateDTO, "ReunionUpdateDTO no puede ser null");

        ObjectId reunionObjectId = convertirAObjectId(reunionIdHex);

        ReunionesModel reunionExistente = obtenerReunionPorObjectId(reunionObjectId);
        reunionesMapper.applyUpdate(reunionExistente, updateDTO);

        validarSegunModalidad(
                reunionExistente.getModalidad(),
                reunionExistente.getLugar(),
                reunionExistente.getEnlace()
        );

        reunionesRepository.save(reunionExistente);
        return tomarUnicoOPrimero(reunionesRepository.resumenPorId(reunionObjectId));
    }

    @Override
    public void eliminar(String reunionIdHex) {

        Objects.requireNonNull(reunionIdHex, "reunionId no puede ser null");
        ObjectId reunionObjectId = convertirAObjectId(reunionIdHex);

        // 1. Borrar todos los archivos asociados en GridFS
        borrarTodosArchivosGridFs(reunionObjectId);

        // 2. Borrar la reunión en la colección principal
        reunionesRepository.deleteById(reunionObjectId);
    }

    @Override
    public ReunionResponseDTO resyncExtensiones(String reunionIdHex) {

        Objects.requireNonNull(reunionIdHex, "reunionId no puede ser null");
        ObjectId reunionObjectId = convertirAObjectId(reunionIdHex);

        actualizarExtensionesDesdeGridFs(reunionObjectId);
        return tomarUnicoOPrimero(reunionesRepository.resumenPorId(reunionObjectId));
    }

    /* ============= Helpers de validación ============= */

    private void validarCreate(ReunionCreateDTO dto) {
        if (estaVacio(dto.getFechaHoraInicio())) {
            throw new IllegalArgumentException("fechaHoraInicio es obligatoria (ISO-8601).");
        }
        if (estaVacio(dto.getModalidad())) {
            throw new IllegalArgumentException("modalidad es obligatoria.");
        }
        if (estaVacio(dto.getLibroId())) {
            throw new IllegalArgumentException("libroId es obligatorio.");
        }

        validarModalidad(dto.getModalidad());
        validarSegunModalidad(dto.getModalidad(), dto.getLugar(), dto.getEnlace());
    }

    private void validarModalidad(String modalidad) {
        String normalizada = aMinusculasSeguro(modalidad);
        if (!("presencial".equals(normalizada) || "virtual".equals(normalizada))) {
            throw new IllegalArgumentException("Modalidad inválida. Use: presencial | virtual.");
        }
    }

    private void validarSegunModalidad(String modalidad,
                                       String lugar,
                                       String enlace) {

        String normalizada = aMinusculasSeguro(modalidad);

        if ("presencial".equals(normalizada) && estaVacio(lugar)) {
            throw new IllegalArgumentException(
                    "Para modalidad presencial, 'lugar' es obligatorio.");
        }

        if ("virtual".equals(normalizada) && estaVacio(enlace)) {
            throw new IllegalArgumentException(
                    "Para modalidad virtual, 'enlace' es obligatorio.");
        }
    }

    /* ========== GridFS (guardar / borrar / sync) ========== */

    /**
     * Guarda uno o varios archivos en GridFS para una reunión,
     * con metadata requerida por el $jsonSchema: reunionId, extension, sha256.
     */
    private void guardarArchivosGridFs(ObjectId reunionObjectId,
                                       List<MultipartFile> archivosSubidos) {

        if (archivosSubidos == null) {
            return;
        }

        for (MultipartFile multipart : archivosSubidos) {
            if (multipart == null || multipart.isEmpty()) {
                continue;
            }

            String nombreArchivo = multipart.getOriginalFilename();
            String extension = extraerExtension(nombreArchivo);

            if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
                throw new IllegalArgumentException(
                        "Extensión no permitida: " + extension +
                        " (use .png/.ppt/.pdf/.docx)");
            }

            try {
                String sha256 = calcularSha256(multipart);

                Document metadata = new Document("reunionId", reunionObjectId)
                        .append("extension", extension)
                        .append("sha256", sha256);

                // Requiere índice único: { "metadata.reunionId": 1, "filename": 1 } unique
                gridFsTemplate.store(
                        multipart.getInputStream(),
                        nombreArchivo,
                        multipart.getContentType(),
                        metadata
                );
            } catch (DuplicateKeyException dke) {
                throw new IllegalArgumentException(
                        "Ya existe un archivo con ese nombre en esta reunión: " + nombreArchivo);
            } catch (IOException ioe) {
                throw new RuntimeException("Error leyendo archivo: " + nombreArchivo, ioe);
            }
        }
    }

    /**
     * Elimina todos los archivos de GridFS asociados a una reunión.
     */
    private void borrarTodosArchivosGridFs(ObjectId reunionObjectId) {
        Query query = new Query(Criteria.where("metadata.reunionId").is(reunionObjectId));
        gridFsTemplate.delete(query);
    }

    /**
     * Recalcula las extensiones presentes en GridFS y actualiza
     * el campo archivosAdjuntos de la reunión.
     */
    private List<String> actualizarExtensionesDesdeGridFs(ObjectId reunionObjectId) {

        Set<String> conjuntoExtensiones = new HashSet<>();

        for (GridFSFile gridFile : gridFsTemplate.find(
                new Query(Criteria.where("metadata.reunionId").is(reunionObjectId)))) {

            String extension;
            if (gridFile.getMetadata() != null) {
                extension = gridFile.getMetadata().getString("extension");
            } else {
                extension = extraerExtension(gridFile.getFilename());
            }

            if (extension != null) {
                conjuntoExtensiones.add(extension);
            }
        }

        ReunionesModel reunion = obtenerReunionPorObjectId(reunionObjectId);
        reunion.setArchivosAdjuntos(new ArrayList<>(conjuntoExtensiones));
        reunionesRepository.save(reunion);

        return conjuntoExtensiones.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /* =================== Utilidades comunes =================== */

    private ReunionesModel obtenerReunionPorObjectId(ObjectId reunionObjectId) {
        return reunionesRepository.findById(reunionObjectId)
                .orElseThrow(() -> new IllegalArgumentException("Reunión no encontrada"));
    }

    private ObjectId convertirAObjectId(String idHex) {
        try {
            return new ObjectId(idHex);
        } catch (Exception ex) {
            throw new IllegalArgumentException("ObjectId inválido: " + idHex);
        }
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String aMinusculasSeguro(String valor) {
        return (valor == null) ? "" : valor.trim().toLowerCase();
    }

    private String extraerExtension(String nombreArchivo) {
        if (estaVacio(nombreArchivo) || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf(".")).toLowerCase();
    }

    private String calcularSha256(MultipartFile archivo) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(archivo.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException("No fue posible calcular el hash del archivo.", e);
        }
    }

    private <T> T tomarUnicoOPrimero(List<T> lista) {
        return (lista == null || lista.isEmpty()) ? null : lista.get(0);
    }
}
