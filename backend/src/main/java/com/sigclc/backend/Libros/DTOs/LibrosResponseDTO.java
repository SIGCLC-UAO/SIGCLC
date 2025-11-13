package com.sigclc.backend.Libros.DTOs;


import java.util.Date;

import com.sigclc.backend.Libros.Models.EstadoLibro;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class LibrosResponseDTO {
    
    private String id;
    private String titulo;
    private String autor;
    private Long anioPublicacion;
    private EstadoLibro estado; //pendiente, en lectura, leido
    private Date fechaSeleccion;
    private String sinopsis;
    private String portada;
    private String genero;

    
}