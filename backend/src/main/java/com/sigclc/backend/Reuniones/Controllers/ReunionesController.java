package com.sigclc.backend.Reuniones.Controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sigclc.backend.Reuniones.DTOs.ArchivoDeleteRequestDTO;
import com.sigclc.backend.Reuniones.DTOs.ArchivoMetaSafeResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.ArchivoUploadResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.GeneroCountDTO;
import com.sigclc.backend.Reuniones.DTOs.InscripcionResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.LibroCountDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionCreateDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionUpdateDTO;
import com.sigclc.backend.Reuniones.DTOs.UsuarioActivoDTO;
import com.sigclc.backend.Reuniones.Services.IReunionesArchivos;
import com.sigclc.backend.Reuniones.Services.IReunionesAsistentes;
import com.sigclc.backend.Reuniones.Services.IReunionesListados;
import com.sigclc.backend.Reuniones.Services.IReunionesService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reuniones")
public class ReunionesController {

    @Autowired
    private IReunionesService reunionesService;

    @Autowired
    private IReunionesListados reunionesListados;

    @Autowired
    private IReunionesArchivos reunionesArchivos;

    @Autowired
    private IReunionesAsistentes reunionesAsistentes;

    /* =========================================================
     *     CRUD + EXTENSIONES
     * ========================================================= */

