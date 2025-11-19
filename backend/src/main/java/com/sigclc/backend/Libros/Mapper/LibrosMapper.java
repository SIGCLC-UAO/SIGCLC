package com.sigclc.backend.Libros.Mapper;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;


import com.sigclc.backend.Libros.DTOs.LibrosCreateDTO;
import com.sigclc.backend.Libros.DTOs.LibrosResponseDTO;
import com.sigclc.backend.Libros.DTOs.LibrosUpdateDTO;
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



    public void UpdateDTO(LibrosUpdateDTO dto, LibrosModel model) {
        if (dto == null || model == null) return;

        if (dto.getTitulo() != null) model.setTitulo(dto.getTitulo());
        if (dto.getAutor() != null) model.setAutor(dto.getAutor());
        if (dto.getAnioPublicacion() != null) model.setAnioPublicacion(dto.getAnioPublicacion());
        if (dto.getEstado() != null) model.setEstado(dto.getEstado());
        if (dto.getSinopsis() != null) model.setSinopsis(dto.getSinopsis());
        if (dto.getPortada() != null) model.setPortada(dto.getPortada());
        if (dto.getGenero() != null) model.setGenero(dto.getGenero());
        if (dto.getFechaSeleccion() != null) model.setFechaSeleccion(dto.getFechaSeleccion());
    }

    public List<LibrosResponseDTO> toResponseDTOList(List<LibrosModel> libros) {
        if(libros == null) return null;

        return libros.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

}