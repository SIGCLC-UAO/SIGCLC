package com.sigclc.backend.Reseñas.Controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigclc.backend.Reseñas.DTOs.ReseniasCreateDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniasResponseDTO;
import com.sigclc.backend.Reseñas.Services.IReseniasService;

@RestController
@RequestMapping("/SIGCLC/api/resenias")
@CrossOrigin(origins = "*")
public class ReseniasController {

    @Autowired
    private IReseniasService service;

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody @Validated ReseniasCreateDTO dto, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errores = result.getAllErrors()
                    .stream().map(x -> x.getDefaultMessage()).toList();
            return ResponseEntity.badRequest().body(errores);
        }

        return new ResponseEntity<>(service.crearResenia(dto), HttpStatus.CREATED);
    }

    @GetMapping("/libro/{idLibro}")
    public ResponseEntity<List<ReseniasResponseDTO>> listarPorLibro(@PathVariable ObjectId idLibro) {
        return ResponseEntity.ok(service.listarPorLibro(idLibro));
    }

    @GetMapping("/autor/{idAutor}")
    public ResponseEntity<List<ReseniasResponseDTO>> listarPorAutor(@PathVariable ObjectId idAutor) {
        return ResponseEntity.ok(service.listarPorAutor(idAutor));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ReseniasResponseDTO>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @PutMapping("/marcar-util/{idResenia}")
    public ResponseEntity<String> marcarUtil(@PathVariable ObjectId idResenia) {
        service.marcarUtil(idResenia);
        return ResponseEntity.ok("Reseña marcada como útil");
    }

    @PatchMapping("/comentar/{idResenia}")
    public ResponseEntity<String> comentar(
            @PathVariable ObjectId idResenia,
            @RequestBody ComentarioResenia comentarioDTO) {

        if (comentarioDTO.getUtilidad() == null ||
            comentarioDTO.getComentario() == null ||
            comentarioDTO.getUsuarioId() == null) {
            return ResponseEntity.badRequest().body("Todos los campos del comentario son obligatorios.");
        }

        service.comentar(
                idResenia,
                comentarioDTO.getUtilidad(),
                comentarioDTO.getUsuarioId(),
                comentarioDTO.getComentario()
        );

        return ResponseEntity.ok("Comentario agregado correctamente");
    }

    public static class ComentarioResenia {
        private String utilidad;
        private ObjectId usuarioId;
        private String comentario;

        public String getUtilidad() { return utilidad; }
        public void setUtilidad(String utilidad) { this.utilidad = utilidad; }

        public ObjectId getUsuarioId() { return usuarioId; }
        public void setUsuarioId(ObjectId usuarioId) { this.usuarioId = usuarioId; }

        public String getComentario() { return comentario; }
        public void setComentario(String comentario) { this.comentario = comentario; }
    }
}
