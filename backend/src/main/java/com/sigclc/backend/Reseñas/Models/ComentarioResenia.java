package com.sigclc.backend.Reseñas.Models;

import java.util.Date;

import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Subdocumento que representa los comentarios dentro de una reseña.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioResenia {

    private String utilidad;        // Ej: "Útil", "No útil", etc.
    private ObjectId usuarioId;     // Usuario que comentó
    private String comentario;      // Texto del comentario
    private Date fecha = new Date(); // Fecha del comentario

    // Constructor simplificado para uso rápido
    public ComentarioResenia(String utilidad, ObjectId usuarioId, String comentario) {
        this.utilidad = utilidad;
        this.usuarioId = usuarioId;
        this.comentario = comentario;
        this.fecha = new Date();
    }
}
