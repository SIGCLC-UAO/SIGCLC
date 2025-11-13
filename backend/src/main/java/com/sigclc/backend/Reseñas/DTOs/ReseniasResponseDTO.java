package com.sigclc.backend.Reseñas.DTOs;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.sigclc.backend.Reseñas.Models.ComentarioResenia;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para devolver reseñas en respuestas HTTP.
 */
@Data
@NoArgsConstructor
public class ReseniasResponseDTO {

    private ObjectId id;
    private ObjectId libroId;
    private ObjectId autorId;
    private int calificacion;
    private String opinion;
    private List<String> archivosAdjuntos;
    private List<ComentarioResenia> comentarios;
    private Integer meGusta;
    private Date fecha;
}
