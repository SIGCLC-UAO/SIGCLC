package com.sigclc.backend.Usuarios.Services;

import java.util.List;

import org.bson.types.ObjectId;

import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.VotoCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.VotoUpdateDTO;

public interface IVotacionesService {

    // Agregar un voto a una propuesta de libro
    UsuarioResponseDTO agregarVoto(
            ObjectId usuarioIdPropuesta,
            String libroIdHex,
            VotoCreateDTO body);

    // Actualizar un voto existente
    UsuarioResponseDTO actualizarVoto(
            ObjectId usuarioIdPropuesta,
            String libroIdHex,
            String votanteIdHex,
            VotoUpdateDTO body);

    // Eliminar un voto
    UsuarioResponseDTO eliminarVoto(
            ObjectId usuarioIdPropuesta,
            String libroIdHex,
            String votanteIdHex);

    // Listar todos los votos de una propuesta
    List<UsuarioResponseDTO.VotacionDTO> listarVotosDePropuesta(
            ObjectId usuarioIdPropuesta,
            String libroIdHex);
}
