package com.sigclc.backend.Usuarios.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor

public class PropuestaCreateResponseDTO {

    private String usuarioId;     
    private String nombreUsuario; 
    private String libroId;       
    private String estado;        // "En Votacion" fijo al crear

    
    private Integer votosSi;      
    private Integer votosNo;      
    private Integer totalVotos;   
}
