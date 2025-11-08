package com.sigclc.backend.Libros.Services;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Libros.DTOs.LibrosCreateDTO;
import com.sigclc.backend.Libros.DTOs.LibrosResponseDTO;
import com.sigclc.backend.Libros.Mapper.LibrosMapper;
import com.sigclc.backend.Libros.Models.LibrosModel;
import com.sigclc.backend.Libros.Repository.ILibrosRepository;
import com.sigclc.backend.Usuarios.Exception.RecursoNoEncontradoException;

@Service

public class LibrosServiceImp implements ILibrosService{
    @Autowired ILibrosRepository librosRepository;
    @Autowired LibrosMapper librosMapper;

    @Override
    public LibrosResponseDTO guardarLibro(LibrosCreateDTO libro) {
        if (libro == null){
            throw new IllegalArgumentException("El libro no puede ser nulo");
        }

        LibrosModel librosModel = librosMapper.toModel(libro);
        
        librosRepository.save(librosModel);

        return librosMapper.toResponseDTO(librosModel);
    }

    @Override
    public List<LibrosResponseDTO> listarLibros() {
        return  librosMapper.toResponseDTOList(librosRepository.findAll());
    }

    @Override
    public LibrosResponseDTO buscarLibroPorId(String idLibro) {
        if (idLibro == null){
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        ObjectId objectId;
        try {
            objectId = new ObjectId(idLibro);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El formato del id no es válido"); 
        }

        LibrosModel librosModel = librosRepository.findById(objectId)
        .orElseThrow(()-> new RecursoNoEncontradoException(
            "El libro con id "+idLibro+" no se ha encontrado o está mal escrito"
        ));
        return  librosMapper.toResponseDTO(librosModel); 

    }     

    @Override
    public List<LibrosResponseDTO> buscarPorTitulo(String titulo) {
        List<LibrosModel>  libros = librosRepository.buscarPorTitulo(titulo);
        return librosMapper.toResponseDTOList(libros);
    }

    @Override
    public List<LibrosResponseDTO> buscarPorAutor(String autor) {
        List<LibrosModel>  libros = librosRepository.buscarPorAutor(autor);
        return librosMapper.toResponseDTOList(libros);
    }

    @Override
    public List<LibrosResponseDTO> buscarPorAnioPublicacion(Long anioPublicacion) {
        List<LibrosModel>  libros = librosRepository.buscarPorAnioPublicacion(anioPublicacion);
        return librosMapper.toResponseDTOList(libros);
    }

    @Override
    public List<LibrosResponseDTO> buscarPorFechaSeleccion(Date fechaSeleccion) {
       List<LibrosModel>  libros = librosRepository.buscarPorFechaSeleccion(fechaSeleccion);
        return librosMapper.toResponseDTOList(libros);
    }

    @Override
    public List<LibrosResponseDTO> buscarPorGenero(String genero) {
       List<LibrosModel>  libros = librosRepository.buscarPorGenero(genero);
        return librosMapper.toResponseDTOList(libros);
    }

     
    @Override
    public LibrosResponseDTO actualizarLibro(String idLibro, LibrosCreateDTO dto){
        if (idLibro == null){
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        ObjectId objectId;
        try {
            objectId = new ObjectId(idLibro);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El formato del id no es válido"); 
        }

        LibrosModel libroExistente = librosRepository.findById(objectId)
        .orElseThrow(()-> new RecursoNoEncontradoException(
            "El libro con id "+idLibro+" no se ha encontrado o está mal escrito"
        ));

        librosMapper.actualizarDTO(dto, libroExistente);

        LibrosModel actualizado = librosRepository.save(libroExistente);

        return  librosMapper.toResponseDTO(actualizado);        
    }

}
