package com.sigclc.backend.Usuarios.Services;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Usuarios.Exception.RecursoNoEncontradoException;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;
import com.sigclc.backend.Usuarios.Repository.IUsuariosRepository;
import com.sigclc.backend.Usuarios.Mapper;

@Service
public class VotacionesServiceImp implements IVotacionesService {

    @Autowired private IUsuariosRepository usuariosRepository;
    @Autowired private IUsuariosService usuariosService; // reutilizamos buscarUsuarioPorId
    @Autowired private Mapper mapper;

    @Override
    public UsuarioResponseDTO agregarVoto(
            ObjectId usuarioIdPropuesta,
            ObjectId libroId,
            ObjectId votanteId,
            String voto,
            String fechaVoto) {

        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioIdPropuesta);

        // localizar la propuesta
        UsuariosModel.LibroPropuesto propuesta = findPropuesta(usuario, libroId);
        if (propuesta == null) {
            throw new RecursoNoEncontradoException("El usuario no tiene propuesta para el libro: " + libroId);
        }

        if (propuesta.getVotaciones() == null) {
            propuesta.setVotaciones(new ArrayList<>());
        }

        // crear voto embebido (clase interna NO estática)
        UsuariosModel.Votacion v = usuario.new Votacion();
        v.setUsuarioId(votanteId);
        v.setVoto(voto);
        v.setFechaVoto(fechaVoto);

        propuesta.getVotaciones().add(v);

        usuariosRepository.save(usuario);
        return mapper.toResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO actualizarVoto(
            ObjectId usuarioIdPropuesta,
            ObjectId libroId,
            ObjectId votanteId,
            String nuevoVoto,
            String nuevaFechaVoto) {

        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioIdPropuesta);

        UsuariosModel.LibroPropuesto propuesta = findPropuesta(usuario, libroId);
        if (propuesta == null) {
            throw new RecursoNoEncontradoException("El usuario no tiene propuesta para el libro: " + libroId);
        }

        UsuariosModel.Votacion existente = findVotoDeVotante(propuesta, votanteId);
        if (existente == null) {
            throw new RecursoNoEncontradoException("No existe voto del usuario: " + votanteId);
        }

        if (nuevoVoto != null)      existente.setVoto(nuevoVoto);
        if (nuevaFechaVoto != null) existente.setFechaVoto(nuevaFechaVoto);

        usuariosRepository.save(usuario);
        return mapper.toResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO eliminarVoto(
            ObjectId usuarioIdPropuesta,
            ObjectId libroId,
            ObjectId votanteId) {

        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioIdPropuesta);

        UsuariosModel.LibroPropuesto propuesta = findPropuesta(usuario, libroId);
        if (propuesta == null) {
            throw new RecursoNoEncontradoException("El usuario no tiene propuesta para el libro: " + libroId);
        }

        boolean removed = false;
        if (propuesta.getVotaciones() != null) {
            removed = propuesta.getVotaciones()
                               .removeIf(v -> v.getUsuarioId() != null && v.getUsuarioId().equals(votanteId));
        }

        if (!removed) {
            throw new RecursoNoEncontradoException("No se encontró el voto del usuario: " + votanteId);
        }

        usuariosRepository.save(usuario);
        return mapper.toResponseDTO(usuario);
    }

    @Override
    public List<UsuarioResponseDTO.VotacionDTO> listarVotosDePropuesta(
            ObjectId usuarioIdPropuesta,
            ObjectId libroId) {

        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioIdPropuesta);

        UsuariosModel.LibroPropuesto propuesta = findPropuesta(usuario, libroId);
        if (propuesta == null) {
            throw new RecursoNoEncontradoException("El usuario no tiene propuesta para el libro: " + libroId);
        }

        if (propuesta.getVotaciones() == null || propuesta.getVotaciones().isEmpty()) {
            return List.of();
        }

        // map manual a DTO anidado del response
        List<UsuarioResponseDTO.VotacionDTO> salida = new ArrayList<>(propuesta.getVotaciones().size());
        for (UsuariosModel.Votacion v : propuesta.getVotaciones()) {
            UsuarioResponseDTO.VotacionDTO d = new UsuarioResponseDTO.VotacionDTO();
            d.setVoto(v.getVoto());
            d.setFechaVoto(v.getFechaVoto());
            d.setUsuarioId(v.getUsuarioId() != null ? v.getUsuarioId().toHexString() : null);
            salida.add(d);
        }
        return salida;
    }

    /* ==================== helpers ==================== */

    private UsuariosModel.LibroPropuesto findPropuesta(UsuariosModel usuario, ObjectId libroId) {
        if (usuario.getLibroPropuesto() == null) return null;
        for (UsuariosModel.LibroPropuesto lp : usuario.getLibroPropuesto()) {
            if (lp.getLibroId() != null && lp.getLibroId().equals(libroId)) {
                return lp;
            }
        }
        return null;
    }

    private UsuariosModel.Votacion findVotoDeVotante(UsuariosModel.LibroPropuesto propuesta, ObjectId votanteId) {
        if (propuesta.getVotaciones() == null) return null;
        for (UsuariosModel.Votacion v : propuesta.getVotaciones()) {
            if (v.getUsuarioId() != null && v.getUsuarioId().equals(votanteId)) {
                return v;
            }
        }
        return null;
    }
}
