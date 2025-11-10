package com.sigclc.backend.Usuarios.DTOs;

import java.util.Date;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Body para actualizar un voto.
 * Campos son opcionales (PATCH): si vienen null, no se tocan.
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class VotoUpdateDTO {

    @Pattern(regexp = "Si|No", message = "voto debe ser 'Si' o 'No'")
    private String voto;     // opcional: "Si" | "No"

    private Date fechaVoto;  // opcional
}
