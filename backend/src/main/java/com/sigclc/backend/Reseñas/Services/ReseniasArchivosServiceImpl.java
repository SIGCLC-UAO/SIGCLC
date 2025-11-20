package com.sigclc.backend.Reseñas.Services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.sigclc.backend.Reseñas.DTOs.ArchivoReseniaMetaDTO;
import com.sigclc.backend.Reseñas.DTOs.ArchivoReseniaUploadResponseDTO;
import com.sigclc.backend.Reseñas.Models.ReseniasModel;
import com.sigclc.backend.Reseñas.Repository.IReseniasRepository;

@Service
public class ReseniasArchivosServiceImpl implements IReseniasArchivosService {

    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of(".png", ".pdf", ".docx");

    @Autowired
    private IReseniasRepository reseniasRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Override
    public List<ArchivoReseniaMetaDTO> listarArchivos(String reseniaIdHex) {
        ObjectId reseniaObjectId = convertirAObjectId(reseniaIdHex);
        verificarExistenciaResenia(reseniaObjectId);
        
        return obtenerArchivosGridFs(reseniaObjectId);
    }

    @Override
    public ArchivoReseniaUploadResponseDTO subirArchivos(String reseniaIdHex, List<MultipartFile> archivosSubidos) {

        ObjectId reseniaObjectId = convertirAObjectId(reseniaIdHex);
        verificarExistenciaResenia(reseniaObjectId);

        guardarArchivosGridFs(reseniaObjectId, archivosSubidos);
        List<String> extensionesActuales = sincronizarExtensiones(reseniaObjectId);

        ArchivoReseniaUploadResponseDTO response = new ArchivoReseniaUploadResponseDTO();
        response.setReseniaId(reseniaIdHex);
        response.setArchivosAdjuntosActualizados(extensionesActuales);
        
        return response;
    }

    @Override
    public ArchivoReseniaUploadResponseDTO eliminarArchivo(String reseniaIdHex, String filename) {

        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("El nombre de archivo (filename) es obligatorio.");
        }

        ObjectId reseniaObjectId = convertirAObjectId(reseniaIdHex);
        verificarExistenciaResenia(reseniaObjectId);
        
        borrarArchivoPorNombre(reseniaObjectId, filename.trim());

        List<String> extensionesActuales = sincronizarExtensiones(reseniaObjectId);
        
        ArchivoReseniaUploadResponseDTO response = new ArchivoReseniaUploadResponseDTO();
        response.setReseniaId(reseniaIdHex);
        response.setNombreArchivoFinal(filename);
        response.setArchivosAdjuntosActualizados(extensionesActuales);
        
