package com.sigclc.backend.Usuarios.Controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.sigclc.backend.Usuarios.DTOs.PropuestaCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.PropuestaCreateResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.PropuestaEstadoUpdateDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.Services.IPropuestasService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/propuestas")
@Validated
public class PropuestasController {

    @Autowired
    private IPropuestasService propuestasService;

    /**
     * Agregar una nueva propuesta de libro a un usuario.
     * Dueño de la propuesta = usuarioId (path)
     *
     * HTTP: POST
     * URL: /api/usuarios/{usuarioId}/propuestas
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> agregarPropuesta(
            @PathVariable("usuarioId") String usuarioIdHex,
            @Valid @RequestBody PropuestaCreateDTO body) {

        ObjectId usuarioId = parseObjectIdOrBadRequest(usuarioIdHex, "usuarioId inválido.");
        UsuarioResponseDTO dto = propuestasService.agregarPropuesta(usuarioId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Cambiar estado de una propuesta concreta (Seleccionado / No Seleccionado).
     *
     * - usuarioId (path) = dueño de la propuesta
     * - libroId (path) = id del libro propuesto
     * - body.usuarioId = actor (moderador/administrador que cambia el estado)
     *
     * HTTP: PATCH
     * URL: /api/usuarios/{usuarioId}/propuestas/{libroId}/estado
     */
    @PatchMapping("/{libroId}/estado")
    public ResponseEntity<UsuarioResponseDTO> cambiarEstadoPropuesta(
            @PathVariable("usuarioId") String usuarioIdHex,
            @PathVariable("libroId") String libroIdHex,
            @Valid @RequestBody PropuestaEstadoUpdateDTO body) {

        ObjectId usuarioId = parseObjectIdOrBadRequest(usuarioIdHex, "usuarioId inválido.");
        UsuarioResponseDTO dto = propuestasService.cambiarEstadoPropuesta(usuarioId, libroIdHex, body);
        return ResponseEntity.ok(dto);
    }

    /**
     * Eliminar una propuesta de libro de un usuario.
     *
     * HTTP: DELETE
     * URL: /api/usuarios/{usuarioId}/propuestas/{libroId}
     */
    @DeleteMapping("/{libroId}")
    public ResponseEntity<UsuarioResponseDTO> eliminarPropuesta(
            @PathVariable("usuarioId") String usuarioIdHex,
            @PathVariable("libroId") String libroIdHex) {

        ObjectId usuarioId = parseObjectIdOrBadRequest(usuarioIdHex, "usuarioId inválido.");
        UsuarioResponseDTO dto = propuestasService.eliminarPropuesta(usuarioId, libroIdHex);
        return ResponseEntity.ok(dto);
    }

    /**
     * Listar todas las propuestas de un usuario, con métricas de votos
     * (cuando la propuesta ya no está "En Votacion").
     *
     * HTTP: GET
     * URL: /api/usuarios/{usuarioId}/propuestas
     */
    @GetMapping
    public ResponseEntity<List<PropuestaCreateResponseDTO>> listarPropuestasDeUsuario(
            @PathVariable("usuarioId") String usuarioIdHex) {

        ObjectId usuarioId = parseObjectIdOrBadRequest(usuarioIdHex, "usuarioId inválido.");
        List<PropuestaCreateResponseDTO> lista = propuestasService.listarPropuestasDeUsuario(usuarioId);
        return ResponseEntity.ok(lista);
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
