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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Documento de Reuniones (SIGCLC) + soporte GridFS (sin cambiar el MER persistido).
 *
 * Persistimos exactamente los campos del MER:
 *  - _id: ObjectId
 *  - fechaHoraInicio: Date
 *  - modalidad: "presencial" | "virtual" (String)
 *  - lugar: String (solo presencial)
 *  - enlace: String (solo virtual)
 *  - asistentes: List<ObjectId> (Usuarios._id)
 *  - archivosAdjuntos: List<String> (".pdf", ".pptx", ".png")  <-- set de extensiones presentes
 *  - libroId: ObjectId (Libros._id)
 *
 * Y añadimos un campo @Transient para exponer, cuando se necesite, el resumen
 * de archivos reales almacenados en GridFS (no se guarda dentro del documento).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@Document(collection = "Reuniones")
@CompoundIndexes({
    // Histórico/agenda por libro y orden cronológico
    @CompoundIndex(name = "idx_libroId_fechaHoraInicio", def = "{ 'libroId': 1, 'fechaHoraInicio': 1 }"),
    // Agenda del usuario (asistente) por fecha (multikey + fecha)
    @CompoundIndex(name = "idx_asistentes_fechaHoraInicio", def = "{ 'asistentes': 1, 'fechaHoraInicio': 1 }"),
    // Filtro por modalidad en rango temporal
    @CompoundIndex(name = "idx_modalidad_fechaHoraInicio", def = "{ 'modalidad': 1, 'fechaHoraInicio': 1 }")
})
public class ReunionesModel {

    @Id
    private ObjectId id;

    @Indexed(name = "idx_fechaHoraInicio")
    private Date fechaHoraInicio;
    private String modalidad;
    private String lugar;
    private String enlace;
    private List<ObjectId> asistentes;

    /**
     * Solo extensiones permitidas (whitelist validada en servicio):
     *   .pdf, .pptx, .png
     * Representa el SET de tipos presentes en GridFS para esta reunión.
     */
    private List<String> archivosAdjuntos;

    /** Referencia al libro asociado (Libros._id) */
    @Indexed(name = "idx_libroId")
    private ObjectId libroId;

    /**
     * Vista NO persistida de los archivos reales en GridFS.
     * El servicio la completa a partir de GridFS (metadata.reunionId = this.id).
     */
    @Transient
    private List<ReunionArchivoView> archivosGrid;

    /**
     * Constantes de modalidad para evitar literales sueltos en la app.
     * No cambia el tipo persistido (sigue siendo String).
     */
    public static final class Modalidad {
        public static final String PRESENCIAL = "presencial";
        public static final String VIRTUAL = "virtual";
        private Modalidad() {}
    }
}