    /**
     * CREAR REUNIÓN SOLO CON JSON (sin archivos).
     * Body: application/json con ReunionCreateDTO.
     *
     * POST /api/reuniones
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ReunionResponseDTO crearReunionJson(
            @RequestBody @Valid ReunionCreateDTO createDTO) {

        return reunionesService.crear(createDTO, null);
    }

    /**
     * CREAR REUNIÓN CON JSON + ARCHIVOS (multipart/form-data).
     * - parte "datos": JSON de ReunionCreateDTO
     * - parte "archivos": (opcional) lista de archivos
     *
     * POST /api/reuniones/multipart
     */
    @PostMapping(
            path = "/multipart",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ReunionResponseDTO crearReunionMultipart(
            @RequestPart("datos") @Valid ReunionCreateDTO createDTO,
            @RequestPart(value = "archivos", required = false) List<MultipartFile> archivosSubidos) {

        return reunionesService.crear(createDTO, archivosSubidos);
    }

    /**
     * Actualizar datos de una reunión existente (sin tocar archivos).
     * Recibe JSON application/json.
     *
     * PUT /api/reuniones/{reunionId}
     */
    @PutMapping("/{reunionId}")
    public ReunionResponseDTO actualizarReunion(
            @PathVariable("reunionId") String reunionId,
            @RequestBody @Valid ReunionUpdateDTO updateDTO) {

        return reunionesService.actualizar(reunionId, updateDTO);
    }

    /**
     * Eliminar reunión y todos sus archivos de GridFS.
     *
     * DELETE /api/reuniones/{reunionId}
     */
    @DeleteMapping("/{reunionId}")
    public void eliminarReunion(@PathVariable("reunionId") String reunionId) {
        reunionesService.eliminar(reunionId);
    }

    /**
     * Recalcular las extensiones adjuntas leyendo lo que haya en GridFS.
     *
     * PATCH /api/reuniones/{reunionId}/extensiones/resync
     */
    @PatchMapping("/{reunionId}/extensiones/resync")
    public ReunionResponseDTO resincronizarExtensiones(
            @PathVariable("reunionId") String reunionId) {

        return reunionesService.resyncExtensiones(reunionId);
    }
    
    /* =========================================================
     *     LISTADOS
     * ========================================================= */

    /**
     * Listado general de reuniones (resumen).
     *
     * GET /api/reuniones/resumen
     */
    @GetMapping("/resumen")
    public List<ReunionResponseDTO> listarResumenTodas() {
        return reunionesListados.listarResumenTodas();
    }

    @GetMapping("/top5-generos")
        public ResponseEntity<List<GeneroCountDTO>> obtenerTopGeneros() {
        List<GeneroCountDTO> topGeneros = reunionesListados.obtenerTopGeneros();
        return new ResponseEntity<>(topGeneros, HttpStatus.OK);
    }


    @GetMapping("/top5-libros")
        public ResponseEntity<List<LibroCountDTO>> obtenerTopLibros() {
        List<LibroCountDTO> topLibros = reunionesListados.obtenerTopLibros();
        return new ResponseEntity<>(topLibros, HttpStatus.OK);
    }

    @GetMapping("/top-usuarios-activos")
    public ResponseEntity<List<UsuarioActivoDTO>> obtenerUsuariosMasActivos() {
        System.out.println("🎯 Endpoint /top-usuarios-activos ejecutado");
        List<UsuarioActivoDTO> usuariosActivos = reunionesListados.obtenerUsuariosMasActivos();
        return new ResponseEntity<>(usuariosActivos, HttpStatus.OK);
    }
    /**
     * Resumen por id de reunión.
     *
     * GET /api/reuniones/resumen/{reunionId}
     */
    @GetMapping("/resumen/{reunionId}")
    public List<ReunionResponseDTO> listarResumenPorId(
            @PathVariable("reunionId") String reunionId) {

        return reunionesListados.listarResumenPorId(reunionId);
    }

    /**
     * Resumen de reuniones asociadas a un libro.
     *
     * GET /api/reuniones/resumen/libro/{libroId}
     */
    @GetMapping("/resumen/libro/{libroId}")
    public List<ReunionResponseDTO> listarResumenPorLibro(
            @PathVariable("libroId") String libroId) {

        return reunionesListados.listarResumenPorLibro(libroId);
    }

    /**
     * Resumen por modalidad y rango de fechas.
     *
     * GET /api/reuniones/resumen/modalidad?modalidad=presencial&desde=...&hasta=...
     */
    @GetMapping("/resumen/modalidad")
    public List<ReunionResponseDTO> listarResumenPorModalidadYRango(
            @RequestParam("modalidad") String modalidad,
            @RequestParam("desde")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fechaDesde,
            @RequestParam("hasta")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date fechaHasta) {

        return reunionesListados.listarResumenPorModalidadYRango(modalidad, fechaDesde, fechaHasta);
    }

    /**
     * Próximas reuniones. Si no se envía fecha, usa "ahora".
     *
     * GET /api/reuniones/resumen/proximas
     * GET /api/reuniones/resumen/proximas?referencia=2025-11-15T00:00:00
     */
    @GetMapping("/resumen/proximas")
    public List<ReunionResponseDTO> listarResumenProximas(
            @RequestParam(name = "referencia", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date referenciaTiempo) {

        return reunionesListados.listarResumenProximas(referenciaTiempo);
    }

    /* =========================================================
     *     ARCHIVOS
     * ========================================================= */

    /**
     * Listar metadata de archivos adjuntos de una reunión.
     *
     * GET /api/reuniones/{reunionId}/archivos
     */
    @GetMapping("/{reunionId}/archivos")
    public List<ArchivoMetaSafeResponseDTO> listarArchivos(
            @PathVariable("reunionId") String reunionId) {

        return reunionesArchivos.listarArchivos(reunionId);
    }

    /**
     * Subir uno o varios archivos a una reunión.
     * multipart/form-data con campo "archivos".
     *
     * POST /api/reuniones/{reunionId}/archivos
     *
     * (SIN 'consumes' restrictivo para evitar HttpMediaTypeNotSupported con
     *  application/octet-stream; Spring seguirá esperando multipart debido a
     *  la presencia de MultipartFile).
     */
    @PostMapping(path = "/{reunionId}/archivos")
    public ArchivoUploadResponseDTO subirArchivos(
            @PathVariable("reunionId") String reunionId,
            @RequestPart("archivos") List<MultipartFile> archivosSubidos) {

        return reunionesArchivos.subirArchivos(reunionId, archivosSubidos);
    }

    

    /**
     * Descargar un archivo concreto por nombre.
     *
     * GET /api/reuniones/{reunionId}/archivos/{filename}
     */
    @GetMapping("/{reunionId}/archivos/{filename}")
    public ResponseEntity<Resource> descargarArchivo(
            @PathVariable("reunionId") String reunionId,
            @PathVariable("filename") String filename) {

        Resource recurso = reunionesArchivos.obtenerRecursoArchivo(reunionId, filename);

        String contentType = "application/octet-stream";
        String lowerName = filename.toLowerCase();
        if (lowerName.endsWith(".pdf")) {
            contentType = "application/pdf";
        } else if (lowerName.endsWith(".png")) {
            contentType = "image/png";
        } else if (lowerName.endsWith(".ppt") || lowerName.endsWith(".pptx")) {
            contentType = "application/vnd.ms-powerpoint";
        } else if (lowerName.endsWith(".docx")) {
            contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\""
                )
                .body(recurso);
    }

    /**
     * Eliminar un archivo de una reunión por nombre.
     * Recibe JSON: { "filename": "algo.pdf" }
     *
     * DELETE /api/reuniones/{reunionId}/archivos
     */
    @DeleteMapping("/{reunionId}/archivos")
    public ArchivoUploadResponseDTO eliminarArchivo(
            @PathVariable("reunionId") String reunionId,
            @Valid @RequestBody ArchivoDeleteRequestDTO deleteRequest) {

        return reunionesArchivos.eliminarArchivo(reunionId, deleteRequest);
    }

    /* =========================================================
     *     ASISTENTES
     * ========================================================= */

    /**
     * Inscribir un usuario como asistente.
     *
     * POST /api/reuniones/{reunionId}/asistentes/{usuarioId}
     */
    @PostMapping("/{reunionId}/asistentes/{usuarioId}")
    public InscripcionResponseDTO inscribirAsistente(
            @PathVariable("reunionId") String reunionId,
            @PathVariable("usuarioId") String usuarioId) {

        return reunionesAsistentes.inscribirAsistente(reunionId, usuarioId);
    }

    /**
     * Retirar un usuario de la lista de asistentes.
     *
     * DELETE /api/reuniones/{reunionId}/asistentes/{usuarioId}
     */
    @DeleteMapping("/{reunionId}/asistentes/{usuarioId}")
    public InscripcionResponseDTO retirarAsistente(
            @PathVariable("reunionId") String reunionId,
            @PathVariable("usuarioId") String usuarioId) {

        return reunionesAsistentes.retirarAsistente(reunionId, usuarioId);
    }




}
