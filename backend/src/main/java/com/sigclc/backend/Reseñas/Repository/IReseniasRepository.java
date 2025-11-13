package com.sigclc.backend.Reseñas.Repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.sigclc.backend.Reseñas.Models.ReseniasModel;

public interface IReseniasRepository extends MongoRepository<ReseniasModel, ObjectId> {
    List<ReseniasModel> findByLibroId(ObjectId libroId);
}
