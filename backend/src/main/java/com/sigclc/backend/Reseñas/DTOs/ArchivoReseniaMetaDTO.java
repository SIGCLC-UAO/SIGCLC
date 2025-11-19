package com.sigclc.backend.Reseñas.DTOs;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArchivoReseniaMetaDTO {

    private String nombre;
    private String extension;
    private long sizeBytes;
    private String url; // o path donde lo guardes
}
