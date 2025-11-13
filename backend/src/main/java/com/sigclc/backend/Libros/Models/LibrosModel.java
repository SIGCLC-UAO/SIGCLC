package com.sigclc.backend.Libros.Models;



import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Document("Libros")
@Data
@AllArgsConstructor
@NoArgsConstructor




public class LibrosModel {
    @Id
    private ObjectId id;
    private String titulo;
    private String autor;
    private Long anioPublicacion;
    private EstadoLibro estado; //pendiente, en lectura,leido
    private Date fechaSeleccion;
    private String sinopsis;
    private String portada;
    private String genero;
 

    @JsonProperty("id")
    public String getIdAsString(){
        return id != null ? id.toHexString():null;
    }


}

