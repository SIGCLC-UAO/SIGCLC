package com.sigclc.backend.Usuarios.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.VotoCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.VotoUpdateDTO;
import com.sigclc.backend.Exception.RecursoNoEncontradoException;
import com.sigclc.backend.Usuarios.Mapper;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;
import com.sigclc.backend.Usuarios.Models.UsuariosModel.LibroPropuesto;
import com.sigclc.backend.Usuarios.Models.UsuariosModel.Votacion;
import com.sigclc.backend.Usuarios.Repository.IUsuariosRepository;

@Service
public class VotacionesServiceImp implements IVotacionesService {

    private static final String EN_VOTACION = "En Votacion";

    @Autowired private IUsuariosRepository usuariosRepository;
    @Autowired private IUsuariosService usuariosService;
    @Autowired private Mapper mapper;

    @Override
    public UsuarioResponseDTO agregarVoto(ObjectId usuarioIdPropuesta, String libroIdHex, VotoCreateDTO body) {
        Objects.requireNonNull(usuarioIdPropuesta, "usuarioIdPropuesta no puede ser null");
        Objects.requireNonNull(libroIdHex, "libroIdHex no puede ser null");
        Objects.requireNonNull(body, "VotoCreateDTO no puede ser null");

        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioIdPropuesta); // lanza si no existe

        ObjectId libroId   = parseObjectIdOrBadRequest(libroIdHex, "libroId inválido.");
        ObjectId votanteId = parseObjectIdOrBadRequest(body.getUsuarioId(), "usuarioId inválido.");

        LibroPropuesto prop = findPropuesta(usuario, libroId);
        if (prop == null) throw new RecursoNoEncontradoException("El usuario no tiene propuesta para ese libro.");
        if (!EN_VOTACION.equals(prop.getEstado())) {
            throw new IllegalStateException("La propuesta ya está cerrada. No se pueden agregar votos.");
        }

        if (prop.getVotaciones() == null) prop.setVotaciones(new ArrayList<>());
        for (Votacion vx : prop.getVotaciones()) {
            if (votanteId.equals(vx.getUsuarioId())) {
                throw new IllegalStateException("El usuario ya votó en esta propuesta.");
            }
        }

        Votacion v = new Votacion();
        v.setUsuarioId(votanteId);
        v.setVoto(body.getVoto());            // "Si" | "No"
        v.setFechaVoto(body.getFechaVoto());  // Date

        prop.getVotaciones().add(v);

