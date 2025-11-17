package com.sigclc.backend.Reuniones.DTOs;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @AllArgsConstructor @NoArgsConstructor
public class ReunionCreateDTO {

    @NotBlank(message = "La fecha y hora de inicio es obligatoria (ISO-8601).")
    private String fechaHoraInicio;

    @NotBlank(message = "La modalidad es obligatoria.")
    @Pattern(
        regexp = "^(presencial|virtual)$",
        message = "Modalidad inválida. Use: presencial | virtual."
    )
    private String modalidad;
    private String lugar;
    private String enlace;
    @NotBlank(message = "El libroId es obligatorio.")
    private String libroId;              

    private List<String> asistentesIds; 
}
