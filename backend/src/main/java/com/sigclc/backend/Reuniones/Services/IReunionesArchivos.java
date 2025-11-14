package com.sigclc.backend.Reuniones.Services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sigclc.backend.Reuniones.DTOs.ArchivoDeleteRequestDTO;
import com.sigclc.backend.Reuniones.DTOs.ArchivoMetaSafeResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.ArchivoUploadResponseDTO;


public interface IReunionesArchivos {

    // Archivos (GridFS)
    List<ArchivoMetaSafeResponseDTO> listarArchivos(String reunionId);
    ArchivoUploadResponseDTO subirArchivos(String reunionId, List<MultipartFile> files);
    ArchivoUploadResponseDTO eliminarArchivo(String reunionId, ArchivoDeleteRequestDTO request);
}
