package com.sigclc.backend.Comentarios.Models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Document("Comentarios")

public class ComentariosModel {
    
    @Id
    private ObjectId Id;
    private String foroId;
    private String mensaje;
    private Date fechaComentario;
    private ObjectId usuarioId;
    private ObjectId comentarioId;

    @JsonProperty("Id")
    public String getIdAsString(){
        return Id != null ? Id.toHexString():null;
    }

}

