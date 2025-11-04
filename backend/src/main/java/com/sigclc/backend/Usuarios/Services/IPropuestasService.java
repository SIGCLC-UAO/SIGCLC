package com.sigclc.backend.Usuarios.Services;

import java.util.List;

import org.bson.types.ObjectId;

import com.sigclc.backend.Usuarios.DTOs.PropuestaCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.PropuestaCreateResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.PropuestaEstadoUpdateDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;

public interface IPropuestasService {

    UsuarioResponseDTO agregarPropuesta(ObjectId usuarioId, PropuestaCreateDTO body);

    UsuarioResponseDTO cambiarEstadoPropuesta(ObjectId usuarioId, String libroIdHex, PropuestaEstadoUpdateDTO body);

    UsuarioResponseDTO eliminarPropuesta(ObjectId usuarioId, String libroIdHex);

    List<PropuestaCreateResponseDTO> listarPropuestasDeUsuario(ObjectId usuarioId);
}
