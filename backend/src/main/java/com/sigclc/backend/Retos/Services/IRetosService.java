package com.sigclc.backend.Retos.Services;

import java.util.Date;
import java.util.List;

import com.sigclc.backend.Retos.DTOs.RetosCreateDTO;
import com.sigclc.backend.Retos.DTOs.RetosResponseDTO;
import com.sigclc.backend.Retos.DTOs.RetosUpdateDTO;


public interface IRetosService{
    
    RetosResponseDTO crearReto(RetosCreateDTO dto);
    RetosResponseDTO obtenerRetoPorId(String id);
    List<RetosResponseDTO> listarRetos();
    RetosResponseDTO actualizarReto(String id, RetosUpdateDTO dto);    
    

    List<RetosResponseDTO> buscarPorTitulo(String titulo);
    List<RetosResponseDTO> buscarPorDescripcion(String descripcion);
    List<RetosResponseDTO> buscarRetosPorFechaInicio(Date fechaInicio);
    List<RetosResponseDTO> buscarRetosPorFechaFin(Date fechaFin);
    List<RetosResponseDTO> buscarRetosPorLibro(String libroId);
    List<String> listarLibrosEnRetos(String idReto);
    List<String> listarRetosPorUsuario(String usuarioId);
    Integer contarParticipantes (String idReto);
    List<String> listarUsuariosInscritos(String idReto);
    void eliminarRetoPorId(String id);

}