        return response;
    }

    @Override
    public Resource obtenerRecursoArchivo(String reseniaIdHex, String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("El nombre de archivo es obligatorio.");
        }
        ObjectId reseniaObjectId = convertirAObjectId(reseniaIdHex);
        verificarExistenciaResenia(reseniaObjectId);
        return obtenerRecursoArchivoInternal(reseniaObjectId, filename.trim());
    }

    @Override
    public void eliminarTodosArchivos(String reseniaIdHex) {
        ObjectId reseniaObjectId = convertirAObjectId(reseniaIdHex);
        Query query = new Query(Criteria.where("metadata.reseniaId").is(reseniaObjectId));
        gridFsTemplate.delete(query);
        
        ReseniasModel resenia = reseniasRepository.findById(reseniaObjectId)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));
        resenia.setArchivosAdjuntos(new ArrayList<>());
        reseniasRepository.save(resenia);
    }

    private void verificarExistenciaResenia(ObjectId reseniaObjectId) {
        boolean existe = reseniasRepository.existsById(reseniaObjectId);
        if (!existe) {
            throw new RuntimeException("Reseña no encontrada (404)");
        }
    }

    private void guardarArchivosGridFs(ObjectId reseniaObjectId, List<MultipartFile> archivosSubidos) {
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
                throw new IllegalArgumentException("Extensión no permitida: " + extension + " (use .png/.pdf/.docx)");
            }

            try {
                byte[] bytes = multipart.getBytes();
                String sha256 = calcularSha256(bytes);

                // ✅ CORREGIDO: reseniaId (no reunionId)
                Document metadata = new Document("reseniaId", reseniaObjectId)
                        .append("extension", extension)
                        .append("sha256", sha256);

                gridFsTemplate.store(new ByteArrayInputStream(bytes), nombreArchivo, multipart.getContentType(), metadata);
                
            } catch (DuplicateKeyException dke) {
                throw new IllegalStateException("Ya existe un archivo con ese nombre en esta reseña: " + nombreArchivo);
            } catch (IOException ioe) {
                throw new RuntimeException("Error leyendo archivo: " + nombreArchivo, ioe);
            }
        }
    }

    private List<ArchivoReseniaMetaDTO> obtenerArchivosGridFs(ObjectId reseniaObjectId) {
        List<ArchivoReseniaMetaDTO> resultado = new ArrayList<>();

        Query query = new Query(Criteria.where("metadata.reseniaId").is(reseniaObjectId));

        for (GridFSFile gridFile : gridFsTemplate.find(query)) {
            ArchivoReseniaMetaDTO dto = new ArchivoReseniaMetaDTO();
            dto.setNombre(gridFile.getFilename());
            dto.setSizeBytes(gridFile.getLength());
            
            Document metadata = gridFile.getMetadata();
            if (metadata != null) {
                dto.setExtension(metadata.getString("extension"));
            } else {
                dto.setExtension(extraerExtension(gridFile.getFilename()));
            }
            
            dto.setUrl("/SIGCLC/api/resenias/" + reseniaObjectId.toHexString() + "/archivos/" + gridFile.getFilename());

            resultado.add(dto);
        }

        return resultado;
    }

    private void borrarArchivoPorNombre(ObjectId reseniaObjectId, String filename) {
        Query query = new Query(Criteria.where("metadata.reseniaId").is(reseniaObjectId).and("filename").is(filename));

        GridFSFile gridFile = Optional.ofNullable(gridFsTemplate.findOne(query))
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado en esta reseña (404)"));

        gridFsTemplate.delete(new Query(Criteria.where("_id").is(gridFile.getObjectId())));
    }

    private List<String> sincronizarExtensiones(ObjectId reseniaObjectId) {
        Set<String> conjuntoExtensiones = new HashSet<>();

        Query query = new Query(Criteria.where("metadata.reseniaId").is(reseniaObjectId));

        for (GridFSFile gridFile : gridFsTemplate.find(query)) {
            Document metadata = gridFile.getMetadata();
            String extension = (metadata != null) ? metadata.getString("extension") : extraerExtension(gridFile.getFilename());

            if (extension != null && !extension.isBlank()) {
                conjuntoExtensiones.add(extension);
            }
        }

        ReseniasModel resenia = reseniasRepository.findById(reseniaObjectId)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada (404)"));

        resenia.setArchivosAdjuntos(new ArrayList<>(conjuntoExtensiones));
        reseniasRepository.save(resenia);

        return conjuntoExtensiones.stream().sorted().collect(Collectors.toList());
    }

    private GridFsResource obtenerRecursoArchivoInternal(ObjectId reseniaObjectId, String filename) {

        Query query = new Query(Criteria.where("metadata.reseniaId").is(reseniaObjectId).and("filename").is(filename));

        GridFSFile gridFile = Optional.ofNullable(gridFsTemplate.findOne(query))
                .orElseThrow(() -> new RuntimeException("Archivo no encontrado en esta reseña (404)"));

        return gridFsTemplate.getResource(gridFile);
    }

    private ObjectId convertirAObjectId(String idHex) {
        try {
            return new ObjectId(idHex);
        } catch (Exception ex) {
            throw new IllegalArgumentException("ObjectId inválido: " + idHex);
        }
    }

    private String extraerExtension(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank() || !nombreArchivo.contains(".")) {
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
            throw new RuntimeException("No se pudo calcular SHA-256", e);
        }
    }
}