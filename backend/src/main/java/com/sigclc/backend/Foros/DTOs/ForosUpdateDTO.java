package com.sigclc.backend.Foros.DTOs;

import com.sigclc.backend.Foros.Models.TipoEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ForosUpdateDTO {
    
    @NotNull(message="El tipo de foro es obligatorio")
    private TipoEnum tipo;
    @NotBlank(message="El id del creador es obligatorio")
    private String creadorId;

}

