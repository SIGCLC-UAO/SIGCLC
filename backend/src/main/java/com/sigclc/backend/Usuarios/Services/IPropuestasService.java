package com.sigclc.backend.Usuarios.Services;

import java.util.List;

import org.bson.types.ObjectId;

import com.sigclc.backend.Usuarios.DTOs.UsuarioPropuestaDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;

public interface IPropuestasService {

    // Agrega una propuesta (por defecto la dejamos "En Votacion" si viene null)
    UsuarioResponseDTO agregarPropuesta(ObjectId usuarioId, ObjectId libroId, String estado);

    // Cambia el estado de una propuesta existente
    UsuarioResponseDTO cambiarEstadoPropuesta(ObjectId usuarioId, ObjectId libroId, String nuevoEstado);

    // Elimina una propuesta del array
    UsuarioResponseDTO eliminarPropuesta(ObjectId usuarioId, ObjectId libroId);

    // Devuelve propuestas del usuario con métricas locales (votosSi/No/Total).
    // Si una propuesta está "En Votacion", no devuelve las métricas (quedan null).
    List<UsuarioPropuestaDTO> propuestasDetalladasDeUsuario(ObjectId usuarioId);
}
