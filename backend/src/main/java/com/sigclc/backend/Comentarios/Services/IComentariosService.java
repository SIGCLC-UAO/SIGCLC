package com.sigclc.backend.Comentarios.Services;

import java.util.Date;
import java.util.List;

import com.sigclc.backend.Comentarios.DTOs.ComentariosCreateDTO;
import com.sigclc.backend.Comentarios.DTOs.ComentariosResponseDTO;
import com.sigclc.backend.Comentarios.DTOs.ComentariosUpdateDTO;

public interface IComentariosService {

    public ComentariosResponseDTO crearComentarios(ComentariosCreateDTO comentarios);
    public List<ComentariosResponseDTO> listarComentarios();
    public ComentariosResponseDTO buscarPorId(String Id);
    public List<ComentariosResponseDTO> buscarPorForoId(String foroId);
    public List<ComentariosResponseDTO> buscarPorFecha (Date fecha);
    public List<ComentariosResponseDTO> buscarPorUsuario (String usuarioId);
    public List<ComentariosResponseDTO> buscarPorComentarioId (String comentarioId);
    public ComentariosResponseDTO actualizarComentario(String id, ComentariosUpdateDTO dto);
    public void eliminarComentario (String id);
    
}
