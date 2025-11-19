package com.sigclc.backend.Reseñas.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopReviewerDTO {

    private String usuarioId;
    private String nombreCompleto;
    private long cantidadResenias;
}