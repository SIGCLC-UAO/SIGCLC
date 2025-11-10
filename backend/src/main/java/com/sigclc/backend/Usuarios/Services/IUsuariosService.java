package com.sigclc.backend.Usuarios.Services;

import java.util.List;

import org.bson.types.ObjectId;

import com.sigclc.backend.Usuarios.DTOs.UsuarioCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioUpdateDTO;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;

public interface IUsuariosService {

    UsuarioResponseDTO crearUsuario(UsuarioCreateDTO dto);

    List<UsuarioResponseDTO> listarUsuarios();

    UsuariosModel buscarUsuarioPorId(ObjectId id);

    UsuarioResponseDTO actualizarUsuarioParcial(ObjectId id, UsuarioUpdateDTO dto);

    void eliminarUsuario(ObjectId id);
}
