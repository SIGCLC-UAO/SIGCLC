package com.sigclc.backend.Comentarios.Controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigclc.backend.Comentarios.DTOs.ComentariosCreateDTO;
import com.sigclc.backend.Comentarios.DTOs.ComentariosResponseDTO;
import com.sigclc.backend.Comentarios.DTOs.ComentariosUpdateDTO;
import com.sigclc.backend.Comentarios.Services.IComentariosService;


import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/SIGCLC/api/comentarios")


public class ComentariosController {

    @Autowired IComentariosService comentariosService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearComentario(@Valid @RequestBody ComentariosCreateDTO dto, BindingResult result){
        if(result.hasErrors()){
            List<String> errores = result.getAllErrors().stream().map(x-> x.getDefaultMessage()).toList();
            return ResponseEntity.badRequest().body(errores);
        }
        return new ResponseEntity<>(comentariosService.crearComentarios(dto),HttpStatus.OK);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ComentariosResponseDTO>> listarComentarios(){
        return new ResponseEntity<List<ComentariosResponseDTO>>(comentariosService.listarComentarios(), HttpStatus.OK);
    }

    @GetMapping("/buscarPorId/{id}")
    public ResponseEntity<ComentariosResponseDTO>buscarPorId(@PathVariable String id){
        return new ResponseEntity<ComentariosResponseDTO>(comentariosService.buscarPorId(id), HttpStatus.OK);
    }

    @GetMapping("/buscarPorForoId/{foroId}")
    public ResponseEntity<List<ComentariosResponseDTO>>buscarPorForoId(@PathVariable String foroId){
        return new ResponseEntity<List<ComentariosResponseDTO>>(comentariosService.buscarPorForoId(foroId), HttpStatus.OK);
    }

    @GetMapping("/fecha/{fecha}")
    public  ResponseEntity<List<ComentariosResponseDTO>> buscarPorFecha(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fecha){
        return new ResponseEntity<List<ComentariosResponseDTO>>(comentariosService.buscarPorFecha(fecha),HttpStatus.OK);
    }

    @GetMapping("/usuarioid/{usuarioId}")
    public ResponseEntity<List<ComentariosResponseDTO>>buscarPorUsuario(@PathVariable String usuarioId){
        return new ResponseEntity<List<ComentariosResponseDTO>>(comentariosService.buscarPorUsuario(usuarioId), HttpStatus.OK);
    }
    @GetMapping("/respuesta/{comentarioId}")
    public ResponseEntity<List<ComentariosResponseDTO>>buscarPorComentarioId(@PathVariable String comentarioId){
        return new ResponseEntity<List<ComentariosResponseDTO>>(comentariosService.buscarPorComentarioId(comentarioId), HttpStatus.OK);
    }

    @PatchMapping("/actualizar/{id}")
    public ResponseEntity<ComentariosResponseDTO> actualizarComentario(@PathVariable String id, @RequestBody ComentariosUpdateDTO dto) {
        ComentariosResponseDTO comentarioActualizado = comentariosService.actualizarComentario(id, dto);
        return new ResponseEntity<>(comentarioActualizado, HttpStatus.OK);
    }
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ComentariosResponseDTO>eliminarComentario(@PathVariable String id){
        comentariosService.eliminarComentario(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
