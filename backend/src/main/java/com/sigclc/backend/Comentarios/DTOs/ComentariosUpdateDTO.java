package com.sigclc.backend.Comentarios.DTOs;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor

public class ComentariosUpdateDTO {

    @NotBlank(message="El id de foros es obligatorio")
    private String foroId;
    @NotBlank(message="El mensaje es obligatorio")    
    private String mensaje;
    @NotBlank(message="La fecha es obligatoria")
    private Date fechaComentario; 
    @NotBlank(message="El id de usuario es obligatorio")
    private String usuarioId;
    private String comentarioId;
    
}
