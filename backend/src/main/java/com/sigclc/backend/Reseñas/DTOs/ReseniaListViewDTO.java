package com.sigclc.backend.Reseñas.DTOs;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReseniaListViewDTO {

    private String id;
    private String libroId;
    private String libroTitulo;
    private String autorId;
    private String autorNombreCompleto;
    private int calificacion;
    private String resumenOpinion;
    private int totalComentarios;
    private boolean tieneAdjuntos;
    private Date creadaEnUTC;
}