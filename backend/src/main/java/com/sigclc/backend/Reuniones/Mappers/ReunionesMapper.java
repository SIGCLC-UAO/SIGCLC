package com.sigclc.backend.Reuniones.Mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.sigclc.backend.Reuniones.DTOs.ArchivoMetaSafeResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.ArchivoUploadResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionCreateDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionUpdateDTO;
import com.sigclc.backend.Reuniones.Models.ReunionArchivoView;
import com.sigclc.backend.Reuniones.Models.ReunionesModel;

@Component
public class ReunionesMapper {

    /* ===========================
     *  CREATE: DTO -> ENTITY
     * =========================== */
    public ReunionesModel toEntityFromCreate(ReunionCreateDTO dto) {
        if (dto == null) return null;

        ReunionesModel entity = new ReunionesModel();
        entity.setFechaHoraInicio(parseIsoToDate(dto.getFechaHoraInicio()));
        entity.setModalidad(safeLower(dto.getModalidad()));
        entity.setLugar(emptyToNull(dto.getLugar()));
        entity.setEnlace(emptyToNull(dto.getEnlace()));
        entity.setLibroId(toObjectId(dto.getLibroId()));
        entity.setAsistentes(toObjectIdList(dto.getAsistentesIds()));
        // Al crear aún no hay adjuntos; el Service actualizará después de subir a GridFS
        entity.setArchivosAdjuntos(new ArrayList<>());
        return entity;
    }

    /* ==================================
     *  UPDATE: aplica DTO sobre ENTITY
     * ================================== */
    public void applyUpdate(ReunionesModel entity, ReunionUpdateDTO dto) {
        if (entity == null || dto == null) return;

        if (notBlank(dto.getFechaHoraInicio())) {
            entity.setFechaHoraInicio(parseIsoToDate(dto.getFechaHoraInicio()));
        }
        if (notBlank(dto.getModalidad())) {
            entity.setModalidad(safeLower(dto.getModalidad()));
        }
        if (dto.getLugar() != null) {
            entity.setLugar(emptyToNull(dto.getLugar()));
        }
        if (dto.getEnlace() != null) {
            entity.setEnlace(emptyToNull(dto.getEnlace()));
        }
        if (dto.getAsistentesIds() != null) {
            entity.setAsistentes(toObjectIdList(dto.getAsistentesIds()));
        }
    }

    /* ==========================================
     *  ENTITY -> RESPONSE (resumen esencial)
     *  El Service resuelve libroTitulo/libroAutor
     * ========================================== */
    public ReunionResponseDTO toResponse(ReunionesModel entity, String libroTitulo, String libroAutor) {
        if (entity == null) return null;

        ReunionResponseDTO dto = new ReunionResponseDTO();
        dto.setFechaHoraInicio(formatDateToIso(entity.getFechaHoraInicio()));
        dto.setModalidad(entity.getModalidad());
        dto.setLibroTitulo(libroTitulo);
        dto.setLibroAutor(libroAutor);
        dto.setExtensionesAdjuntas(entity.getArchivosAdjuntos());
        return dto;
    }

    /* ==========================================
     *  ARCHIVOS (GridFS metadata -> DTO seguro)
     * ========================================== */
    public List<ArchivoMetaSafeResponseDTO> toArchivoMetaList(List<ReunionArchivoView> views) {
        List<ArchivoMetaSafeResponseDTO> out = new ArrayList<>();
        if (views == null) return out;

        for (ReunionArchivoView v : views) {
            if (v == null) continue;
            ArchivoMetaSafeResponseDTO meta = new ArchivoMetaSafeResponseDTO();
            meta.setFilename(v.getFilename());
            meta.setExtension(v.getExtension());
            meta.setLength(v.getLength());
            meta.setUploadDate(formatDateToIso(v.getUploadDate()));
            out.add(meta);
        }
        return out;
    }

    public ArchivoUploadResponseDTO toArchivoUploadResponse(List<ReunionArchivoView> views, List<String> extensiones) {
        ArchivoUploadResponseDTO dto = new ArchivoUploadResponseDTO();
        dto.setArchivos(toArchivoMetaList(views));
        dto.setExtensionesAdjuntas(extensiones != null ? extensiones : new ArrayList<>());
        return dto;
    }

    /* ====================== utilidades ====================== */

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String emptyToNull(String s) {
        return notBlank(s) ? s.trim() : null;
    }

    private static String safeLower(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }

    private static Date parseIsoToDate(String iso) {
        if (!notBlank(iso)) return null;
        // Acepta: "2025-11-13T15:00:00Z", "2025-11-13T15:00:00-05:00" o "2025-11-13T15:00:00"
        try {
            return Date.from(OffsetDateTime.parse(iso, DateTimeFormatter.ISO_DATE_TIME).toInstant());
        } catch (Exception ignore) {}
        try {
            return Date.from(Instant.parse(iso));
        } catch (Exception ignore) {}
        try {
            LocalDateTime ldt = LocalDateTime.parse(iso, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return Date.from(ldt.toInstant(ZoneOffset.UTC));
        } catch (Exception e) {
            throw new IllegalArgumentException("Fecha inválida (use ISO-8601): " + iso);
        }
    }

    private static String formatDateToIso(Date date) {
        if (date == null) return null;
        return date.toInstant().toString(); // ISO-8601 con 'Z'
    }

    private static ObjectId toObjectId(String hex) {
        if (!notBlank(hex)) return null;
        return new ObjectId(hex.trim());
    }

    private static List<ObjectId> toObjectIdList(List<String> ids) {
        List<ObjectId> out = new ArrayList<>();
        if (ids == null) return out;
        for (String s : ids) {
            if (notBlank(s)) {
                out.add(new ObjectId(s.trim()));
            }
        }
        return out;
    }
}
