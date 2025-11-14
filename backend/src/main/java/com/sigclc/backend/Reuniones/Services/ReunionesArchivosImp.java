package com.sigclc.backend.Reuniones.Services;

import java.io.IOException;
import java.util.*;
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
import com.sigclc.backend.Reuniones.DTOs.ArchivoDeleteRequestDTO;
import com.sigclc.backend.Reuniones.DTOs.ArchivoMetaSafeResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.ArchivoUploadResponseDTO;
import com.sigclc.backend.Reuniones.Mappers.ReunionesMapper;
import com.sigclc.backend.Reuniones.Models.ReunionArchivoView;
import com.sigclc.backend.Reuniones.Models.ReunionesModel;
import com.sigclc.backend.Reuniones.Repository.IReunionesRepository;

@Service
public class ReunionesArchivosImp implements IReunionesArchivos {

    private static final Set<String> EXTENSIONES_PERMITIDAS =
            Set.of(".png", ".ppt", ".pdf", ".docx");

    @Autowired
    private IReunionesRepository reunionesRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private ReunionesMapper reunionesMapper;

    @Override
    public List<ArchivoMetaSafeResponseDTO> listarArchivos(String reunionIdHex) {
        Objects.requireNonNull(reunionIdHex, "reunionId no puede ser null");
        ObjectId reunionObjectId = convertirAObjectId(reunionIdHex);
        List<ReunionArchivoView> archivos = obtenerArchivosGridFs(reunionObjectId);
        return reunionesMapper.toArchivoMetaList(archivos);
    }

    @Override
    public ArchivoUploadResponseDTO subirArchivos(String reunionIdHex,
                                                  List<MultipartFile> archivosSubidos) {
        Objects.requireNonNull(reunionIdHex, "reunionId no puede ser null");
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
        Objects.requireNonNull(reunionIdHex, "reunionId no puede ser null");

        if (deleteRequest == null
                || deleteRequest.getFilename() == null
                || deleteRequest.getFilename().isBlank()) {
            throw new IllegalArgumentException("filename es obligatorio");
        }

        ObjectId reunionObjectId = convertirAObjectId(reunionIdHex);
        borrarArchivoPorNombre(reunionObjectId, deleteRequest.getFilename().trim());

        List<String> extensionesActuales = sincronizarExtensiones(reunionObjectId);
        return new ArchivoUploadResponseDTO(
                reunionesMapper.toArchivoMetaList(obtenerArchivosGridFs(reunionObjectId)),
                extensionesActuales
        );
    }

    /* ===================== Helpers GridFS ===================== */

    private void verificarExistenciaReunion(ObjectId reunionObjectId) {
        ObjectId nonNullId = Objects.requireNonNull(reunionObjectId, "reunionObjectId es obligatorio");
        reunionesRepository.findById(nonNullId)
                .orElseThrow(() -> new IllegalArgumentException("Reunión no encontrada"));
    }

    private void guardarArchivosGridFs(ObjectId reunionObjectId,
                                       List<MultipartFile> archivosSubidos) {
        if (archivosSubidos == null) return;

        for (MultipartFile multipart : archivosSubidos) {
            if (multipart == null || multipart.isEmpty()) continue;

            String nombreArchivo = multipart.getOriginalFilename();
            String extension = extraerExtension(nombreArchivo);

            if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
                throw new IllegalArgumentException(
                        "Extensión no permitida: " + extension + " (use .png/.ppt/.pdf/.docx)");
            }

            try {
                Document metadata = new Document("reunionId", reunionObjectId)
                        .append("extension", extension);
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
                throw new RuntimeException("Error leyendo archivo: " + nombreArchivo);
            }
        }
    }

    private List<ReunionArchivoView> obtenerArchivosGridFs(ObjectId reunionObjectId) {
        ObjectId nonNullId = Objects.requireNonNull(reunionObjectId, "reunionObjectId es obligatorio");

        List<ReunionArchivoView> resultado = new ArrayList<>();
        Query query = new Query(Criteria.where("metadata.reunionId").is(nonNullId));

        for (GridFSFile gridFile : gridFsTemplate.find(query)) {
            ReunionArchivoView view = new ReunionArchivoView();
            view.setFileId(gridFile.getObjectId());
            view.setFilename(gridFile.getFilename());

            String extension = (gridFile.getMetadata() != null)
                    ? gridFile.getMetadata().getString("extension")
                    : extraerExtension(gridFile.getFilename());

            view.setExtension(extension);
            view.setLength(gridFile.getLength());
            view.setUploadDate(gridFile.getUploadDate());
            resultado.add(view);
        }
        return resultado;
    }

    private void borrarArchivoPorNombre(ObjectId reunionObjectId, String filename) {
        ObjectId nonNullId = Objects.requireNonNull(reunionObjectId, "reunionObjectId es obligatorio");

        Query query = new Query(
                Criteria.where("metadata.reunionId").is(nonNullId)
                        .and("filename").is(filename)
        );

        GridFSFile gridFile = Optional.ofNullable(gridFsTemplate.findOne(query))
                .orElseThrow(() -> new IllegalArgumentException("Archivo no encontrado en esta reunión"));

        gridFsTemplate.delete(new Query(Criteria.where("_id").is(gridFile.getObjectId())));
    }

    private List<String> sincronizarExtensiones(ObjectId reunionObjectId) {
        ObjectId nonNullId = Objects.requireNonNull(reunionObjectId, "reunionObjectId es obligatorio");

        Set<String> conjuntoExtensiones = new HashSet<>();

        Query query = new Query(Criteria.where("metadata.reunionId").is(nonNullId));
        for (GridFSFile gridFile : gridFsTemplate.find(query)) {
            String extension = (gridFile.getMetadata() != null)
                    ? gridFile.getMetadata().getString("extension")
                    : extraerExtension(gridFile.getFilename());
            if (extension != null && !extension.isBlank()) {
                conjuntoExtensiones.add(extension);
            }
        }

        ReunionesModel reunion = reunionesRepository.findById(nonNullId)
                .orElseThrow(() -> new IllegalArgumentException("Reunión no encontrada"));
        reunion.setArchivosAdjuntos(new ArrayList<>(conjuntoExtensiones));
        reunionesRepository.save(reunion);

        return conjuntoExtensiones.stream()
                .sorted()
                .collect(Collectors.toList());
    }

    /* ===================== Utilidades ===================== */

    private ObjectId convertirAObjectId(String idHex) {
        try {
            Objects.requireNonNull(idHex, "idHex no puede ser null");
            return new ObjectId(idHex);
        } catch (Exception ex) {
            throw new IllegalArgumentException("ObjectId inválido: " + idHex);
        }
    }

    private String extraerExtension(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank() || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo;
    }
}
                