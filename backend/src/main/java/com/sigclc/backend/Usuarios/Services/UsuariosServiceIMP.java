package com.sigclc.backend.Usuarios.Services;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Exception.RecursoNoEncontradoException;
import com.sigclc.backend.Usuarios.Mapper;
import com.sigclc.backend.Usuarios.DTOs.UsuarioCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioUpdateDTO;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;
import com.sigclc.backend.Usuarios.Repository.IUsuariosRepository;

@Service
public class UsuariosServiceImp implements IUsuariosService {

    @Autowired private IUsuariosRepository usuariosRepository;
    @Autowired private Mapper mapper;

    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioCreateDTO usuario) {
        // Sin validaciones adicionales; si algo falla, lo capturará el GlobalExceptionHandler
        UsuariosModel model = mapper.toModel(usuario);
        usuariosRepository.save(model);
        return mapper.toResponseDTO(model);
    }

    @Override
    public List<UsuarioResponseDTO> listarUsuarios() {
        return mapper.toResponseDTOList(usuariosRepository.findAll());
    }

    @Override
    public UsuariosModel buscarUsuarioPorId(ObjectId id) {
        // Si no existe, lanzamos RecursoNoEncontradoException -> 404 por el handler
        return usuariosRepository.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("No existe usuario con id: " + id));
    }

    @Override
    public UsuarioResponseDTO actualizarUsuarioParcial(ObjectId id, UsuarioUpdateDTO dto) {
        UsuariosModel existente = buscarUsuarioPorId(id);
        mapper.patchModel(existente, dto);
        usuariosRepository.save(existente);
        return mapper.toResponseDTO(existente);
    }
}
