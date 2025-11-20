package com.sigclc.backend.Foros.Repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.sigclc.backend.Foros.Models.ForosModel;

public interface IForosRepository extends MongoRepository<ForosModel, ObjectId>{
    

    @Aggregation(pipeline = {
        "{ '$match': { '_id': { $oid: ?0 } } }"
    })
    public ForosModel buscarForoPorId(String Id);
    @Aggregation(pipeline = {
        "{ '$match': { 'tipo': ?0 } }",
    })
    public List<ForosModel> buscarPorTipo(String tipo);
    @Aggregation(pipeline = {
        "{ '$match': { 'creadorId': ?0 } }"
        })
    public List<ForosModel> buscarPorCreadorId(String creadorId);
    
}
