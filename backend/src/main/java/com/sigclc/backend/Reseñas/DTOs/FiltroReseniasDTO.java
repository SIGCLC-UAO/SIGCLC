package com.sigclc.backend.Reseñas.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FiltroReseniasDTO {

    private String libroId;
    private String autorId;
    private Integer calificacionMin;
    private Integer calificacionMax;
    private Boolean tieneAdjuntos;
    private String texto;
    private String comentadoPor;
    
    private Integer pagina = 0;
    private Integer tamanio = 10;
}