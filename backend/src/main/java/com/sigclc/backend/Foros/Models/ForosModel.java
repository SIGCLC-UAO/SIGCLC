package com.sigclc.backend.Foros.Models;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Document("Foros")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ForosModel {
    @Id
    private ObjectId Id;
    private TipoEnum tipo;
    private String creadorId;
    
    @JsonProperty("Id")
    public String getIdAsString(){
        return Id != null ? Id.toHexString():null;
    }

}
