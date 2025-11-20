package com.sigclc.backend.Reseñas.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sigclc.backend.Reseñas.DTOs.ComentarioCreateDTO;
import com.sigclc.backend.Reseñas.DTOs.FiltroReseniasDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniaDetailDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniaListViewDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniaUpdateDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniasCreateDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniasResponseDTO;
import com.sigclc.backend.Reseñas.DTOs.StatsReseniasDTO;
import com.sigclc.backend.Reseñas.Services.IReseniasService;

@RestController
@RequestMapping("/SIGCLC/api/resenias")
@CrossOrigin(origins = "*")
public class ReseniasController {

    @Autowired
    private IReseniasService service;

    @PostMapping("/crear")
    public ResponseEntity<?> crear(
            @RequestBody @Validated ReseniasCreateDTO dto,
            BindingResult result) {

        if (result.hasErrors()) {
            List<String> errores = result.getAllErrors().stream()
                    .map(x -> x.getDefaultMessage()).toList();
            return ResponseEntity.badRequest().body(errores);
        }

        return new ResponseEntity<>(service.crearResenia(dto), HttpStatus.CREATED);
    }

    
    @PostMapping("/crear-con-archivos")
    public ResponseEntity<?> crearConArchivos(
            @RequestPart("datos") @Validated ReseniasCreateDTO dto,
            @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {

        return new ResponseEntity<>(service.crearReseniaConArchivos(dto, archivos), HttpStatus.CREATED);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable String id,
            @RequestBody @Validated ReseniaUpdateDTO dto,
            @RequestHeader("usuarioId") String usuarioId,
            BindingResult result) {

        if (result.hasErrors()) {
            List<String> errores = result.getAllErrors().stream()
                    .map(x -> x.getDefaultMessage()).toList();
            return ResponseEntity.badRequest().body(errores);
        }

        return ResponseEntity.ok(service.actualizarResenia(id, dto, usuarioId));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(
            @PathVariable String id,
            @RequestHeader("usuarioId") String usuarioId) {

        service.eliminarResenia(id, usuarioId);
        return ResponseEntity.ok("Reseña eliminada correctamente");
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ReseniaListViewDTO>> listar(
            @RequestParam(required = false) String libroId,
            @RequestParam(required = false) String autorId,
            @RequestParam(required = false) Integer calificacionMin,
            @RequestParam(required = false) Integer calificacionMax,
            @RequestParam(required = false) Boolean tieneAdjuntos,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String comentadoPor) {

        FiltroReseniasDTO filtros = new FiltroReseniasDTO();
        filtros.setLibroId(libroId);
        filtros.setAutorId(autorId);
        filtros.setCalificacionMin(calificacionMin);
        filtros.setCalificacionMax(calificacionMax);
        filtros.setTieneAdjuntos(tieneAdjuntos);
        filtros.setTexto(texto);
        filtros.setComentadoPor(comentadoPor);

        return ResponseEntity.ok(service.listarConFiltros(filtros));
    }

    @GetMapping("/detalle/{id}")
    public ResponseEntity<ReseniaDetailDTO> obtenerDetalle(
            @PathVariable String id,
            @RequestHeader("usuarioId") String usuarioId) {

        return ResponseEntity.ok(service.obtenerDetalle(id, usuarioId));
    }

    @GetMapping("/mis-resenias")
    public ResponseEntity<List<ReseniaListViewDTO>> misResenias(
            @RequestHeader("usuarioId") String usuarioId) {

        return ResponseEntity.ok(service.misResenias(usuarioId));
    }

    @PostMapping("/comentar/{idResenia}")
    public ResponseEntity<String> comentar(
            @PathVariable String idResenia,
            @RequestBody @Validated ComentarioCreateDTO dto,
            BindingResult result) {

        if (result.hasErrors()) {
            List<String> errores = result.getAllErrors().stream()
                    .map(x -> x.getDefaultMessage()).toList();
            return ResponseEntity.badRequest().body(errores.toString());
        }

        service.agregarComentario(idResenia, dto);
        return ResponseEntity.ok("Comentario agregado correctamente");
    }

    @DeleteMapping("/comentario/{idResenia}")
    public ResponseEntity<String> eliminarComentario(
            @PathVariable String idResenia,
            @RequestParam String usuarioId,
            @RequestParam String textoComentario) {

        service.eliminarComentario(idResenia, usuarioId, textoComentario);
        return ResponseEntity.ok("Comentario eliminado correctamente");
    }

    @GetMapping("/estadisticas/libro/{libroId}")
    public ResponseEntity<StatsReseniasDTO> estadisticas(
            @PathVariable String libroId) {

        return ResponseEntity.ok(service.obtenerEstadisticasPorLibro(libroId));
    }

    @GetMapping("/top-reviewers")
    public ResponseEntity<?> topReviewers(@RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(service.obtenerTopReviewers(limite));
    }

    @GetMapping("/libro/{idLibro}")
    public ResponseEntity<List<ReseniasResponseDTO>> listarPorLibro(@PathVariable String idLibro) {
        return ResponseEntity.ok(service.listarPorLibro(idLibro));
    }

    @GetMapping("/autor/{idAutor}")
    public ResponseEntity<List<ReseniasResponseDTO>> listarPorAutor(@PathVariable String idAutor) {
        return ResponseEntity.ok(service.listarPorAutor(idAutor));
    }

    @PutMapping("/marcar-util/{idResenia}")
    public ResponseEntity<String> marcarUtil(@PathVariable String idResenia) {
        service.marcarUtil(idResenia);
        return ResponseEntity.ok("Reseña marcada como útil");
    }

}
