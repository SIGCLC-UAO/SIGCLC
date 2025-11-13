package com.sigclc.backend.Libros.DTOs;


import java.util.Date;

import com.sigclc.backend.Libros.Models.EstadoLibro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor


public class LibrosCreateDTO{

    @NotBlank(message="El titulo es obligatorio")
    private String titulo;
    @NotBlank(message="El autor es obligatorio")
    private String autor;
    @NotNull(message="El año de publicacion es obligatorio")
    private Long anioPublicacion;
    @NotNull(message="El estado de lectura es obligatorio")
    private EstadoLibro estado; //pendiente, en lectura, leido
    private Date fechaSeleccion; //opcional
    @NotBlank(message="La sinopsis es obligatoria")
    private String sinopsis;
    private String portada; //opcional
    @NotBlank(message="El genero es obligatorio")
    private String genero; 
}  