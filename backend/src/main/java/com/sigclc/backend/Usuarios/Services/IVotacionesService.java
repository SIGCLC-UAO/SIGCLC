package com.sigclc.backend.Usuarios.Services;

import java.util.List;

import org.bson.types.ObjectId;

import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;

public interface IVotacionesService {

    // Agregar un voto a la propuesta (de un usuario) sobre un libro
    UsuarioResponseDTO agregarVoto(
        ObjectId usuarioIdPropuesta,   // usuario dueño de la propuesta
        ObjectId libroId,              // libro propuesto
        ObjectId votanteId,            // usuario que vota
        String   voto,                 // "Si" | "No"
        String   fechaVoto             // ISO-8601 sugerido; tu modelo usa String
    );

    // Actualizar el voto de un votante en esa propuesta
    UsuarioResponseDTO actualizarVoto(
        ObjectId usuarioIdPropuesta,
        ObjectId libroId,
        ObjectId votanteId,
        String   nuevoVoto,
        String   nuevaFechaVoto
    );

    // Eliminar el voto de un votante en esa propuesta
    UsuarioResponseDTO eliminarVoto(
        ObjectId usuarioIdPropuesta,
        ObjectId libroId,
        ObjectId votanteId
    );

    // Listar votos de una propuesta (si la propuesta está "En Votacion" puedes decidir ocultarlos en controlador)
    List<UsuarioResponseDTO.VotacionDTO> listarVotosDePropuesta(
        ObjectId usuarioIdPropuesta,
        ObjectId libroId
    );
}
