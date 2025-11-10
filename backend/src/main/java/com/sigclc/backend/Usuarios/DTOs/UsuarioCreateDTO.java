package com.sigclc.backend.Usuarios.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioCreateDTO {
    private String nombre;
    private String apellido;
    private String email;
    private String rol;       // "lector" | "moderador" | "administrador"
    private Long telefono;    // bsonType: "long"
    private Long edad;        // bsonType: "long" (4..99)
    private String ocupacion;
}
