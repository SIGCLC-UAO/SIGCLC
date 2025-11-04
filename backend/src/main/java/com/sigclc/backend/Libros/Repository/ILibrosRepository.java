package com.sigclc.backend.Libros.Repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.sigclc.backend.Libros.Models.LibrosModel;



public interface  ILibrosRepository extends  MongoRepository<LibrosModel, ObjectId>{

    @Aggregation(pipeline ={
        "{$match: {'titulo': {$regex: ?0, $options:'i'}}}"
    })
    List<LibrosModel> buscarPorTitulo(String titulo);

    @Aggregation(pipeline ={
        "{$match: {'autor': {$regex: ?0, $options:'i'}}}"
    })
    List<LibrosModel> buscarPorAutor(String autor);

    @Aggregation(pipeline = {
        "{$match: {anioPublicacion: ?0 } }"
    })
    List<LibrosModel> buscarPorAnioPublicacion(Long anioPublicacion);

    @Aggregation(pipeline = {
        "{$match: { fechaSeleccion: ?0 } }"
    })
    List<LibrosModel> buscarPorFechaSeleccion(Date fechaSeleccion);

    @Aggregation(pipeline ={
        "{$match: {'genero':{$regex: ?0, $options:'i'}}}"
    })
    List<LibrosModel> buscarPorGenero(String genero); 
}



