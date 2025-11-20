package com.sigclc.backend.Foros.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.sigclc.backend.Foros.DTOs.ForosCreateDTO;
import com.sigclc.backend.Foros.Services.IForosService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.sigclc.backend.Foros.DTOs.ForosResponseDTO;
import com.sigclc.backend.Foros.DTOs.ForosUpdateDTO;





@RestController
@RequestMapping("/SIGCLC/api/foros")

public class ForosController {
    @Autowired IForosService forosService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearForo(@Valid @RequestBody ForosCreateDTO dto, BindingResult result){
        if(result.hasErrors()){
            List<String> errores = result.getAllErrors().stream().map(x -> x.getDefaultMessage()).toList();
            return ResponseEntity.badRequest().body(errores);
        }
        return new ResponseEntity<>(forosService.crearForo(dto), HttpStatus.OK); 
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ForosResponseDTO>> listarForos(){
        return new ResponseEntity<List<ForosResponseDTO>>(forosService.listarForos(), HttpStatus.OK);
    }
    
    @GetMapping("/buscarForoPorId/{Id}")
    public ResponseEntity<ForosResponseDTO>buscarForoPorId(@PathVariable String Id){
        return new ResponseEntity<>(forosService.buscarForoPorId(Id), HttpStatus.OK);
    }

    @GetMapping("/buscarPorTipo/{tipo}")
    public ResponseEntity<List<ForosResponseDTO>>buscarPorTipo(@PathVariable String tipo){
        return new ResponseEntity<List<ForosResponseDTO>>(forosService.buscarPorTipo(tipo), HttpStatus.OK);
    }

    @GetMapping("/buscarPorCreadorId/{creadorId}")
    public ResponseEntity<List<ForosResponseDTO>>buscarPorCreadorId(@PathVariable String creadorId){
        return new ResponseEntity<List<ForosResponseDTO>>(forosService.buscarPorCreadorId(creadorId), HttpStatus.OK);
    }

    @PatchMapping("/actualizar/{id}")
    public ResponseEntity<ForosResponseDTO> actualizarForo(@PathVariable String id, @RequestBody ForosUpdateDTO dto) {
        return new ResponseEntity<>(forosService.actualizarForo(id, dto), HttpStatus.OK);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ForosResponseDTO>eliminarForo(@PathVariable String id){
        forosService.eliminarForo(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    
    


}
