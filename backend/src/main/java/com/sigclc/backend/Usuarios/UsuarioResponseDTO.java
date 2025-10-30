package com.sigclc.backend.Usuarios;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDTO {
    private String id;          // ObjectId en string (hex)
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private Long telefono;
    private Long edad;
    private String ocupacion;
    private String newField;    // "New Field"
    private List<LibroPropuestoDTO> libroPropuesto;

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class LibroPropuestoDTO {
        private String estado;               // "Seleccionado" | "No Seleccionado" | "En Votacion"
        private String libroId;              // string
        private List<VotacionDTO> votaciones;
    }

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class VotacionDTO {
        private String voto;                 // "Si" | "No"
        private String fechaVoto;            // string (ISO-8601 recomendado)
        private String usuarioId;            // ObjectId en string
    }
}
