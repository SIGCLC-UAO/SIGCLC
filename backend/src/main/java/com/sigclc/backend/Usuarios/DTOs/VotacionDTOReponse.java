// src/main/java/com/sigclc_backend/DTO/VotacionDTO.java
package com.sigclc.backend.Usuarios.DTOs;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class VotacionDTOReponse {
    private VotacionCreateDTO.Voto voto;  // referencia al enum anidado
    private Date fechaVoto;
    private String usuarioId;
}
