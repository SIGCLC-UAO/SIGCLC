package com.sigclc.backend.Usuarios.Services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Usuarios.DTOs.TopLibroVotosDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioPropuestaDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.VotacionCreateDTO;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;
import com.sigclc.backend.Usuarios.Repository.IUsuariosRepository;


@Service
public class UsuariosServiceImp implements IUsuariosService {

    private static final String ESTADO_EN_VOTACION    = "En Votacion";
    private static final String ESTADO_SELECCIONADO   = "Seleccionado";
    private static final String ESTADO_NO_SELECCIONADO= "No Seleccionado";

    private static final String ROL_ADMIN      = "administrador";
    private static final String ROL_MODERADOR  = "moderador";

    @Autowired IUsuariosRepository usuariosRepository;
    @Autowired UsuarioMapper usuarioMapper;

    // 1) Crear usuario
    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioCreateDTO usuario) {
        UsuariosModel model = usuarioMapper.toModel(usuario);
        usuariosRepository.save(model);
        return usuarioMapper.toResponseDTO(model);
    }

    // 2) Obtener usuario por id (devuelve Model, como en tu ejemplo base)
    @Override
    public UsuariosModel buscarUsuarioPorId(ObjectId id) {
        return usuariosRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("No existe usuario con id: " + id));
    }

    // 3) Listar usuarios (simple)
    @Override
    public List<UsuarioResponseDTO> listarUsuarios() {
        return usuarioMapper.toResponseDTOList(usuariosRepository.findAll());
    }

    // 4) Actualizar parcial
    @Override
    public UsuarioResponseDTO actualizarUsuarioParcial(ObjectId id, UsuarioUpdateDTO dto) {
        UsuariosModel existing = buscarUsuarioPorId(id);
        usuarioMapper.patchModel(existing, dto);
        usuariosRepository.save(existing);
        return usuarioMapper.toResponseDTO(existing);
    }

    // 5) Agregar propuesta
    @Override
    public UsuarioResponseDTO agregarPropuesta(ObjectId usuarioId, ObjectId libroId, String estado) {
        UsuariosModel usuario = buscarUsuarioPorId(usuarioId);

        if (usuario.getLibroPropuesto() == null) {
            usuario.setLibroPropuesto(new ArrayList<>());
        } else {
            // Evitar duplicado del mismo libro en este usuario
            boolean yaExiste = usuario.getLibroPropuesto().stream()
                .anyMatch(lp -> libroId.equals(lp.getLibroId()));
            if (yaExiste) {
                // Puedes lanzar excepción custom si prefieres
                throw new IllegalStateException("El usuario ya propuso este libro.");
            }
        }

        LibroPropuesto nueva = new LibroPropuesto();
        nueva.setLibroId(libroId);
        nueva.setEstado(estado);
        nueva.setVotaciones(new ArrayList<>());

        usuario.getLibroPropuesto().add(nueva);
        usuariosRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuario);
    }

    // 6) Cambiar estado de propuesta (solo admin/moderador)
    @Override
    public UsuarioResponseDTO cambiarEstadoPropuesta(ObjectId actorId, ObjectId usuarioId, ObjectId libroId, String nuevoEstado) {
        UsuariosModel actor = buscarUsuarioPorId(actorId);
        if (!tienePermisoCambioEstado(actor)) {
            throw new IllegalStateException("Solo administrador o moderador pueden cambiar el estado de la propuesta.");
        }

        UsuariosModel usuario = buscarUsuarioPorId(usuarioId);
        LibroPropuesto lp = encontrarPropuesta(usuario, libroId);

        String estadoAnterior = lp.getEstado();
        lp.setEstado(nuevoEstado);

        // Si pasa de En Votacion -> (Seleccionado|No Seleccionado), se da por cerrada (regla aplicada en métodos de voto)
        // Aquí no vaciamos votaciones; solo bloquearemos nuevas operaciones de voto en los métodos 10–12.

        usuariosRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuario);
    }

    // 7) Eliminar propuesta
    @Override
    public UsuarioResponseDTO eliminarPropuesta(ObjectId usuarioId, ObjectId libroId) {
        UsuariosModel usuario = buscarUsuarioPorId(usuarioId);
        if (usuario.getLibroPropuesto() != null) {
            usuario.getLibroPropuesto().removeIf(lp -> libroId.equals(lp.getLibroId()));
        }
        usuariosRepository.save(usuario);
        return usuarioMapper.toResponseDTO(usuario);
    }

    // 8) (fusión 9+14) Propuestas detalladas de un usuario (con lookup y métricas; oculta métricas si "En Votacion")
    @Override
    public List<UsuarioPropuestaDTO> propuestasDetalladasDeUsuario(ObjectId usuarioId) {
        List<UsuarioPropuestaDTO> lista = usuariosRepository.propuestasDetalladasDeUsuario(usuarioId);
        // Ocultar métricas si la propuesta está "En Votacion"
        for (UsuarioPropuestaDTO dto : lista) {
            if (ESTADO_EN_VOTACION.equals(dto.getEstado())) {
                dto.setVotosSi(null);
                dto.setVotosNo(null);
                dto.setTotalVotos(null);
            }
        }
        return lista;
    }

    // 10) Agregar votación (solo si estado = "En Votacion") REVISAR
    @Override
    public UsuarioResponseDTO agregarVotacion(ObjectId usuarioIdDueno, ObjectId libroId, VotacionCreateDTO votoDTO) {
        UsuariosModel dueno = buscarUsuarioPorId(usuarioIdDueno);
        LibroPropuesto lp = encontrarPropuesta(dueno, libroId);

        if (!ESTADO_EN_VOTACION.equals(lp.getEstado())) {
            throw new IllegalStateException("La propuesta no está en 'En Votacion'; no se pueden agregar votos.");
        }

        if (lp.getVotaciones() == null) lp.setVotaciones(new ArrayList<>());
        // Un voto por votante
        boolean yaVoto = lp.getVotaciones().stream()
            .anyMatch(v -> v.getUsuarioId() != null && v.getUsuarioId().equals(new ObjectId(votoDTO.getUsuarioId())));
        if (yaVoto) {
            throw new IllegalStateException("El votante ya tiene un voto en esta propuesta. Usa actualizarVotacion.");
        }

        Votacion v = new Votacion();
        v.setVoto(votoDTO.getVoto());
        v.setFechaVoto(votoDTO.getFechaVoto());
        v.setUsuarioId(new ObjectId(votoDTO.getUsuarioId()));

        lp.getVotaciones().add(v);
        usuariosRepository.save(dueno);
        return usuarioMapper.toResponseDTO(dueno);
    }

    // 11) Actualizar votación (solo si estado = "En Votacion")
    @Override
    public UsuarioResponseDTO actualizarVotacion(ObjectId usuarioIdDueno, ObjectId libroId, ObjectId votanteId, String nuevoVoto) {
        UsuariosModel dueno = buscarUsuarioPorId(usuarioIdDueno);
        LibroPropuesto lp = encontrarPropuesta(dueno, libroId);

        if (!ESTADO_EN_VOTACION.equals(lp.getEstado())) {
            throw new IllegalStateException("La propuesta no está en 'En Votacion'; no se pueden actualizar votos.");
        }

        Votacion voto = encontrarVoto(lp, votanteId);
        voto.setVoto(nuevoVoto);

        usuariosRepository.save(dueno);
        return usuarioMapper.toResponseDTO(dueno);
    }

    // 12) Eliminar votación (solo si estado = "En Votacion")
    @Override
    public UsuarioResponseDTO eliminarVotacion(ObjectId usuarioIdDueno, ObjectId libroId, ObjectId votanteId) {
        UsuariosModel dueno = buscarUsuarioPorId(usuarioIdDueno);
        LibroPropuesto lp = encontrarPropuesta(dueno, libroId);

        if (!ESTADO_EN_VOTACION.equals(lp.getEstado())) {
            throw new IllegalStateException("La propuesta no está en 'En Votacion'; no se pueden eliminar votos.");
        }

        if (lp.getVotaciones() != null) {
            Iterator<Votacion> it = lp.getVotaciones().iterator();
            boolean eliminado = false;
            while (it.hasNext()) {
                Votacion v = it.next();
                if (v.getUsuarioId() != null && v.getUsuarioId().equals(votanteId)) {
                    it.remove();
                    eliminado = true;
                    break;
                }
            }
            if (!eliminado) {
                throw new RecursoNoEncontradoException("No existe voto del usuario: " + votanteId);
            }
        }
        usuariosRepository.save(dueno);
        return usuarioMapper.toResponseDTO(dueno);
    }

    // 13) Métricas de una propuesta puntual
    @Override
    public UsuarioPropuestaLibroDTO metricasDePropuesta(ObjectId usuarioIdDueno, ObjectId libroId) {
        UsuariosModel dueno = buscarUsuarioPorId(usuarioIdDueno);
        LibroPropuesto lp = encontrarPropuesta(dueno, libroId);

        int si = 0, no = 0, total = 0;
        if (lp.getVotaciones() != null) {
            total = lp.getVotaciones().size();
            for (Votacion v : lp.getVotaciones()) {
                if ("Si".equals(v.getVoto())) si++;
                else if ("No".equals(v.getVoto())) no++;
            }
        }

        UsuarioPropuestaDTO dto = new UsuarioPropuestaDTO();
        dto.setNombreUsuario(dueno.getNombre());
        dto.setLibroId(libroId != null ? libroId.toHexString() : null);
        dto.setEstado(lp.getEstado());
        // Título / autor pueden venir de un lookup si lo necesitas más adelante
        dto.setTitulo(null);
        dto.setAutor(null);
        dto.setVotosSi(si);
        dto.setVotosNo(no);
        dto.setTotalVotos(total);

        return dto;
    }

    // 15) Top global (libro más votado)
    @Override
    public List<TopLibroVotosDTO> topLibroMasVotado() {
        return usuariosRepository.topLibroMasVotado();
    }

    // 17) Propuestas para un libro (en toda la BD)
    @Override
    public List<UsuarioPropuestaDTO> buscarPropuestasPorLibro(ObjectId libroId) {
        // Implementación simple basada en carga completa; puedes optimizar con una agregación en repository
        List<UsuariosModel> usuarios = usuariosRepository.findAll();
        List<UsuarioPropuestaDTO> res = new ArrayList<>();

        for (UsuariosModel u : usuarios) {
            if (u.getLibroPropuesto() == null) continue;
            for (LibroPropuesto lp : u.getLibroPropuesto()) {
                if (Objects.equals(lp.getLibroId(), libroId)) {
                    int si = 0, no = 0, total = 0;
                    if (lp.getVotaciones() != null) {
                        total = lp.getVotaciones().size();
                        for (Votacion v : lp.getVotaciones()) {
                            if ("Si".equals(v.getVoto())) si++;
                            else if ("No".equals(v.getVoto())) no++;
                        }
                    }
                    UsuarioPropuestaDTO dto = new UsuarioPropuestaDTO();
                    dto.setNombreUsuario(u.getNombre());
                    dto.setLibroId(libroId.toHexString());
                    dto.setEstado(lp.getEstado());
                    dto.setTitulo(null); // puedes hacer lookup en repo más adelante
                    dto.setAutor(null);
                    dto.setVotosSi(si);
                    dto.setVotosNo(no);
                    dto.setTotalVotos(total);
                    res.add(dto);
                }
            }
        }
        return res;
    }

    // ==== Helpers privados ====

    private boolean tienePermisoCambioEstado(UsuariosModel actor) {
        if (actor == null || actor.getRol() == null) return false;
        String rol = actor.getRol().toLowerCase();
        return ROL_ADMIN.equals(rol) || ROL_MODERADOR.equals(rol);
    }

    private LibroPropuesto encontrarPropuesta(UsuariosModel usuario, ObjectId libroId) {
        if (usuario.getLibroPropuesto() == null || usuario.getLibroPropuesto().isEmpty()) {
            throw new RecursoNoEncontradoException("El usuario no tiene propuestas.");
        }
        return usuario.getLibroPropuesto().stream()
            .filter(lp -> libroId.equals(lp.getLibroId()))
            .findFirst()
            .orElseThrow(() -> new RecursoNoEncontradoException("No existe propuesta para el libro: " + libroId));
    }

    private Votacion encontrarVoto(LibroPropuesto lp, ObjectId votanteId) {
        if (lp.getVotaciones() == null || lp.getVotaciones().isEmpty()) {
            throw new RecursoNoEncontradoException("No hay votos en esta propuesta.");
        }
        return lp.getVotaciones().stream()
            .filter(v -> votanteId.equals(v.getUsuarioId()))
            .findFirst()
            .orElseThrow(() -> new RecursoNoEncontradoException("No existe voto del usuario: " + votanteId));
    }
}
