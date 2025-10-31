package com.sigclc.backend.Usuarios.Services;

import java.util.List;

import org.bson.types.ObjectId;

import com.sigclc.backend.Usuarios.DTOs.UsuarioCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioUpdateDTO;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;

public interface IUsuariosService {

    // Crear usuario
    UsuarioResponseDTO crearUsuario(UsuarioCreateDTO usuario);

    // Listar usuarios (simple)
    List<UsuarioResponseDTO> listarUsuarios();

    // Buscar detalle (devuelve Model para reutilizar internamente)
    UsuariosModel buscarUsuarioPorId(ObjectId id);

    // Actualizar parcial (PATCH)
    UsuarioResponseDTO actualizarUsuarioParcial(ObjectId id, UsuarioUpdateDTO dto);
}
