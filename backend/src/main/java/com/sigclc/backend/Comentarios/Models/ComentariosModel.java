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

@Document("ComentarioPost")

public class ComentariosModel {
    
    @Id
    private ObjectId id;
    private String foroId;
    private String mensaje;
    private Date fechaComentario;
    private ObjectId usuarioId;
    private ObjectId comentarioId;

    @JsonProperty("id")
    public String getIdAsString(){
        return id != null ? id.toHexString():null;
    }

}

