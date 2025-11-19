package com.sigclc.backend.Reseñas.Services;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.sigclc.backend.Reseñas.DTOs.ArchivoReseniaMetaDTO;
import com.sigclc.backend.Reseñas.DTOs.ArchivoReseniaUploadResponseDTO;

/**
 * Servicio para gestionar archivos de reseñas en GridFS.
 * Patrón similar a ReunionesArchivosService.
 */
public interface IReseniasArchivosService {

    /**
     * Listar metadata de archivos adjuntos de una reseña.
     * @param reseniaId ID de la reseña
     * @return Lista de metadata de archivos
     */
    List<ArchivoReseniaMetaDTO> listarArchivos(String reseniaId);

    /**
     * Subir uno o varios archivos a una reseña existente.
     * @param reseniaId ID de la reseña
     * @param archivosSubidos Lista de archivos MultipartFile
     * @return Response con archivos subidos y extensiones actualizadas
     */
    ArchivoReseniaUploadResponseDTO subirArchivos(String reseniaId, List<MultipartFile> archivosSubidos);

    /**
     * Eliminar un archivo de una reseña por nombre.
     * @param reseniaId ID de la reseña
     * @param filename Nombre del archivo a eliminar
     * @return Response actualizado después de eliminar
     */
    ArchivoReseniaUploadResponseDTO eliminarArchivo(String reseniaId, String filename);

    /**
     * Obtener recurso de GridFS para descargar archivo.
     * @param reseniaId ID de la reseña
     * @param filename Nombre del archivo
     * @return Resource para descarga
     */
    Resource obtenerRecursoArchivo(String reseniaId, String filename);

    /**
     * Eliminar todos los archivos asociados a una reseña (usado al eliminar reseña).
     * @param reseniaId ID de la reseña
     */
    void eliminarTodosArchivos(String reseniaId);
}