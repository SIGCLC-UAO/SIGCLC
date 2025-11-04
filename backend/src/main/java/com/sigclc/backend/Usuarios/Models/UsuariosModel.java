package com.sigclc.backend.Usuarios.Models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*; // NotNull, NotBlank, Min, Max, Pattern

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuariosModel {

    @Id
    private ObjectId id;

    // ===== Campos obligatorios =====
    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    private String apellido;

    @NotBlank(message = "El email es obligatorio.")
    @Indexed(unique = true) // índice único en BD
    private String email;

    @NotBlank(message = "El rol es obligatorio.")
    @Pattern(
        regexp = "^(lector|moderador|administrador)$",
        message = "Rol inválido. Use: lector | moderador | administrador."
    )
    private String rol;

    @NotNull(message = "El teléfono es obligatorio.")
    @Indexed(unique = true) // índice único en BD
    private Long telefono;

    @NotNull(message = "La edad es obligatoria.")
    private Long edad;

    @NotBlank(message = "La ocupación es obligatoria.")
    private String ocupacion;

    // ===== Embebidos =====
    private List<LibroPropuesto> libroPropuesto = new ArrayList<>();

    // Exponer _id como "id" (hex) en respuestas JSON
    @JsonProperty("id")
    public String getIdAsString() {
        return id != null ? id.toHexString() : null;
    }

    // ================== Subdocumento: LibroPropuesto ==================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LibroPropuesto {

        @NotBlank(message = "El estado de la propuesta es obligatorio.")
        @Pattern(
            regexp = "^(Seleccionado|No Seleccionado|En Votacion)$",
            message = "Estado inválido. Use: Seleccionado | No Seleccionado | En Votacion."
        )
        private String estado;

        @NotNull(message = "El libroId es obligatorio en la propuesta.")
        private ObjectId libroId;

        private List<Votacion> votaciones = new ArrayList<>();
    }

    // ===================== Subdocumento: Votacion =====================
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Votacion {

        @NotBlank(message = "El voto es obligatorio.")
        @Pattern(
            regexp = "^(Si|No)$",
            message = "Voto inválido. Use: Si | No."
        )
        private String voto;

        @NotNull(message = "La fecha del voto es obligatoria.")
        private Date fechaVoto; 

        @NotNull(message = "El usuarioId del votante es obligatorio.")
        private ObjectId usuarioId;
    }
}
