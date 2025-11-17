package com.sigclc.backend.Libros.Services;

import java.util.Date;
import java.util.List;

import com.sigclc.backend.Libros.DTOs.LibrosCreateDTO;
import com.sigclc.backend.Libros.DTOs.LibrosResponseDTO;
import com.sigclc.backend.Libros.DTOs.LibrosUpdateDTO;


public interface  ILibrosService {
    public LibrosResponseDTO guardarLibro(LibrosCreateDTO libros);
    public List<LibrosResponseDTO> listarLibros();
    public LibrosResponseDTO  buscarLibroPorId( String id);
    public List<LibrosResponseDTO> buscarPorTitulo(String titulo);    
    public List<LibrosResponseDTO> buscarPorAutor(String autor);
    public List<LibrosResponseDTO> buscarPorAnioPublicacion(Long anioPublicacion);
    public List<LibrosResponseDTO> buscarPorFechaSeleccion(Date fechaSeleccion);
    public List<LibrosResponseDTO> buscarPorGenero(String genero);     
    public LibrosResponseDTO actualizarLibro(String id, LibrosUpdateDTO  dto);
    public void eliminarLibroPorId(String id);
}
