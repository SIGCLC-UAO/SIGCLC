package com.sigclc.backend.Usuarios.Services;

import java.util.List;

import org.bson.types.ObjectId;

import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.VotoCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.VotoUpdateDTO;

public interface IVotacionesService {

    UsuarioResponseDTO agregarVoto(ObjectId usuarioIdPropuesta, String libroIdHex, VotoCreateDTO body);

    UsuarioResponseDTO actualizarVoto(ObjectId usuarioIdPropuesta, String libroIdHex, String votanteIdHex, VotoUpdateDTO body);

    UsuarioResponseDTO eliminarVoto(ObjectId usuarioIdPropuesta, String libroIdHex, String votanteIdHex);

    List<UsuarioResponseDTO.VotacionDTO> listarVotosDePropuesta(ObjectId usuarioIdPropuesta, String libroIdHex);
}
