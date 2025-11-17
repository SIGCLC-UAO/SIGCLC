package com.sigclc.backend.Usuarios.DTOs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class UsuarioUpdateDTO {

    // Todos opcionales (PATCH). Si llegan, se validan.
    private String nombre;
    private String apellido;
    private Long telefono;

    private Long edad;

    private String ocupacion;

    public Object getTelefono() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
