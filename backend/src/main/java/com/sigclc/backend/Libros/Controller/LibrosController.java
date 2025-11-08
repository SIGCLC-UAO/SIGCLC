package com.sigclc.backend.Libros.Controller;


import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigclc.backend.Libros.Services.ILibrosService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sigclc.backend.Libros.DTOs.LibrosCreateDTO;
import com.sigclc.backend.Libros.DTOs.LibrosResponseDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PutMapping;





@RestController
@RequestMapping("/SIGCLC/api/libros")

public class LibrosController {
    @Autowired ILibrosService librosService;
    @PostMapping("/insertar")
    public ResponseEntity<?> crearLibro(@Valid @RequestBody LibrosCreateDTO libros, BindingResult result){
        
        if(result.hasErrors()){
            List<String> errores = result.getAllErrors().stream().map(x -> x.getDefaultMessage()).toList();
            return ResponseEntity.badRequest().body(errores);
        }   
        return  new ResponseEntity<LibrosResponseDTO>(librosService.guardarLibro(libros), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<LibrosResponseDTO>> listarLibros(){
        return new ResponseEntity<List<LibrosResponseDTO>>(librosService.listarLibros(),HttpStatus.OK);
    }
    
    @GetMapping("/libroporId/{id}")
    public ResponseEntity<LibrosResponseDTO>buscarLibroPorId(@PathVariable String id){
        return new ResponseEntity<LibrosResponseDTO>(librosService.buscarLibroPorId(id),HttpStatus.OK);
    }

    @GetMapping("/buscar/titulo/{titulo}")
    public  ResponseEntity<List<LibrosResponseDTO>> buscarPorTitulo(@PathVariable String titulo){
        return new ResponseEntity<List<LibrosResponseDTO>>(librosService.buscarPorTitulo(titulo),HttpStatus.OK);
    }
    
    @GetMapping("/buscar/autor/{autor}")
    public  ResponseEntity<List<LibrosResponseDTO>> buscarPorAutor(@PathVariable String autor){
        return new ResponseEntity<List<LibrosResponseDTO>>(librosService.buscarPorAutor(autor),HttpStatus.OK);
    }

    @GetMapping("/buscar/anioPublicacion/{anioPublicacion}")
    public  ResponseEntity<List<LibrosResponseDTO>> buscarPorAnioPublicacion(@PathVariable Long anioPublicacion){
        return new ResponseEntity<List<LibrosResponseDTO>>(librosService.buscarPorAnioPublicacion(anioPublicacion),HttpStatus.OK);
    }

    @GetMapping("/buscar/fechaSeleccion/{fechaSeleccion}")
    public  ResponseEntity<List<LibrosResponseDTO>> buscarPorFechaSeleccion(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaSeleccion){
        return new ResponseEntity<List<LibrosResponseDTO>>(librosService.buscarPorFechaSeleccion(fechaSeleccion),HttpStatus.OK);
    }
    
    @GetMapping("/buscar/genero/{genero}")
    public  ResponseEntity<List<LibrosResponseDTO>> buscarPorGenero(@PathVariable String genero){
        return new ResponseEntity<List<LibrosResponseDTO>>(librosService.buscarPorGenero(genero),HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<LibrosResponseDTO>actualizarLibro(@PathVariable String id, @RequestBody LibrosCreateDTO dto){
        return new ResponseEntity<LibrosResponseDTO>(librosService.actualizarLibro(id, dto),HttpStatus.OK);
    }
}
