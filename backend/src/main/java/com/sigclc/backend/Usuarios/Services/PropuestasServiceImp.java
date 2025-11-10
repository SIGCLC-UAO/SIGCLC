package com.sigclc.backend.Usuarios.Services;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Usuarios.Exception.RecursoNoEncontradoException;
import com.sigclc.backend.Usuarios.Mapper;
import com.sigclc.backend.Usuarios.DTOs.UsuarioPropuestaDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;
import com.sigclc.backend.Usuarios.Repository.IUsuariosRepository;

@Service
public class PropuestasServiceImp implements IPropuestasService {

    @Autowired private IUsuariosRepository usuariosRepository;
    @Autowired private IUsuariosService usuariosService; // para reutilizar buscarUsuarioPorId
    @Autowired private Mapper mapper;

    @Override
    public UsuarioResponseDTO agregarPropuesta(ObjectId usuarioId, ObjectId libroId, String estado) {
        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioId);

        // crear subdocumento embebido (clase interna NO estática → u.new ...)
        UsuariosModel.LibroPropuesto propuesta = usuario.new LibroPropuesto();
        propuesta.setLibroId(libroId);
        propuesta.setEstado(estado != null ? estado : "En Votacion");
        propuesta.setVotaciones(new ArrayList<>());

        if (usuario.getLibroPropuesto() == null) {
            usuario.setLibroPropuesto(new ArrayList<>());
        }
        usuario.getLibroPropuesto().add(propuesta);

        usuariosRepository.save(usuario);
        return mapper.toResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO cambiarEstadoPropuesta(ObjectId usuarioId, ObjectId libroId, String nuevoEstado) {
        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioId);

        UsuariosModel.LibroPropuesto encontrada = null;
        if (usuario.getLibroPropuesto() != null) {
            for (UsuariosModel.LibroPropuesto lp : usuario.getLibroPropuesto()) {
                if (lp.getLibroId() != null && lp.getLibroId().equals(libroId)) {
                    encontrada = lp;
                    break;
                }
            }
        }

        if (encontrada == null) {
            throw new RecursoNoEncontradoException("El usuario no tiene propuesta para el libro: " + libroId);
        }

        encontrada.setEstado(nuevoEstado);
        usuariosRepository.save(usuario);
        return mapper.toResponseDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO eliminarPropuesta(ObjectId usuarioId, ObjectId libroId) {
        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioId);

        boolean removed = false;
        if (usuario.getLibroPropuesto() != null) {
            removed = usuario.getLibroPropuesto()
                             .removeIf(lp -> lp.getLibroId() != null && lp.getLibroId().equals(libroId));
        }

        if (!removed) {
            throw new RecursoNoEncontradoException("No se encontró la propuesta para el libro: " + libroId);
        }

        usuariosRepository.save(usuario);
        return mapper.toResponseDTO(usuario);
    }

    @Override
    public List<UsuarioPropuestaDTO> propuestasDetalladasDeUsuario(ObjectId usuarioId) {
        UsuariosModel usuario = usuariosService.buscarUsuarioPorId(usuarioId);

        List<UsuarioPropuestaDTO> salida = new ArrayList<>();
        if (usuario.getLibroPropuesto() == null || usuario.getLibroPropuesto().isEmpty()) {
            return salida;
        }

        String nombreUsuario = (usuario.getNombre() != null ? usuario.getNombre() : "") +
                               (usuario.getApellido() != null ? " " + usuario.getApellido() : "");

        for (UsuariosModel.LibroPropuesto lp : usuario.getLibroPropuesto()) {
            UsuarioPropuestaDTO dto = new UsuarioPropuestaDTO();
            dto.setNombreUsuario(nombreUsuario.trim());
            dto.setLibroId(lp.getLibroId() != null ? lp.getLibroId().toHexString() : null);
            dto.setEstado(lp.getEstado());

            // métricas locales
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
                // Ocultamos métricas si está "En Votacion"
                dto.setVotosSi(null);
                dto.setVotosNo(null);
                dto.setTotalVotos(null);
            }

            salida.add(dto);
        }

        return salida;
    }
}
