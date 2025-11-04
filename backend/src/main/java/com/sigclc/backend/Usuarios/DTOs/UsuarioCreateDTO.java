package com.sigclc.backend.Usuarios.DTOs;

import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class UsuarioCreateDTO {

    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    private String apellido;

    @NotBlank(message = "El email es obligatorio.")
    private String email;

    @NotBlank(message = "El rol es obligatorio.")
    @Pattern(
        regexp = "^(lector|moderador|administrador)$",
        message = "Rol inválido. Use: lector | moderador | administrador."
    )
    private String rol;

    @NotNull(message = "El teléfono es obligatorio.")
    private Long telefono;

    @NotNull(message = "La edad es obligatoria.")
    private Long edad;

    @NotBlank(message = "La ocupación es obligatoria.")
    private String ocupacion;
}
