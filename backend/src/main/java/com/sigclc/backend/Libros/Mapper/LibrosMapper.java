package com.sigclc.backend.Libros.Mapper;


import java.util.List;

import org.springframework.stereotype.Component;

import com.sigclc.backend.Libros.DTOs.LibrosCreateDTO;
import com.sigclc.backend.Libros.DTOs.LibrosResponseDTO;
import com.sigclc.backend.Libros.Models.LibrosModel;

@Component

public class LibrosMapper {
    
    public LibrosModel toModel(LibrosCreateDTO dto){
        if (dto == null) return null;

        LibrosModel model = new LibrosModel();
        model.setTitulo(dto.getTitulo());
        model.setAutor(dto.getAutor());
        model.setAnioPublicacion(dto.getAnioPublicacion());
        model.setEstado(dto.getEstado());
        model.setSinopsis(dto.getSinopsis());
        model.setPortada(dto.getPortada());
        model.setGenero(dto.getGenero());
        model.setFechaSeleccion(dto.getFechaSeleccion());


        return model;
 
    }



    public LibrosResponseDTO toResponseDTO(LibrosModel model){
        if (model == null) return null;

        LibrosResponseDTO dto = new LibrosResponseDTO();
        dto.setId(model.getIdAsString());
        dto.setTitulo(model.getTitulo());
        dto.setAutor(model.getAutor());
        dto.setAnioPublicacion(model.getAnioPublicacion());
        dto.setEstado(model.getEstado());
        dto.setFechaSeleccion(model.getFechaSeleccion());
        dto.setSinopsis(model.getSinopsis());
        dto.setPortada(model.getPortada());
        dto.setGenero(model.getGenero());

        return dto;
    }


    public void actualizarDTO(LibrosCreateDTO dto, LibrosModel model){
        if (dto == null || model == null) return;

        model.setTitulo(dto.getTitulo());
        model.setAutor(dto.getAutor());
        model.setAnioPublicacion(dto.getAnioPublicacion());
        model.setEstado(dto.getEstado());
        model.setSinopsis(dto.getSinopsis());
        model.setPortada(dto.getPortada());
        model.setGenero(dto.getGenero());
        model.setFechaSeleccion(dto.getFechaSeleccion()); 
    }

    public List<LibrosResponseDTO> toResponseDTOList(List<LibrosModel> models){
        return models.stream()
        .map(this::toResponseDTO).toList();
    }
}