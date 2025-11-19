package com.sigclc.backend.Reseñas.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ComentarioCreateDTO {

    @NotNull(message = "La utilidad es obligatoria")
    private String utilidad;

    @NotNull(message = "El comentario es obligatorio")
    private String comentario;
    
    @NotNull(message = "El usuarioId es obligatorio")
    private String usuarioId;
}