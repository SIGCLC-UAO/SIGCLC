package com.sigclc.backend.Usuarios.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Libros.Repository.ILibrosRepository;
import com.sigclc.backend.Usuarios.DTOs.PropuestaCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.PropuestaCreateResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.PropuestaEstadoUpdateDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.Exception.RecursoNoEncontradoException;
import com.sigclc.backend.Usuarios.Mapper;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;
import com.sigclc.backend.Usuarios.Models.UsuariosModel.LibroPropuesto;
import com.sigclc.backend.Usuarios.Repository.IUsuariosRepository;

@Service
public class PropuestasServiceImp implements IPropuestasService {

    private static final String EN_VOTACION  = "En Votacion";
    private static final String SELECCIONADO = "Seleccionado";
    private static final String NO_SELECCION = "No Seleccionado";

    @Autowired private IUsuariosRepository usuariosRepository;
    @Autowired private IUsuariosService usuariosService;
    @Autowired private ILibrosRepository librosRepository;
    @Autowired private Mapper mapper;

    @Override
    public UsuarioResponseDTO agregarPropuesta(ObjectId usuarioId, PropuestaCreateDTO body) {
        Objects.requireNonNull(usuarioId, "usuarioId no puede ser null");
        Objects.requireNonNull(body, "PropuestaCreateDTO no puede ser null");

        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioId);

        ObjectId libroId = parseObjectIdOrBadRequest(body.getLibroId(), "libroId inválido.");
        if (!librosRepository.existsById(
                java.util.Objects.requireNonNull(libroId, "libroId no puede ser null")
        )) {
            throw new RecursoNoEncontradoException("No existe libro con id: " + libroId);
        }
        if (usuariosRepository.existsPropuesta(usuarioId, libroId)) {
            throw new IllegalStateException("Ya existe una propuesta para este libro en el usuario.");
        }

        if (usuario.getLibroPropuesto() == null) usuario.setLibroPropuesto(new ArrayList<>());
        LibroPropuesto p = new LibroPropuesto();
        p.setLibroId(libroId);
        p.setEstado(EN_VOTACION);
        p.setVotaciones(new ArrayList<>());
        usuario.getLibroPropuesto().add(p);

