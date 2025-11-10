package com.sigclc.backend.Usuarios.Controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.sigclc.backend.Usuarios.DTOs.UsuarioCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioUpdateDTO;
import com.sigclc.backend.Usuarios.Mapper;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;
import com.sigclc.backend.Usuarios.Services.IUsuariosService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
@Validated
public class UsuariosController {

    @Autowired
    private IUsuariosService usuariosService;

    @Autowired
    private Mapper mapper;

    /**
     * Crear nuevo usuario
     * HTTP: POST
     * URL: /api/usuarios
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(
            @Valid @RequestBody UsuarioCreateDTO body) {
        UsuarioResponseDTO creado = usuariosService.crearUsuario(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * Listar todos los usuarios
     * HTTP: GET
     * URL: /api/usuarios
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        List<UsuarioResponseDTO> lista = usuariosService.listarUsuarios();
        return ResponseEntity.ok(lista);
    }

    /**
     * Obtener un usuario por id
     * HTTP: GET
     * URL: /api/usuarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> obtenerUsuarioPorId(
            @PathVariable("id") String idHex) {

        ObjectId id = parseObjectIdOrBadRequest(idHex, "id de usuario inválido.");
        UsuariosModel model = usuariosService.buscarUsuarioPorId(id);
        UsuarioResponseDTO dto = mapper.toResponseDTO(model);
        return ResponseEntity.ok(dto);
    }

    /**
     * Actualizar parcialmente un usuario (PATCH)
     * HTTP: PATCH
     * URL: /api/usuarios/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuarioParcial(
            @PathVariable("id") String idHex,
            @Valid @RequestBody UsuarioUpdateDTO body) {

        ObjectId id = parseObjectIdOrBadRequest(idHex, "id de usuario inválido.");
        UsuarioResponseDTO actualizado = usuariosService.actualizarUsuarioParcial(id, body);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Eliminar usuario
     * HTTP: DELETE
     * URL: /api/usuarios/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable("id") String idHex) {

        ObjectId id = parseObjectIdOrBadRequest(idHex, "id de usuario inválido.");
        usuariosService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    /* ==== Helper privado ==== */
    private ObjectId parseObjectIdOrBadRequest(String hex, String msg) {
        if (hex == null || hex.isBlank()) {
            throw new IllegalArgumentException(msg);
        }
        try {
            return new ObjectId(hex);
        } catch (Exception e) {
            throw new IllegalArgumentException(msg);
        }
    }
}
