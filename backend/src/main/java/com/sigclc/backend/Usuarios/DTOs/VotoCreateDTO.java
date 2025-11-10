package com.sigclc.backend.Usuarios.DTOs;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class VotoCreateDTO {

    @NotBlank(message = "El voto es obligatorio.")
    @Pattern(
        regexp = "^(Si|No)$",
        message = "Voto inválido. Use: Si | No."
    )
    private String voto;

    @NotNull(message = "La fechaVoto es obligatoria.")
    private Date fechaVoto;

    @NotBlank(message = "El Id de Usuario es obligatorio.")
    private String usuarioId;
}
