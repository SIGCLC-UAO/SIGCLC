package com.sigclc.backend.Usuarios.Services;

import java.util.List;

import org.bson.types.ObjectId;

import com.sigclc.backend.Usuarios.DTOs.TopLibroVotosDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioPropuestaDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.VotacionCreateDTO;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;

public interface IUsuariosService {

    // 1) Crear usuario
    UsuarioResponseDTO crearUsuario(UsuarioCreateDTO usuario);

    // 2) Obtener usuario por id
    UsuariosModel buscarUsuarioPorId(ObjectId id);

    // 3) Listar usuarios (simple)
    List<UsuarioResponseDTO> listarUsuarios();

    // 5) Agregar propuesta
    UsuarioResponseDTO agregarPropuesta(ObjectId usuarioId, ObjectId libroId, String estado);

    // 6) Cambiar estado de una propuesta (solo admin/moderador)
    UsuarioResponseDTO cambiarEstadoPropuesta(ObjectId actorId, ObjectId usuarioId, ObjectId libroId, String nuevoEstado);

    // 7) Eliminar propuesta
    UsuarioResponseDTO eliminarPropuesta(ObjectId usuarioId, ObjectId libroId);

    // 8) (fusión 9+14) Listar propuestas detalladas del usuario (con libro y métricas; oculta métricas si "En Votacion")
    List<UsuarioPropuestaDTO> propuestasDetalladasDeUsuario(ObjectId usuarioId);

    // 10) Agregar votación (solo si estado = "En Votacion")
    UsuarioResponseDTO agregarVotacion(ObjectId usuarioIdDueno, ObjectId libroId, VotacionCreateDTO votoDTO);

    // 11) Actualizar votación (solo si estado = "En Votacion")
    UsuarioResponseDTO actualizarVotacion(ObjectId usuarioIdDueno, ObjectId libroId, ObjectId votanteId, String nuevoVoto);

    // 12) Eliminar votación (solo si estado = "En Votacion")
    UsuarioResponseDTO eliminarVotacion(ObjectId usuarioIdDueno, ObjectId libroId, ObjectId votanteId);

    // 13) Métricas de una propuesta puntual
    UsuarioPropuestaDTO metricasDePropuesta(ObjectId usuarioIdDueno, ObjectId libroId);

    // 15) Top global (libro más votado)
    List<TopLibroVotosDTO> topLibroMasVotado();

    // 17) Propuestas para un libro (en toda la BD)
    List<UsuarioPropuestaDTO> buscarPropuestasPorLibro(ObjectId libroId);
}
