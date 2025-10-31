package com.sigclc.backend.Usuarios.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioUpdateDTO {

    private String nombre;
    private String apellido;
    private String email;
    private String rol;       // "lector" | "moderador" | "administrador"
    private Long   telefono;  
    private Long   edad;      
    private String ocupacion;
}
