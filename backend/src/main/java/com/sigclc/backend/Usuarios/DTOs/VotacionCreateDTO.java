package com.sigclc.backend.Usuarios.DTOs;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class VotacionCreateDTO {

    public enum Voto { Si, No }   // ← enum anidado
    private Voto voto;            // Si | No
    private Date fechaVoto;       // obligatorio
    private String usuarioId;     
    
}
