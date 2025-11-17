package com.sigclc.backend.Usuarios.DTOs;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor
public class VotoCreateResponseDTO {

    private String usuarioIdPropuesta; 
    private String libroId;            
    private String votanteId;          
    private String voto;               
    private Date   fechaVoto;          

    private String estadoPropuesta;    // "En Votacion" | "Seleccionado" | "No Seleccionado"
}
