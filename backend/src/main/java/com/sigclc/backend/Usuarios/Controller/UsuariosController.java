package com.sigclc.backend.Usuarios.Controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sigclc.backend.Usuarios.DTOs.UsuarioCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioPropuestaDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioUpdateDTO;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;
import com.sigclc.backend.Usuarios.Services.IUsuariosService;
import com.sigclc.backend.Usuarios.Services.IPropuestasService;
import com.sigclc.backend.Usuarios.Services.IVotacionesService;

/** Controller principal de Usuarios + Propuestas + Votos */
@RestController
@RequestMapping("/SIGCLC/api/usuarios")
public class UsuariosController {

    @Autowired private IUsuariosService usuariosService;
    @Autowired private IPropuestasService propuestasService;
    @Autowired private IVotacionesService votacionesService;

    /* =========================
     *       USUARIOS
     * ========================= */

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@RequestBody UsuarioCreateDTO body) {
        return new ResponseEntity<>(usuariosService.crearUsuario(body), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return new ResponseEntity<>(usuariosService.listarUsuarios(), HttpStatus.OK);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<UsuariosModel> detalle(@PathVariable String usuarioId) {
        return new ResponseEntity<>(usuariosService.buscarUsuarioPorId(new ObjectId(usuarioId)), HttpStatus.OK);
    }

    @PatchMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponseDTO> actualizarParcial(
            @PathVariable String usuarioId,
            @RequestBody UsuarioUpdateDTO body) {
        return new ResponseEntity<>(
                usuariosService.actualizarUsuarioParcial(new ObjectId(usuarioId), body),
                HttpStatus.OK
        );
    }

    /* =========================
     *       PROPUESTAS
     * ========================= */

    /** Agregar propuesta a un usuario (estado por defecto “En Votacion” si viene null) */
    @PostMapping("/{usuarioId}/propuestas")
    public ResponseEntity<UsuarioResponseDTO> agregarPropuesta(
            @PathVariable String usuarioId,
            @RequestBody PropuestaRequest body) {
        return new ResponseEntity<>(
                propuestasService.agregarPropuesta(
                        new ObjectId(usuarioId),
                        new ObjectId(body.libroId),
                        body.estado),
                HttpStatus.CREATED
        );
    }

    /** Cambiar estado de una propuesta existente */
    @PatchMapping("/{usuarioId}/propuestas/{libroId}/estado")
    public ResponseEntity<UsuarioResponseDTO> cambiarEstado(
            @PathVariable String usuarioId,
            @PathVariable String libroId,
            @RequestBody EstadoRequest body) {
        return new ResponseEntity<>(
                propuestasService.cambiarEstadoPropuesta(
                        new ObjectId(usuarioId),
                        new ObjectId(libroId),
                        body.estado),
                HttpStatus.OK
        );
    }

    /** Eliminar una propuesta del usuario */
    @DeleteMapping("/{usuarioId}/propuestas/{libroId}")
    public ResponseEntity<UsuarioResponseDTO> eliminarPropuesta(
            @PathVariable String usuarioId,
            @PathVariable String libroId) {
        return new ResponseEntity<>(
                propuestasService.eliminarPropuesta(
                        new ObjectId(usuarioId),
                        new ObjectId(libroId)),
                HttpStatus.OK
        );
    }

    /** Listar propuestas del usuario con métricas locales (oculta métricas si estado = “En Votacion”) */
    @GetMapping("/{usuarioId}/propuestas/detalle")
    public ResponseEntity<List<UsuarioPropuestaDTO>> propuestasDetalladas(
            @PathVariable String usuarioId) {
        return new ResponseEntity<>(
                propuestasService.propuestasDetalladasDeUsuario(new ObjectId(usuarioId)),
                HttpStatus.OK
        );
    }

    /* =========================
     *         VOTOS
     * ========================= */

    /** Agregar voto a la propuesta de un libro */
    @PostMapping("/{usuarioId}/propuestas/{libroId}/votos")
    public ResponseEntity<UsuarioResponseDTO> agregarVoto(
            @PathVariable String usuarioId,
            @PathVariable String libroId,
            @RequestBody VotoRequest body) {
        return new ResponseEntity<>(
                votacionesService.agregarVoto(
                        new ObjectId(usuarioId),
                        new ObjectId(libroId),
                        new ObjectId(body.votanteId),
                        body.voto,
                        body.fechaVoto),
                HttpStatus.CREATED
        );
    }

    /** Actualizar el voto de un votante */
    @PatchMapping("/{usuarioId}/propuestas/{libroId}/votos/{votanteId}")
    public ResponseEntity<UsuarioResponseDTO> actualizarVoto(
            @PathVariable String usuarioId,
            @PathVariable String libroId,
            @PathVariable String votanteId,
            @RequestBody VotoUpdateRequest body) {
        return new ResponseEntity<>(
                votacionesService.actualizarVoto(
                        new ObjectId(usuarioId),
                        new ObjectId(libroId),
                        new ObjectId(votanteId),
                        body.voto,
                        body.fechaVoto),
                HttpStatus.OK
        );
    }

    /** Eliminar el voto de un votante */
    @DeleteMapping("/{usuarioId}/propuestas/{libroId}/votos/{votanteId}")
    public ResponseEntity<UsuarioResponseDTO> eliminarVoto(
            @PathVariable String usuarioId,
            @PathVariable String libroId,
            @PathVariable String votanteId) {
        return new ResponseEntity<>(
                votacionesService.eliminarVoto(
                        new ObjectId(usuarioId),
                        new ObjectId(libroId),
                        new ObjectId(votanteId)),
                HttpStatus.OK
        );
    }

    /** Listar votos de una propuesta */
    @GetMapping("/{usuarioId}/propuestas/{libroId}/votos")
    public ResponseEntity<List<UsuarioResponseDTO.VotacionDTO>> listarVotos(
            @PathVariable String usuarioId,
            @PathVariable String libroId) {
        return new ResponseEntity<>(
                votacionesService.listarVotosDePropuesta(
                        new ObjectId(usuarioId),
                        new ObjectId(libroId)),
                HttpStatus.OK
        );
    }

    /* =========================
     *  Request bodies simples
     * ========================= */
    public static class PropuestaRequest {
        public String libroId; // hex de ObjectId
        public String estado;  // "En Votacion" | "Seleccionado" | "No Seleccionado"
    }
    public static class EstadoRequest {
        public String estado;
    }
    public static class VotoRequest {
        public String votanteId; // hex de ObjectId
        public String voto;      // "Si" | "No"
        public String fechaVoto; // ISO-8601 sugerido
    }
    public static class VotoUpdateRequest {
        public String voto;       // opcional
        public String fechaVoto;  // opcional
    }
}
