package com.sigclc.backend.Reuniones.Services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.sigclc.backend.Exception.RecursoNoEncontradoException;
import com.sigclc.backend.Reuniones.DTOs.ArchivoDeleteRequestDTO;
import com.sigclc.backend.Reuniones.DTOs.ArchivoMetaSafeResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.ArchivoUploadResponseDTO;
import com.sigclc.backend.Reuniones.Mappers.ReunionesMapper;
import com.sigclc.backend.Reuniones.Models.ReunionArchivoView;
import com.sigclc.backend.Reuniones.Models.ReunionesModel;
import com.sigclc.backend.Reuniones.Repository.IReunionesRepository;

@Service
public class ReunionesArchivosImp implements IReunionesArchivos {

    // Extensiones permitidas según tu esquema
    private static final Set<String> EXTENSIONES_PERMITIDAS =
            Set.of(".png", ".ppt", ".pdf", ".docx");

    @Autowired
    private IReunionesRepository reunionesRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private ReunionesMapper reunionesMapper;

    /* ======================= PÚBLICOS (INTERFAZ) ======================= */

    @Override
    public List<ArchivoMetaSafeResponseDTO> listarArchivos(String reunionIdHex) {
        ObjectId reunionObjectId = convertirAObjectId(reunionIdHex);
        List<ReunionArchivoView> archivos = obtenerArchivosGridFs(reunionObjectId);
        return reunionesMapper.toArchivoMetaList(archivos);
    }

    @Override
    public ArchivoUploadResponseDTO subirArchivos(String reunionIdHex,
                                                  List<MultipartFile> archivosSubidos) {

        ObjectId reunionObjectId = convertirAObjectId(reunionIdHex);
        verificarExistenciaReunion(reunionObjectId);

        guardarArchivosGridFs(reunionObjectId, archivosSubidos);
        List<String> extensionesActuales = sincronizarExtensiones(reunionObjectId);

        return new ArchivoUploadResponseDTO(
                reunionesMapper.toArchivoMetaList(obtenerArchivosGridFs(reunionObjectId)),
                extensionesActuales
        );
    }

    @Override
    public ArchivoUploadResponseDTO eliminarArchivo(String reunionIdHex,
                                                    ArchivoDeleteRequestDTO deleteRequest) {

        if (deleteRequest == null || deleteRequest.getFilename() == null
                || deleteRequest.getFilename().isBlank()) {
            throw new IllegalArgumentException("El nombre de archivo (filename) es obligatorio.");
        }

        ObjectId reunionObjectId = convertirAObjectId(reunionIdHex);
        borrarArchivoPorNombre(reunionObjectId, deleteRequest.getFilename().trim());

        List<String> extensionesActuales = sincronizarExtensiones(reunionObjectId);
        return new ArchivoUploadResponseDTO(
                reunionesMapper.toArchivoMetaList(obtenerArchivosGridFs(reunionObjectId)),
                extensionesActuales
        );
    }

