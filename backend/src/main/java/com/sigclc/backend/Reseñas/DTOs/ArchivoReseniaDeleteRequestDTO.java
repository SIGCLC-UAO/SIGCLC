package com.sigclc.backend.Reseñas.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para eliminar un archivo por nombre dentro de la reseña.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchivoReseniaDeleteRequestDTO {

    @NotBlank(message = "El nombre del archivo es obligatorio.")
    private String filename;
}