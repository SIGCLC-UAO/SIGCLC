package com.sigclc.backend.Reuniones.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioActivoDTO {
    private String usuarioId;
    private Long totalAsistencias;  // Número de reuniones a las que ha asistido
}