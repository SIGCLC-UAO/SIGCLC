package com.sigclc.backend.Reuniones.DTOs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @AllArgsConstructor @NoArgsConstructor
public class ArchivoUploadResponseDTO {
    private List<ArchivoMetaSafeResponseDTO> archivos;
    private List<String> extensionesAdjuntas; // .png/.ppt/.pdf/.docx
}
