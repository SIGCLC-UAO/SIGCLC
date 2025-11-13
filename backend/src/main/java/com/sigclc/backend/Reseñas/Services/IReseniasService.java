package com.sigclc.backend.Reseñas.Services;

import java.util.List;

import org.bson.types.ObjectId;

import com.sigclc.backend.Reseñas.DTOs.ReseniasCreateDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniasResponseDTO;

public interface IReseniasService {
    ReseniasResponseDTO crearResenia(ReseniasCreateDTO dto);
    List<ReseniasResponseDTO> listarPorLibro(ObjectId libroId);

    // NUEVOS: listar por autor y listar todas
    List<ReseniasResponseDTO> listarPorAutor(ObjectId autorId);
    List<ReseniasResponseDTO> listarTodas();

    void marcarUtil(ObjectId idResenia);
    void comentar(ObjectId idResenia, String utilidad, ObjectId usuarioId, String comentario);
}
