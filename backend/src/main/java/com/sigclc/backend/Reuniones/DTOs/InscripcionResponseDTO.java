package com.sigclc.backend.Reuniones.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @AllArgsConstructor @NoArgsConstructor
public class InscripcionResponseDTO {
    private String  mensaje;         // "inscrito" | "retirado"
    private Integer totalAsistentes; // cantidad resultante
}
