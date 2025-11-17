package com.sigclc.backend.Reuniones.DTOs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @AllArgsConstructor @NoArgsConstructor
public class ReunionResponseDTO {
    private String fechaHoraInicio;             // ISO-8601
    private String modalidad;                   // "presencial" | "virtual"
    private String libroTitulo;                 // derivado de libroId
    private String libroAutor;                  // opcional
    private List<String> extensionesAdjuntas;   // .png/.ppt/.pdf/.docx (opcional)
}
