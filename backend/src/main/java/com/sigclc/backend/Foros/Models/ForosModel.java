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
    private ObjectId id;
    private TipoEnum tipo;
    private String creadorId;
    
    @JsonProperty("id")
    public String getIdAsString(){
        return id != null ? id.toHexString():null;
    }

}
