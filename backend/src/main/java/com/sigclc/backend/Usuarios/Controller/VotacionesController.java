package com.sigclc.backend.Usuarios.Controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.VotoCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.VotoUpdateDTO;
import com.sigclc.backend.Usuarios.Services.IVotacionesService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios/{usuarioIdPropuesta}/propuestas/{libroId}/votos")
@Validated
public class VotacionesController {

    @Autowired
    private IVotacionesService votacionesService;

    /**
     * Agregar voto a una propuesta de libro
     * - usuarioIdPropuesta (path) = dueño de la propuesta
     * - libroId (path) = id del libro propuesto
     * - body.usuarioId = votante
     *
     * HTTP: POST
     * URL: /api/usuarios/{usuarioIdPropuesta}/propuestas/{libroId}/votos
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> agregarVoto(
            @PathVariable("usuarioIdPropuesta") String usuarioIdPropuestaHex,
            @PathVariable("libroId") String libroIdHex,
            @Valid @RequestBody VotoCreateDTO body) {

        ObjectId usuarioIdPropuesta = parseObjectIdOrBadRequest(
                usuarioIdPropuestaHex, "usuarioIdPropuesta inválido.");

        UsuarioResponseDTO dto = votacionesService.agregarVoto(usuarioIdPropuesta, libroIdHex, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Actualizar un voto concreto (del votante indicado)
     *
     * HTTP: PATCH
     * URL: /api/usuarios/{usuarioIdPropuesta}/propuestas/{libroId}/votos/{votanteId}
     */
    @PatchMapping("/{votanteId}")
    public ResponseEntity<UsuarioResponseDTO> actualizarVoto(
            @PathVariable("usuarioIdPropuesta") String usuarioIdPropuestaHex,
            @PathVariable("libroId") String libroIdHex,
            @PathVariable("votanteId") String votanteIdHex,
            @Valid @RequestBody VotoUpdateDTO body) {

        ObjectId usuarioIdPropuesta = parseObjectIdOrBadRequest(
                usuarioIdPropuestaHex, "usuarioIdPropuesta inválido.");

        UsuarioResponseDTO dto = votacionesService.actualizarVoto(
                usuarioIdPropuesta, libroIdHex, votanteIdHex, body);

        return ResponseEntity.ok(dto);
    }

    /**
     * Eliminar voto de un votante sobre una propuesta
     *
     * HTTP: DELETE
     * URL: /api/usuarios/{usuarioIdPropuesta}/propuestas/{libroId}/votos/{votanteId}
     */
    @DeleteMapping("/{votanteId}")
    public ResponseEntity<UsuarioResponseDTO> eliminarVoto(
            @PathVariable("usuarioIdPropuesta") String usuarioIdPropuestaHex,
            @PathVariable("libroId") String libroIdHex,
            @PathVariable("votanteId") String votanteIdHex) {

        ObjectId usuarioIdPropuesta = parseObjectIdOrBadRequest(
                usuarioIdPropuestaHex, "usuarioIdPropuesta inválido.");

        UsuarioResponseDTO dto = votacionesService.eliminarVoto(
                usuarioIdPropuesta, libroIdHex, votanteIdHex);

        return ResponseEntity.ok(dto);
    }

    /**
     * Listar todos los votos de una propuesta
     *
     * HTTP: GET
     * URL: /api/usuarios/{usuarioIdPropuesta}/propuestas/{libroId}/votos
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO.VotacionDTO>> listarVotosDePropuesta(
            @PathVariable("usuarioIdPropuesta") String usuarioIdPropuestaHex,
            @PathVariable("libroId") String libroIdHex) {

        ObjectId usuarioIdPropuesta = parseObjectIdOrBadRequest(
                usuarioIdPropuestaHex, "usuarioIdPropuesta inválido.");

        List<UsuarioResponseDTO.VotacionDTO> lista =
                votacionesService.listarVotosDePropuesta(usuarioIdPropuesta, libroIdHex);

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
