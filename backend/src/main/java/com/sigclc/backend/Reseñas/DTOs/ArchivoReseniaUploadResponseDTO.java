package com.sigclc.backend.Reseñas.DTOs;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArchivoReseniaUploadResponseDTO {

    private String reseniaId;
    private String nombreArchivoFinal;
    private ArchivoReseniaMetaDTO metadata;

    // Importante: coincide EXACTAMENTE con tu modelo
    private List<String> archivosAdjuntosActualizados;
}
