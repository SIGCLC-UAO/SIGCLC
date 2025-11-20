package com.sigclc.backend.Foros.DTOs;

import com.sigclc.backend.Foros.Models.TipoEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ForosUpdateDTO {
    
    private TipoEnum tipo;
    private String creadorId;

}

