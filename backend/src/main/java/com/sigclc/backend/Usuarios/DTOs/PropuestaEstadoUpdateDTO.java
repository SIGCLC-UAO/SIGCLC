package com.sigclc.backend.Usuarios.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data @NoArgsConstructor @AllArgsConstructor
public class PropuestaEstadoUpdateDTO {

    @NotBlank(message = "usuarioId obligatorio")
    private String usuarioId;             

    @NotBlank(message = "nuevoEstado obligatorio")
    @Pattern(regexp = "Seleccionado|No Seleccionado",
             message = "nuevoEstado debe ser 'Seleccionado' o 'No Seleccionado'")
    private String nuevoEstado;
}
