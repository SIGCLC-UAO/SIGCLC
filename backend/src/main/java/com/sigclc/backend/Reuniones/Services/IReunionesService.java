package com.sigclc.backend.Reuniones.Services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sigclc.backend.Reuniones.DTOs.ReunionCreateDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionUpdateDTO;

public interface IReunionesService {

    // CRUD
    ReunionResponseDTO crear(ReunionCreateDTO dto, List<MultipartFile> files);
    ReunionResponseDTO actualizar(String reunionId, ReunionUpdateDTO dto);
    void eliminar(String reunionId);
    ReunionResponseDTO resyncExtensiones(String reunionId);
    
}
