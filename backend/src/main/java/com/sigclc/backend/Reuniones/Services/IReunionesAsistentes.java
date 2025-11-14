package com.sigclc.backend.Reuniones.Services;

import com.sigclc.backend.Reuniones.DTOs.InscripcionResponseDTO;

public interface IReunionesAsistentes {
    // Asistentes
    InscripcionResponseDTO inscribirAsistente(String reunionId, String usuarioId);
    InscripcionResponseDTO retirarAsistente(String reunionId, String usuarioId);

  
}
