package com.sigclc.backend.Reuniones.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metadata segura del archivo (sin exponer IDs internos).
 * Compatible con validaciones en Postman.
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class ArchivoMetaSafeResponseDTO {
    private String filename;
    private String extension;     // ".png" | ".ppt" | ".pdf" | ".docx"
    private long   length;        // bytes
    private String uploadDate;    // ISO-8601
}
