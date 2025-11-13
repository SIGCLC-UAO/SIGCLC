package com.sigclc.backend.Reseñas.Models;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "Resenias")
public class ReseniasModel {

    @Id
    private ObjectId id;

    private ObjectId libroId;
    private ObjectId autorId;
    private int calificacion;
    private String opinion;

    private List<String> archivosAdjuntos;
    private List<ComentarioResenia> comentarios;
    private Integer meGusta = 0;
    private Date fecha = new Date();
}
