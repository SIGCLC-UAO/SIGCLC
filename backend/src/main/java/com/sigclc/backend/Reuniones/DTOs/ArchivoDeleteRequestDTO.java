package com.sigclc.backend.Reuniones.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para eliminar un archivo por nombre dentro de la reunión.
 * Esto funciona porque hay un índice único en GridFS:
 *   { "metadata.reunionId": 1, "filename": 1 } (unique)
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class ArchivoDeleteRequestDTO {

    @NotBlank(message = "El nombre del archivo es obligatorio.")
    private String filename;
}
