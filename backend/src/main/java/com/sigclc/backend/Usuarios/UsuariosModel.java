package com.sigclc.backend.Usuarios;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    private String nombre;
    private String apellido;
    private String email;
    private String rol;          // "lector" | "moderador" | "administrador"
    private Long telefono;     
    private Integer edad;           
    private String ocupacion;

    private List<LibroPropuesto> libroPropuesto = new ArrayList<>();

    @JsonProperty("id")
    public String getIdAsString() {
        return id != null ? id.toHexString() : null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LibroPropuesto {
        private String estado;               // "Seleccionado" | "No Seleccionado" | "En Votacion"
        private ObjectId libroId;           
        private List<Votacion> votaciones = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Votacion {
        private String voto;                 // "Si" | "No"
        private String fechaVoto;            
        private ObjectId usuarioId;          // ObjectId
    }
}
