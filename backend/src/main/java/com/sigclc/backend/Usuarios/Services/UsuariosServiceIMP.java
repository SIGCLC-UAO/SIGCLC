package com.sigclc.backend.Usuarios.Services;

import java.util.List;
import java.util.Objects;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Usuarios.DTOs.UsuarioCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioUpdateDTO;
import com.sigclc.backend.Usuarios.Exception.RecursoNoEncontradoException;
import com.sigclc.backend.Usuarios.Mapper;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;
import com.sigclc.backend.Usuarios.Repository.IUsuariosRepository;

@Service
public class UsuariosServiceImp implements IUsuariosService {

    @Autowired private IUsuariosRepository usuariosRepository;
    @Autowired private Mapper mapper;

    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioCreateDTO dto) {
        Objects.requireNonNull(dto, "UsuarioCreateDTO no puede ser null");

        if (usuariosRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalStateException("El email ya está registrado.");
        }
        if (usuariosRepository.existsByTelefono(dto.getTelefono())) {
            throw new IllegalStateException("El teléfono ya está registrado.");
        }
        validarRol(dto.getRol());

        UsuariosModel model = Objects.requireNonNull(mapper.toModel(dto), "Mapper.toModel devolvió null");
        usuariosRepository.save(Objects.requireNonNull(model, "model no puede ser null"));
        return Objects.requireNonNull(mapper.toResponseDTO(model), "Mapper.toResponseDTO devolvió null");
    }

    @Override
    public List<UsuarioResponseDTO> listarUsuarios() {
        return mapper.toResponseDTOList(usuariosRepository.findAll());
    }

    @Override
    public UsuariosModel buscarUsuarioPorId(ObjectId id) {
        Objects.requireNonNull(id, "id no puede ser null");
        return usuariosRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("No existe usuario con id: " + id));
    }

    @Override
    public UsuarioResponseDTO actualizarUsuarioParcial(ObjectId id, UsuarioUpdateDTO dto) {
        Objects.requireNonNull(id, "id no puede ser null");
        Objects.requireNonNull(dto, "UsuarioUpdateDTO no puede ser null");

        UsuariosModel existente = buscarUsuarioPorId(id);

        if (dto.getTelefono() != null && !dto.getTelefono().equals(existente.getTelefono())) {
            if (usuariosRepository.existsByTelefono(dto.getTelefono())) {
                throw new IllegalStateException("El teléfono ya está registrado.");
            }
        }

        mapper.patchModel(existente, dto);
        usuariosRepository.save(Objects.requireNonNull(existente, "existente no puede ser null"));
        return Objects.requireNonNull(mapper.toResponseDTO(existente), "Mapper.toResponseDTO devolvió null");
    }

    @Override
    public void eliminarUsuario(ObjectId id) {
        Objects.requireNonNull(id, "id no puede ser null");

        UsuariosModel aEliminar = buscarUsuarioPorId(id);

        // Limpiar votos emitidos por este usuario en propuestas de otros
        List<UsuariosModel> afectados = usuariosRepository.findAllConVotoDeUsuario(id);
        for (UsuariosModel u : afectados) {
            if (u.getLibroPropuesto() == null) continue;
            boolean modificado = false;
            for (UsuariosModel.LibroPropuesto lp : u.getLibroPropuesto()) {
                if (lp.getVotaciones() == null) continue;
                boolean removed = lp.getVotaciones().removeIf(v -> id.equals(v.getUsuarioId()));
                modificado = modificado || removed;
            }
            if (modificado) {
                usuariosRepository.save(u);
            }
        }

        usuariosRepository.deleteById(Objects.requireNonNull(aEliminar.getId(),
                "El usuario a eliminar no tiene _id"));
    }

    /* Helpers */
    private void validarRol(String rol) {
        if (rol == null) throw new IllegalArgumentException("Rol obligatorio.");
        if (!rol.equals("lector") && !rol.equals("moderador") && !rol.equals("administrador")) {
            throw new IllegalArgumentException("Rol inválido. Use: lector | moderador | administrador.");
        }
    }
}
