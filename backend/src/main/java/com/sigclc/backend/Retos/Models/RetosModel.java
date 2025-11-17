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
    public ObjectId id;
    public String titulo;
    public Date fechaInicio;
    public Date fechaFin;
    public String descripcion;
    @Field("libroAsociados")
    public List<librosAsociados>libroAsociados;
    public List<Participantes>participantes;

    
    @JsonProperty("id")
    public String getIdAsString(){
        return id != null ? id.toHexString():null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public  static class librosAsociados{
        public ObjectId libroId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor    
    public static class Participantes{
        public ObjectId usuarioId;
        public List<Progreso>progreso;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Progreso{
        public ObjectId libroId;
        public String progreso;
    }

}
