package com.sigclc.backend.Reseñas.DTOs;

import java.util.Date;
import java.util.List;

import com.sigclc.backend.Reseñas.Models.ComentarioResenia;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReseniaDetailDTO {

    private String id;
    private String libroId;
    private String libroTitulo;
    private String libroAutor;
    private String autorId;
    private String autorNombreCompleto;
    private int calificacion;
    private String opinionCompleta;
    private List<ComentarioResenia> comentarios;
    private List<String> archivosAdjuntos;
    private Date creadaEnUTC;
    private boolean esAutor;
    private boolean puedeEditarEliminar;
}