package com.sigclc.backend.Foros.Services;

import java.util.List;

import com.sigclc.backend.Foros.DTOs.ForosResponseDTO;
import com.sigclc.backend.Foros.DTOs.ForosUpdateDTO;

public interface IForosService {

    public ForosResponseDTO crearForo(ForosResponseDTO foro);
    public List<ForosResponseDTO> listarForos();
    public ForosResponseDTO buscarForoPorId(String Id);
    public List<ForosResponseDTO> buscarPorTipo(String tipo);
    public List<ForosResponseDTO> buscarPorCreadorId(String creadorId);
    public ForosResponseDTO actualizarForo(String id, ForosUpdateDTO dto);
    public void eliminarForo(String id);


    
}