        // ⬇️ Truco para el analyzer: verificación en la llamada
        usuariosRepository.save(Objects.requireNonNull(usuario, "usuario no puede ser null"));
        return Objects.requireNonNull(mapper.toResponseDTO(usuario), "Mapper.toResponseDTO devolvió null");
    }

    @Override
    public UsuarioResponseDTO actualizarVoto(ObjectId usuarioIdPropuesta, String libroIdHex,
                                             String votanteIdHex, VotoUpdateDTO body) {
        Objects.requireNonNull(usuarioIdPropuesta, "usuarioIdPropuesta no puede ser null");
        Objects.requireNonNull(libroIdHex, "libroIdHex no puede ser null");
        Objects.requireNonNull(votanteIdHex, "votanteIdHex no puede ser null");
        Objects.requireNonNull(body, "VotoUpdateDTO no puede ser null");

        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioIdPropuesta);

        ObjectId libroId   = parseObjectIdOrBadRequest(libroIdHex, "libroId inválido.");
        ObjectId votanteId = parseObjectIdOrBadRequest(votanteIdHex, "votanteId inválido.");

        LibroPropuesto prop = findPropuesta(usuario, libroId);
        if (prop == null) throw new RecursoNoEncontradoException("El usuario no tiene propuesta para ese libro.");
        if (!EN_VOTACION.equals(prop.getEstado())) {
            throw new IllegalStateException("La propuesta ya está cerrada. No se puede modificar el voto.");
        }

        Votacion existente = findVoto(prop, votanteId);
        if (existente == null) throw new RecursoNoEncontradoException("No existe voto de ese usuario en la propuesta.");

        if (body.getVoto() != null)      existente.setVoto(body.getVoto());
        if (body.getFechaVoto() != null) existente.setFechaVoto(body.getFechaVoto());

        usuariosRepository.save(Objects.requireNonNull(usuario, "usuario no puede ser null"));
        return Objects.requireNonNull(mapper.toResponseDTO(usuario), "Mapper.toResponseDTO devolvió null");
    }

    @Override
    public UsuarioResponseDTO eliminarVoto(ObjectId usuarioIdPropuesta, String libroIdHex, String votanteIdHex) {
        Objects.requireNonNull(usuarioIdPropuesta, "usuarioIdPropuesta no puede ser null");
        Objects.requireNonNull(libroIdHex, "libroIdHex no puede ser null");
        Objects.requireNonNull(votanteIdHex, "votanteIdHex no puede ser null");

        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioIdPropuesta);

        ObjectId libroId   = parseObjectIdOrBadRequest(libroIdHex, "libroId inválido.");
        ObjectId votanteId = parseObjectIdOrBadRequest(votanteIdHex, "votanteId inválido.");

        LibroPropuesto prop = findPropuesta(usuario, libroId);
        if (prop == null) throw new RecursoNoEncontradoException("El usuario no tiene propuesta para ese libro.");
        if (!EN_VOTACION.equals(prop.getEstado())) {
            throw new IllegalStateException("La propuesta ya está cerrada. No se puede eliminar el voto.");
        }

        boolean removed = false;
        if (prop.getVotaciones() != null) {
            removed = prop.getVotaciones().removeIf(v -> votanteId.equals(v.getUsuarioId()));
        }
        if (!removed) throw new RecursoNoEncontradoException("No se encontró el voto del usuario en la propuesta.");

        usuariosRepository.save(Objects.requireNonNull(usuario, "usuario no puede ser null"));
        return Objects.requireNonNull(mapper.toResponseDTO(usuario), "Mapper.toResponseDTO devolvió null");
    }

    @Override
    public List<UsuarioResponseDTO.VotacionDTO> listarVotosDePropuesta(ObjectId usuarioIdPropuesta, String libroIdHex) {
        Objects.requireNonNull(usuarioIdPropuesta, "usuarioIdPropuesta no puede ser null");
        Objects.requireNonNull(libroIdHex, "libroIdHex no puede ser null");

        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioIdPropuesta);
        ObjectId libroId = parseObjectIdOrBadRequest(libroIdHex, "libroId inválido.");

        LibroPropuesto prop = findPropuesta(usuario, libroId);
        if (prop == null) throw new RecursoNoEncontradoException("El usuario no tiene propuesta para ese libro.");

        if (prop.getVotaciones() == null || prop.getVotaciones().isEmpty()) {
            return List.of();
        }

        List<UsuarioResponseDTO.VotacionDTO> salida = new ArrayList<>();
        for (Votacion v : prop.getVotaciones()) {
            UsuarioResponseDTO.VotacionDTO d = new UsuarioResponseDTO.VotacionDTO();
            d.setVoto(v.getVoto());
            d.setFechaVoto(v.getFechaVoto());
            d.setUsuarioId(v.getUsuarioId() != null ? v.getUsuarioId().toHexString() : null);
            salida.add(d);
        }
        return salida;
    }

    /* ===== Helpers ===== */
    private LibroPropuesto findPropuesta(UsuariosModel usuario, ObjectId libroId) {
        if (usuario.getLibroPropuesto() == null) return null;
        for (LibroPropuesto lp : usuario.getLibroPropuesto()) {
            if (libroId.equals(lp.getLibroId())) return lp;
        }
        return null;
    }

    private Votacion findVoto(LibroPropuesto prop, ObjectId votanteId) {
        if (prop.getVotaciones() == null) return null;
        for (Votacion v : prop.getVotaciones()) {
            if (votanteId.equals(v.getUsuarioId())) return v;
        }
        return null;
    }

    private ObjectId parseObjectIdOrBadRequest(String hex, String msg) {
        if (hex == null || hex.isBlank()) throw new IllegalArgumentException(msg);
        try { return new ObjectId(hex); } catch (Exception e) { throw new IllegalArgumentException(msg); }
    }
}
