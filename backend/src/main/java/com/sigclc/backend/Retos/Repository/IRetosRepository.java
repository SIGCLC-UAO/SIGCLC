package com.sigclc.backend.Retos.Repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sigclc.backend.Retos.Models.RetosModel;

@Repository

public interface IRetosRepository extends  MongoRepository<RetosModel, ObjectId> {


    @Aggregation(pipeline = {
    "{$match: {'titulo': {$regex: ?0, $options: 'i'}}}"
    }) 
    List<RetosModel> buscarPorTitulo(String titulo);
    @Aggregation(pipeline = {
    "{$match: {'descripcion': {$regex: ?0, $options: 'i'}}}"
    })
    List<RetosModel> buscarPorDescripcion(String descripcion);

    @Aggregation(pipeline = {
    "{$match: { 'libroAsociados.libroId': ?0 }}"
    })
    List<RetosModel> buscarRetosPorLibro(ObjectId libroId);
    @Aggregation(pipeline = {
    "{$match: {_id: ?0}}",
    "{$unwind: '$libroAsociados'}",
    "{$project: {_id: 0, libroId: { $toString: '$libroAsociados.libroId' }}}"
    })
    List<String> listarLibrosEnRetos(ObjectId idReto);
    @Aggregation(pipeline = {
    "{$match: {'participantes.usuarioId': ?0}}",
    "{$project: {_id: 1}}",
    "{$project: {_id: 0, retoId: { $toString: '$_id' }}}"
    })
    List<String> listarRetosPorUsuario(ObjectId usuarioId);

    @Aggregation(pipeline = {
    "{$match: {_id: ?0}}",
    "{$project: {count: {$size: '$participantes'}}}"
    })
    Integer contarParticipantes (ObjectId idReto);
    @Aggregation(pipeline = {
    "{$match: {_id: ?0}}",
    "{$unwind: '$participantes'}",
    "{$project: {_id: 0, usuarioId: '$participantes.usuarioId'}}"
    })
    List<String> listarUsuariosInscritos(ObjectId idReto);
    @Aggregation(pipeline = {
        "{$match: {'fechaInicio': {$eq: ?0}}}"
    })
    List<RetosModel> buscarRetosPorFechaInicio(Date inicio);

    @Aggregation(pipeline = {
        "{$match: {'fechaFin': {$eq: ?0}}}"
    })
    List<RetosModel> buscarRetosPorFechaFin(Date fin);
}

