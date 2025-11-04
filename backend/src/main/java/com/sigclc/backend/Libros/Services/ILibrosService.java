package com.sigclc.backend.Libros.Services;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.sigclc.backend.Libros.DTOs.LibrosCreateDTO;
import com.sigclc.backend.Libros.DTOs.LibrosResponseDTO;
import com.sigclc.backend.Libros.Models.LibrosModel;


public interface  ILibrosService {
    public LibrosResponseDTO guardarLibro(LibrosCreateDTO libros);
    public List<LibrosResponseDTO> listarLibros();
    public LibrosModel buscarLibroPorId(ObjectId id);
    public List<LibrosResponseDTO> buscarPorTitulo(String titulo);    
    public List<LibrosResponseDTO> buscarPorAutor(String autor);
    public List<LibrosResponseDTO> buscarPorAnioPublicacion(Long anioPublicacion);
    public List<LibrosResponseDTO> buscarPorFechaSeleccion(Date fechaSeleccion);
    public List<LibrosResponseDTO> buscarPorGenero(String genero);     
}
