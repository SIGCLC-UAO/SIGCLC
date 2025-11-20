package com.sigclc.backend.Retos.Models;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Document("Retos")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class RetosModel {
    
    @Id
    private ObjectId id;
    private String titulo;
    private Date fechaInicio;
    private Date fechaFin;
    private String descripcion;
    @Field("libroAsociados")
    private List<librosAsociados>libroAsociados;
    private List<Participantes>participantes;

    
    @JsonProperty("id")
    public String getIdAsString(){
        return id != null ? id.toHexString():null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public  static class librosAsociados{
        private ObjectId libroId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor    
    public static class Participantes{
        private ObjectId usuarioId;
        private List<Progreso>progreso;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Progreso{
        private ObjectId libroId;
        private String progreso;
    }

}
