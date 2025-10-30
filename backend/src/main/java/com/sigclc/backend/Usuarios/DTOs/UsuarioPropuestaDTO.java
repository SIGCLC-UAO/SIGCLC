package com.sigclc.backend.Usuarios.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPropuestaDTO {
    private String  nombreUsuario;  
    private String  libroId;        
    private String  estado;        
    private Integer votosSi;        
    private Integer votosNo;        
    private Integer totalVotos;     
}
