package com.sigclc.backend.Retos.Controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigclc.backend.Retos.DTOs.RetosCreateDTO;
import com.sigclc.backend.Retos.DTOs.RetosResponseDTO;
import com.sigclc.backend.Retos.DTOs.RetosUpdateDTO;
import com.sigclc.backend.Retos.Services.IRetosService;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/SIGCLC/api/retos")

public class RetosController{

    @Autowired IRetosService retosService;
    @PostMapping("/insertar")
    public ResponseEntity<?> crearReto(@Valid @RequestBody RetosCreateDTO retos, BindingResult result){
        if(result.hasErrors()){
            List<String> errores = result.getAllErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .toList();
            return ResponseEntity.badRequest().body(errores);
        }    
        
        return new ResponseEntity<RetosResponseDTO>(retosService.crearReto(retos), HttpStatus.CREATED);

    }


    @GetMapping("/listar")
    public ResponseEntity<List<RetosResponseDTO>> listarRetos(){
        return new ResponseEntity<List<RetosResponseDTO>>(retosService.listarRetos(),HttpStatus.OK);
    }


    @GetMapping("/retoPorId/{retoId}")
    public ResponseEntity<RetosResponseDTO> obtenerRetoPorId(@PathVariable String retoId){
        return new ResponseEntity<RetosResponseDTO>(retosService.obtenerRetoPorId(retoId),HttpStatus.OK);
    }
    

    @GetMapping("/buscar/titulo/{titulo}")
    public ResponseEntity<List<RetosResponseDTO>> buscarPorTitulo(@PathVariable String titulo){
        return new ResponseEntity<>(retosService.buscarPorTitulo(titulo),HttpStatus.OK);

    }
    
    @GetMapping("/buscar/descripcion/{descripcion}")
    public ResponseEntity<List<RetosResponseDTO>> buscarPorDescripcion(@PathVariable String descripcion){
        return new ResponseEntity<>(retosService.buscarPorDescripcion(descripcion),HttpStatus.OK);

    }



    
    @GetMapping("/buscar/libro/{libroId}")
    public ResponseEntity<List<RetosResponseDTO>> buscarRetosPorLibro(@PathVariable String libroId){
        return new ResponseEntity<>(retosService.buscarRetosPorLibro(libroId),HttpStatus.OK);
    }    


    @GetMapping("/listar/libros/{retoId}")
    public ResponseEntity<List<String>> listarLibrosEnRetos(@PathVariable String retoId){
        return new ResponseEntity<>(retosService.listarLibrosEnRetos(retoId), HttpStatus.OK);
    }

    @GetMapping("/listar/usuario/{usuarioId}")
    public ResponseEntity<List<String>> listarRetosPorUsuario(@PathVariable String usuarioId){
        return new ResponseEntity<>(retosService.listarRetosPorUsuario(usuarioId),HttpStatus.OK);
    }
    
    @GetMapping("/contar/participantes/{retoId}")
    public ResponseEntity<Integer> contarParticipantes(@PathVariable String retoId){
        return new ResponseEntity<>(retosService.contarParticipantes(retoId),HttpStatus.OK);
    }

    @GetMapping("/listar/usuarioInscrito/{retoId}")
    public ResponseEntity<List<String>> listarUsuariosInscritos(@PathVariable String retoId){
        return new ResponseEntity<>(retosService.listarUsuariosInscritos(retoId),HttpStatus.OK);
    }

    @GetMapping("/buscar/fechaInicio/{fechaInicio}")
    public ResponseEntity<List<RetosResponseDTO>> buscarRetosPorFechaInicio(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaInicio){
        List<RetosResponseDTO> retos = retosService.buscarRetosPorFechaInicio(fechaInicio);
        return new ResponseEntity<>(retos, HttpStatus.OK);
    }

    @GetMapping("/buscar/fechaFin/{fechaFin}")
    public ResponseEntity<List<RetosResponseDTO>> buscarRetosPorFechaFin(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaFin){
        List<RetosResponseDTO> retos = retosService.buscarRetosPorFechaFin(fechaFin);
        return new ResponseEntity<>(retos, HttpStatus.OK);
    }



    @PutMapping("/actualizar/{id}")
    public ResponseEntity<RetosResponseDTO> actualizarReto(@PathVariable String id, @RequestBody RetosUpdateDTO reto){
        return new ResponseEntity<>(retosService.actualizarReto(id, reto),HttpStatus.OK);
    }

    @DeleteMapping("/eliminarPorId/{id}")
    public ResponseEntity<RetosResponseDTO>eliminarRetoPorId(@PathVariable String id){
        retosService.eliminarRetoPorId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        
    }

    

    
}


