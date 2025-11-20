package com.sigclc.backend.Libros.DTOs;


import java.util.Date;

import com.sigclc.backend.Libros.Models.EstadoLibro;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class LibrosUpdateDTO {

    private String titulo;
    private String autor;
    private Long anioPublicacion;
    private EstadoLibro estado; // pendiente, en lectura, leído
    private Date fechaSeleccion;
    private String sinopsis;
    private String portada;
    private String genero;
}

