package com.sigclc.backend.Reseñas.Repository;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.sigclc.backend.Reseñas.Models.ReseniasModel;

public interface IReseniasRepository extends MongoRepository<ReseniasModel, ObjectId> {

    List<ReseniasModel> findByLibroId(ObjectId libroId);
    
    List<ReseniasModel> findByAutorId(ObjectId autorId);
    
    Optional<ReseniasModel> findByAutorIdAndLibroId(ObjectId autorId, ObjectId libroId);
    
    @Aggregation(pipeline = {
        "{ $group: { _id: '$autorId', count: { $sum: 1 } } }",
        "{ $sort: { count: -1 } }",
        "{ $limit: ?0 }"
    })
    List<Object> findTopReviewers(int limit);
    
    @Aggregation(pipeline = {
        "{ $match: { libroId: ?0 } }",
        "{ $group: { _id: '$libroId', promedio: { $avg: '$calificacion' }, total: { $sum: 1 } } }"
    })
    Object findStatsByLibro(ObjectId libroId);
}