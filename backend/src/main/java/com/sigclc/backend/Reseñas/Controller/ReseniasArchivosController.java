package com.sigclc.backend.Reseñas.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sigclc.backend.Reseñas.DTOs.ArchivoReseniaDeleteRequestDTO;
import com.sigclc.backend.Reseñas.DTOs.ArchivoReseniaMetaDTO;
import com.sigclc.backend.Reseñas.Services.IReseniasArchivosService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/SIGCLC/api/resenias/{reseniaId}/archivos")
@CrossOrigin(origins = "*")
public class ReseniasArchivosController {

    @Autowired
    private IReseniasArchivosService archivosService;

    @GetMapping
    public ResponseEntity<List<ArchivoReseniaMetaDTO>> listar(@PathVariable String reseniaId) {
        return ResponseEntity.ok(archivosService.listarArchivos(reseniaId));
    }

    
    @PostMapping
    public ResponseEntity<?> subir(
            @PathVariable String reseniaId,
            @RequestPart("archivos") List<MultipartFile> archivos) {

        return ResponseEntity.ok(archivosService.subirArchivos(reseniaId, archivos));
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> descargar(
            @PathVariable String reseniaId,
            @PathVariable String filename) {

        Resource recurso = archivosService.obtenerRecursoArchivo(reseniaId, filename);

        String contentType = "application/octet-stream";
        String lowerName = filename.toLowerCase();

        if (lowerName.endsWith(".pdf")) {
            contentType = "application/pdf";
        } else if (lowerName.endsWith(".png")) {
            contentType = "image/png";
        } else if (lowerName.endsWith(".docx")) {
            contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(recurso);
    }

    @DeleteMapping
    public ResponseEntity<?> eliminar(
            @PathVariable String reseniaId,
            @Valid @RequestBody ArchivoReseniaDeleteRequestDTO dto) {

        return ResponseEntity.ok(archivosService.eliminarArchivo(reseniaId, dto.getFilename()));
    }
}