package com.sigclc.backend.Reuniones.DTOs;

import java.util.List;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class ReunionUpdateDTO {

    private String fechaHoraInicio;   // ISO-8601 (opcional)

    @Pattern(
        regexp = "^(presencial|virtual)$",
        message = "Modalidad inválida. Use: presencial | virtual."
    )
    private String modalidad;         // opcional

    private String lugar;             // requerido si modalidad=presencial (validar en Service)
    private String enlace;            // requerido si modalidad=virtual (validar en Service)

    private List<String> asistentesIds; // opcional (si decides permitirlo)
}
