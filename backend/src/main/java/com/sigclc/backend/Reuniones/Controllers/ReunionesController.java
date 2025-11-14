package com.sigclc.backend.Reuniones.Controllers;

import java.util.Date;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sigclc.backend.Reuniones.DTOs.ArchivoDeleteRequestDTO;
import com.sigclc.backend.Reuniones.DTOs.ArchivoMetaSafeResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.ArchivoUploadResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.InscripcionResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionCreateDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionUpdateDTO;
import com.sigclc.backend.Reuniones.Services.IReunionesArchivos;
import com.sigclc.backend.Reuniones.Services.IReunionesAsistentes;
import com.sigclc.backend.Reuniones.Services.IReunionesListados;
import com.sigclc.backend.Reuniones.Services.IReunionesService;

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

    /* ===================== CRUD + EXTENSIONES ===================== */

    /**
     * Crear reunión.
     * Espera un multipart/form-data con:
     *  - parte "datos": JSON de ReunionCreateDTO
     *  - parte "archivos": (opcional) lista de archivos
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ReunionResponseDTO crearReunion(
            @RequestPart("datos") @Valid ReunionCreateDTO createDTO,
            @RequestPart(value = "archivos", required = false) List<MultipartFile> archivosSubidos) {

        return reunionesService.crear(createDTO, archivosSubidos);
    }

    /**
     * Actualizar datos de una reunión existente (sin tocar archivos).
     * Recibe JSON simple.
     */
    @PutMapping("/{reunionId}")
    public ReunionResponseDTO actualizarReunion(
            @PathVariable("reunionId") String reunionId,
            @RequestBody @Valid ReunionUpdateDTO updateDTO) {

        return reunionesService.actualizar(reunionId, updateDTO);
    }

    /**
     * Eliminar reunión y todos sus archivos de GridFS.
     */
    @DeleteMapping("/{reunionId}")
    public void eliminarReunion(@PathVariable("reunionId") String reunionId) {
        reunionesService.eliminar(reunionId);
    }

    /**
     * Recalcular las extensiones adjuntas leyendo lo que haya en GridFS.
     * Útil si por alguna razón se desincronizó.
     */
    @PatchMapping("/{reunionId}/extensiones/resync")
    public ReunionResponseDTO resincronizarExtensiones(
            @PathVariable("reunionId") String reunionId) {

        return reunionesService.resyncExtensiones(reunionId);
    }

    /* ========================== LISTADOS ========================== */

    /**
     * Listado general de reuniones (resumen).
     */
    @GetMapping("/resumen")
    public List<ReunionResponseDTO> listarResumenTodas() {
        return reunionesListados.listarResumenTodas();
    }

    /**
     * Resumen por id de reunión.
     */
    @GetMapping("/resumen/{reunionId}")
    public List<ReunionResponseDTO> listarResumenPorId(
            @PathVariable("reunionId") String reunionId) {

        return reunionesListados.listarResumenPorId(reunionId);
    }

    /**
     * Resumen de reuniones asociadas a un libro.
     */
    @GetMapping("/resumen/libro/{libroId}")
    public List<ReunionResponseDTO> listarResumenPorLibro(
            @PathVariable("libroId") String libroId) {

        return reunionesListados.listarResumenPorLibro(libroId);
    }

    /**
     * Resumen por modalidad y rango de fechas.
     * Ejemplo de llamada:
     * /api/reuniones/resumen/modalidad?modalidad=presencial&desde=2025-11-10T00:00:00&hasta=2025-11-30T23:59:59
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
     * Ejemplo: /api/reuniones/resumen/proximas
     *          /api/reuniones/resumen/proximas?referencia=2025-11-15T00:00:00
     */
    @GetMapping("/resumen/proximas")
    public List<ReunionResponseDTO> listarResumenProximas(
            @RequestParam(name = "referencia", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date referenciaTiempo) {

        return reunionesListados.listarResumenProximas(referenciaTiempo);
    }

    /* ========================== ARCHIVOS ========================== */

    /**
     * Listar metadata de archivos adjuntos de una reunión.
     */
    @GetMapping("/{reunionId}/archivos")
    public List<ArchivoMetaSafeResponseDTO> listarArchivos(
            @PathVariable("reunionId") String reunionId) {

        return reunionesArchivos.listarArchivos(reunionId);
    }

    /**
     * Subir uno o varios archivos a una reunión.
     * multipart/form-data con campo "archivos".
     */
    @PostMapping(
            path = "/{reunionId}/archivos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ArchivoUploadResponseDTO subirArchivos(
            @PathVariable("reunionId") String reunionId,
            @RequestPart("archivos") List<MultipartFile> archivosSubidos) {

        return reunionesArchivos.subirArchivos(reunionId, archivosSubidos);
    }

    /**
     * Eliminar un archivo de una reunión por nombre de archivo.
     * Recibe JSON: { "filename": "algo.pdf" }
     */
    @DeleteMapping("/{reunionId}/archivos")
    public ArchivoUploadResponseDTO eliminarArchivo(
            @PathVariable("reunionId") String reunionId,
            @RequestBody @Valid ArchivoDeleteRequestDTO deleteRequest) {

        return reunionesArchivos.eliminarArchivo(reunionId, deleteRequest);
    }

    /* ========================= ASISTENTES ========================= */

    /**
     * Inscribir un usuario como asistente.
     */
    @PostMapping("/{reunionId}/asistentes/{usuarioId}")
    public InscripcionResponseDTO inscribirAsistente(
            @PathVariable("reunionId") String reunionId,
            @PathVariable("usuarioId") String usuarioId) {

        return reunionesAsistentes.inscribirAsistente(reunionId, usuarioId);
    }

    /**
     * Retirar un usuario de la lista de asistentes.
     */
    @DeleteMapping("/{reunionId}/asistentes/{usuarioId}")
    public InscripcionResponseDTO retirarAsistente(
            @PathVariable("reunionId") String reunionId,
            @PathVariable("usuarioId") String usuarioId) {

        return reunionesAsistentes.retirarAsistente(reunionId, usuarioId);
    }
}
