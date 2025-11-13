package com.sigclc.backend.Reuniones.Models;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Reuniones")
@CompoundIndexes({
    @CompoundIndex(name = "idx_libroId_fechaHoraInicio", def = "{ 'libroId': 1, 'fechaHoraInicio': 1 }"),
    @CompoundIndex(name = "idx_asistentes_fechaHoraInicio", def = "{ 'asistentes': 1, 'fechaHoraInicio': 1 }"),
    @CompoundIndex(name = "idx_modalidad_fechaHoraInicio", def = "{ 'modalidad': 1, 'fechaHoraInicio': 1 }")
})

public class ReunionesModel {

    @Id
    private ObjectId id;

    @NotNull
    @Indexed(name = "idx_fechaHoraInicio")
    private Date fechaHoraInicio;

    @NotNull
    @Pattern(regexp = "^(presencial|virtual)$", message = "modalidad debe ser 'presencial' o 'virtual'")
    private String modalidad;

    private String lugar;

    private String enlace;

    private List<ObjectId> asistentes;

    private List<
        @Pattern(
            regexp = "^\\.(png|ppt|pdf|docx)$",
            message = "Solo se permiten extensiones .png, .ppt, .pdf, .docx"
        )
        String
    > archivosAdjuntos;

    /** Referencia al libro asociado (Libros._id). */
    @NotNull
    @Indexed(name = "idx_libroId")
    private ObjectId libroId;

    /**
     * Vista NO persistida con metadata de archivos reales en GridFS.
     * La completa el Service leyendo GridFS (metadata.reunionId = this.id).
     */
    @Transient
    private List<ReunionArchivoView> archivosGrid;

    public static final class Patterns {
        public static final String MODALIDAD = "^(presencial|virtual)$";
        public static final String EXT_WHITELIST = "^\\.(png|ppt|pdf|docx)$";
    }
    public static final class Messages {
        public static final String MODALIDAD = "modalidad debe ser 'presencial' o 'virtual'";
        public static final String EXT_WHITELIST = "Solo se permiten extensiones .png, .ppt, .pdf, .docx";
    }

}
