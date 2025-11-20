package com.sigclc.backend.Reseñas.DTOs;

import java.util.Date;
import java.util.List;

import com.sigclc.backend.Reseñas.Models.ComentarioResenia;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReseniasResponseDTO {

    private String id;
    private String libroId;
    private String autorId;
    
    private int calificacion;
    private String opinion;
    
    private List<String> archivosAdjuntos;
    private List<ComentarioResenia> comentarios;
    
    private Integer meGusta;
    private Date fecha;
}