        usuariosRepository.save(Objects.requireNonNull(usuario, "usuario no puede ser null"));
        return Objects.requireNonNull(mapper.toResponseDTO(usuario), "Mapper.toResponseDTO devolvió null");
    }

    @Override
    public UsuarioResponseDTO cambiarEstadoPropuesta(ObjectId usuarioId, String libroIdHex, PropuestaEstadoUpdateDTO body) {
        Objects.requireNonNull(usuarioId, "usuarioId no puede ser null");
        Objects.requireNonNull(libroIdHex, "libroIdHex no puede ser null");
        Objects.requireNonNull(body, "PropuestaEstadoUpdateDTO no puede ser null");

        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioId);

        ObjectId libroId = parseObjectIdOrBadRequest(libroIdHex, "libroId inválido.");
        ObjectId actorId = parseObjectIdOrBadRequest(body.getUsuarioId(), "usuarioId inválido.");

        UsuariosModel actor = usuariosService.buscarUsuarioPorId(actorId);
        String rol = actor.getRol();
        if (!"administrador".equals(rol) && !"moderador".equals(rol)) {
            throw new IllegalStateException("No tiene permisos para cambiar estado de propuestas.");
        }

        LibroPropuesto prop = findPropuesta(usuario, libroId);
        if (prop == null) throw new RecursoNoEncontradoException("El usuario no tiene propuesta para ese libro.");
        if (!EN_VOTACION.equals(prop.getEstado())) {
            throw new IllegalArgumentException("La propuesta ya está cerrada. No se puede cambiar.");
        }

        String nuevo = body.getNuevoEstado();
        if (!SELECCIONADO.equals(nuevo) && !NO_SELECCION.equals(nuevo)) {
            throw new IllegalArgumentException("Transición inválida. Use: Seleccionado | No Seleccionado.");
        }

        prop.setEstado(nuevo);
        usuariosRepository.save(
            java.util.Objects.requireNonNull(usuario, "usuario no puede ser null")
        );        
        return Objects.requireNonNull(mapper.toResponseDTO(usuario), "Mapper.toResponseDTO devolvió null");
    }

    @Override
    public UsuarioResponseDTO eliminarPropuesta(ObjectId usuarioId, String libroIdHex) {
        Objects.requireNonNull(usuarioId, "usuarioId no puede ser null");
        Objects.requireNonNull(libroIdHex, "libroIdHex no puede ser null");

        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioId);
        ObjectId libroId = parseObjectIdOrBadRequest(libroIdHex, "libroId inválido.");

        boolean removed = false;
        if (usuario.getLibroPropuesto() != null) {
            removed = usuario.getLibroPropuesto()
                .removeIf(lp -> lp.getLibroId() != null && lp.getLibroId().equals(libroId));
        }
        if (!removed) throw new RecursoNoEncontradoException("No se encontró la propuesta para el libro.");

        usuariosRepository.save(usuario);
        return Objects.requireNonNull(mapper.toResponseDTO(usuario), "Mapper.toResponseDTO devolvió null");
    }

    @Override
    public List<PropuestaCreateResponseDTO> listarPropuestasDeUsuario(ObjectId usuarioId) {
        Objects.requireNonNull(usuarioId, "usuarioId no puede ser null");

        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioId);

        if (usuario.getLibroPropuesto() == null || usuario.getLibroPropuesto().isEmpty()) {
            return new ArrayList<>();
        }

        final String idHex = usuario.getId() != null ? usuario.getId().toHexString() : null;
        final String nombreUsuario = 
            (usuario.getNombre() != null ? usuario.getNombre() : "") +
            (usuario.getApellido() != null ? " " + usuario.getApellido() : "");

        List<PropuestaCreateResponseDTO> out = new ArrayList<>();

        for (UsuariosModel.LibroPropuesto lp : usuario.getLibroPropuesto()) {
            PropuestaCreateResponseDTO dto = new PropuestaCreateResponseDTO();
            dto.setUsuarioId(idHex);
            dto.setNombreUsuario(nombreUsuario.trim());
            dto.setLibroId(lp.getLibroId() != null ? lp.getLibroId().toHexString() : null);
            dto.setEstado(lp.getEstado());

            if (lp.getVotaciones() != null && !"En Votacion".equalsIgnoreCase(lp.getEstado())) {
                int si = 0, no = 0;
                for (UsuariosModel.Votacion v : lp.getVotaciones()) {
                    if (v != null && v.getVoto() != null) {
                        if ("Si".equalsIgnoreCase(v.getVoto())) si++;
                        else if ("No".equalsIgnoreCase(v.getVoto())) no++;
                    }
                }
                dto.setVotosSi(si);
                dto.setVotosNo(no);
                dto.setTotalVotos(si + no);
            } else {
                dto.setVotosSi(null);
                dto.setVotosNo(null);
                dto.setTotalVotos(null);
            }

            out.add(dto);
        }

        return out;
    }

    private LibroPropuesto findPropuesta(UsuariosModel usuario, ObjectId libroId) {
        if (usuario.getLibroPropuesto() == null) return null;
        for (LibroPropuesto lp : usuario.getLibroPropuesto()) {
            if (libroId.equals(lp.getLibroId())) return lp;
        }
        return null;
    }
    
    private ObjectId parseObjectIdOrBadRequest(String hex, String msg) {
        if (hex == null || hex.isBlank()) throw new IllegalArgumentException(msg);
        try { return new ObjectId(hex); } catch (Exception e) { throw new IllegalArgumentException(msg); }
    }
}