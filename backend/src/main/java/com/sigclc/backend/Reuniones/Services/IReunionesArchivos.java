package com.sigclc.backend.Reuniones.Services;

import java.util.List;

import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

import com.sigclc.backend.Reuniones.DTOs.ArchivoDeleteRequestDTO;
import com.sigclc.backend.Reuniones.DTOs.ArchivoMetaSafeResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.ArchivoUploadResponseDTO;

public interface IReunionesArchivos {

    // Listar metadata de archivos de una reunión
    List<ArchivoMetaSafeResponseDTO> listarArchivos(String reunionId);

    // Subir uno o varios archivos
    ArchivoUploadResponseDTO subirArchivos(String reunionId, List<MultipartFile> archivosSubidos);

    // Eliminar archivo por nombre
    ArchivoUploadResponseDTO eliminarArchivo(String reunionId, ArchivoDeleteRequestDTO request);

    // Obtener recurso de GridFS para descargar
    GridFsResource obtenerRecursoArchivo(String reunionId, String filename);
}
