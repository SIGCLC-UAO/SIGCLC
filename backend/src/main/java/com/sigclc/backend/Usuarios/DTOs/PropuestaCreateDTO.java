package com.sigclc.backend.Usuarios.DTOs;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor
public class PropuestaCreateDTO {

    @NotBlank(message = "El libroId es obligatorio.")
    private String libroId;
}
