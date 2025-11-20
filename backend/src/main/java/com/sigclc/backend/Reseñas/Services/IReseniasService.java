package com.sigclc.backend.Reseñas.Services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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

    // -------------------------------------------------------------------------
    // CREACIÓN Y EDICIÓN
    // -------------------------------------------------------------------------

    ReseniasResponseDTO crearResenia(ReseniasCreateDTO dto);

    /**
     * Crea una reseña con archivos adjuntos almacenados en GridFS.
     */
    ReseniasResponseDTO crearReseniaConArchivos(ReseniasCreateDTO dto, List<MultipartFile> archivosSubidos);

    ReseniasResponseDTO actualizarResenia(String id, ReseniaUpdateDTO dto, String usuarioAutenticadoId);

    void eliminarResenia(String id, String usuarioAutenticadoId);


    // -------------------------------------------------------------------------
    // LISTADOS Y CONSULTAS
    // -------------------------------------------------------------------------

    List<ReseniaListViewDTO> listarConFiltros(FiltroReseniasDTO filtros);

    ReseniaDetailDTO obtenerDetalle(String id, String usuarioAutenticadoId);

    List<ReseniaListViewDTO> misResenias(String usuarioId);

    List<ReseniasResponseDTO> listarPorLibro(String libroId);

    List<ReseniasResponseDTO> listarPorAutor(String autorId);

    List<ReseniasResponseDTO> listarTodas();


    // -------------------------------------------------------------------------
    // COMENTARIOS
    // -------------------------------------------------------------------------

    void agregarComentario(String idResenia, ComentarioCreateDTO dto);

    void eliminarComentario(String idResenia, String usuarioId, String textoComentario);


    // -------------------------------------------------------------------------
    // ESTADÍSTICAS
    // -------------------------------------------------------------------------

    StatsReseniasDTO obtenerEstadisticasPorLibro(String libroId);

    List<TopReviewerDTO> obtenerTopReviewers(int limite);


    // -------------------------------------------------------------------------
    // UTILIDAD / INTERACCIÓN
    // -------------------------------------------------------------------------

    void marcarUtil(String idResenia);

    void comentar(String idResenia, String utilidad, String usuarioId, String comentario);


}
