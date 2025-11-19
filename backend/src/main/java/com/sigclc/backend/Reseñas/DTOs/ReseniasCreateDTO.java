package com.sigclc.backend.Reseñas.DTOs;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReseniasCreateDTO {

    @NotNull(message = "El ID del libro es obligatorio")
    private String libroId;

    @NotNull(message = "El ID del autor es obligatorio")
    private String autorId;

    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private int calificacion;

    @NotNull(message = "Debe ingresar una opinión")
    private String opinion;

    private List<String> archivosAdjuntos;
}