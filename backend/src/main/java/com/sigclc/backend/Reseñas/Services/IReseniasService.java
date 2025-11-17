package com.sigclc.backend.Reseñas.Services;

import java.util.List;

import com.sigclc.backend.Reseñas.DTOs.ComentarioCreateDTO;
import com.sigclc.backend.Reseñas.DTOs.FiltroReseniasDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniaDetailDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniaListViewDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniaUpdateDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniasCreateDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniasResponseDTO;
import com.sigclc.backend.Reseñas.DTOs.StatsReseniasDTO;
import com.sigclc.backend.Reseñas.DTOs.TopReviewerDTO;

public interface IReseniasService {

    ReseniasResponseDTO crearResenia(ReseniasCreateDTO dto);
    ReseniasResponseDTO actualizarResenia(String id, ReseniaUpdateDTO dto, String usuarioAutenticadoId);
    void eliminarResenia(String id, String usuarioAutenticadoId);
    
    List<ReseniaListViewDTO> listarConFiltros(FiltroReseniasDTO filtros);
    ReseniaDetailDTO obtenerDetalle(String id, String usuarioAutenticadoId);
    List<ReseniaListViewDTO> misResenias(String usuarioId);
    
    void agregarComentario(String idResenia, ComentarioCreateDTO dto);
    void eliminarComentario(String idResenia, String usuarioId, String textoComentario);
    
    StatsReseniasDTO obtenerEstadisticasPorLibro(String libroId);
    List<TopReviewerDTO> obtenerTopReviewers(int limite);
    
    List<ReseniasResponseDTO> listarPorLibro(String libroId);
    List<ReseniasResponseDTO> listarPorAutor(String autorId);
    List<ReseniasResponseDTO> listarTodas();
    void marcarUtil(String idResenia);
    void comentar(String idResenia, String utilidad, String usuarioId, String comentario);
}