    @Override
    public GridFsResource obtenerRecursoArchivo(String reunionIdHex, String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("El nombre de archivo es obligatorio.");
        }
        ObjectId reunionObjectId = convertirAObjectId(reunionIdHex);
        verificarExistenciaReunion(reunionObjectId);
        return obtenerRecursoArchivoInternal(reunionObjectId, filename.trim());
    }

    /* ============================ HELPERS ============================ */

    private void verificarExistenciaReunion(ObjectId reunionObjectId) {
        boolean existe = reunionesRepository.existsById(reunionObjectId);
        if (!existe) {
            throw new RecursoNoEncontradoException("Reunión no encontrada.");
        }
    }

    /**
     * Guarda archivos en GridFS cumpliendo el esquema:
     * metadata.reunionId, metadata.extension, metadata.sha256
     */
    private void guardarArchivosGridFs(ObjectId reunionObjectId,
                                       List<MultipartFile> archivosSubidos) {
        if (archivosSubidos == null || archivosSubidos.isEmpty()) {
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
                // Leemos el archivo en memoria para poder calcular SHA-256
                byte[] bytes = multipart.getBytes();
                String sha256 = calcularSha256(bytes);

                Document metadata = new Document("reunionId", reunionObjectId)
                        .append("extension", extension)
                        .append("sha256", sha256);

                gridFsTemplate.store(
                        new ByteArrayInputStream(bytes),
                        nombreArchivo,
                        multipart.getContentType(),
                        metadata
                );
            } catch (DuplicateKeyException dke) {
                // GlobalExceptionHandler transformará esto en 409 (IllegalState)
                throw new IllegalStateException(
                        "Ya existe un archivo con ese nombre en esta reunión: " + nombreArchivo);
            } catch (IOException ioe) {
                throw new RuntimeException("Error leyendo archivo: " + nombreArchivo, ioe);
            }
        }
    }

    /**
     * Lista archivos de GridFS como vistas de dominio.
     */
    private List<ReunionArchivoView> obtenerArchivosGridFs(ObjectId reunionObjectId) {
        List<ReunionArchivoView> resultado = new ArrayList<>();

        Query query = new Query(Criteria
                .where("metadata.reunionId").is(reunionObjectId));

        for (GridFSFile gridFile : gridFsTemplate.find(query)) {
            ReunionArchivoView view = new ReunionArchivoView();
            view.setFileId((ObjectId) gridFile.getObjectId());
            view.setFilename(gridFile.getFilename());
            view.setLength(gridFile.getLength());
            view.setUploadDate(gridFile.getUploadDate());

            Document metadata = gridFile.getMetadata();
            if (metadata != null) {
                view.setExtension(metadata.getString("extension"));
                view.setSha256(metadata.getString("sha256"));
                String uploaderId = metadata.getString("uploaderId");
                if (uploaderId != null && !uploaderId.isBlank()) {
                    try {
                        view.setUploaderId(new ObjectId(uploaderId));
                    } catch (Exception ignore) {
                        // Si no es un ObjectId válido, simplemente lo ignoramos
                    }
                }
            } else {
                view.setExtension(extraerExtension(gridFile.getFilename()));
            }

            resultado.add(view);
        }

        return resultado;
    }

    /**
     * Elimina un archivo de GridFS por reunión + nombre.
     * Usamos Optional para evitar "dead code" en la validación.
     */
    private void borrarArchivoPorNombre(ObjectId reunionObjectId, String filename) {
        Query query = new Query(
                Criteria.where("metadata.reunionId").is(reunionObjectId)
                        .and("filename").is(filename)
        );

        GridFSFile gridFile = Optional
                .ofNullable(gridFsTemplate.findOne(query))
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Archivo no encontrado en esta reunión.")
                );

        gridFsTemplate.delete(
                new Query(Criteria.where("_id").is(gridFile.getObjectId()))
        );
    }

    /**
     * Recorre todos los archivos de la reunión, actualiza el campo
     * `archivosAdjuntos` de la colección Reuniones y devuelve el listado
     * de extensiones ordenado.
     */
    private List<String> sincronizarExtensiones(ObjectId reunionObjectId) {
        Set<String> conjuntoExtensiones = new HashSet<>();

        Query query = new Query(
                Criteria.where("metadata.reunionId").is(reunionObjectId)
        );

        for (GridFSFile gridFile : gridFsTemplate.find(query)) {
            Document metadata = gridFile.getMetadata();
            String extension = (metadata != null)
                    ? metadata.getString("extension")
                    : extraerExtension(gridFile.getFilename());

            if (extension != null && !extension.isBlank()) {
                conjuntoExtensiones.add(extension);
            }
        }

        ReunionesModel reunion = reunionesRepository.findById(reunionObjectId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reunión no encontrada."));

        reunion.setArchivosAdjuntos(new ArrayList<>(conjuntoExtensiones));
        reunionesRepository.save(reunion);

        return conjuntoExtensiones.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el recurso de un archivo (para descarga) por reunión + nombre.
     * También con Optional para evitar warning de dead code.
     */
    private GridFsResource obtenerRecursoArchivoInternal(ObjectId reunionObjectId,
                                                         String filename) {

        Query query = new Query(
                Criteria.where("metadata.reunionId").is(reunionObjectId)
                        .and("filename").is(filename)
        );

        GridFSFile gridFile = Optional
                .ofNullable(gridFsTemplate.findOne(query))
                .orElseThrow(() ->
                        new RecursoNoEncontradoException("Archivo no encontrado en esta reunión.")
                );

        return gridFsTemplate.getResource(gridFile);
    }

    /* ====================== UTILIDADES BÁSICAS ====================== */

    private ObjectId convertirAObjectId(String idHex) {
        try {
            return new ObjectId(idHex);
        } catch (Exception ex) {
            throw new IllegalArgumentException("ObjectId inválido: " + idHex);
        }
    }

    private String extraerExtension(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank()
                || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf(".")).toLowerCase();
    }

    private String calcularSha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data);

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // No debería pasar en un entorno normal (SHA-256 es estándar)
            throw new RuntimeException("No se pudo calcular SHA-256", e);
        }
    }
}
