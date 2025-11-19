package com.sigclc.backend.Comentarios.DTOs;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ComentariosResponseDTO {

    private String Id;
    private String foroId;     
    private String mensaje; 
    private Date fechaComentario;
    private String usuarioId;      
    private String comentarioId; 
}

