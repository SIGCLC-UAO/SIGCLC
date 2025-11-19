package com.sigclc.backend.Reseñas.Models;

import java.util.Date;

import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComentarioResenia {

    private String utilidad;
    private ObjectId usuarioId;
    private String comentario;
    private Date fecha = new Date();

    public ComentarioResenia(String utilidad, ObjectId usuarioId, String comentario) {
        this.utilidad = utilidad;
        this.usuarioId = usuarioId;
        this.comentario = comentario;
        this.fecha = new Date();
    }
}