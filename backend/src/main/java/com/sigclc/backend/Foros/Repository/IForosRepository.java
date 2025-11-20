package com.sigclc.backend.Foros.Repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.sigclc.backend.Foros.Models.ForosModel;

public interface IForosRepository extends MongoRepository<ForosModel, ObjectId>{
    
    public List<ForosModel> listarForos();
    public ForosModel buscarForoPorId(String Id);
    public List<ForosModel> buscarPorTipo(String tipo);
    public List<ForosModel> buscarPorCreadorId(String creadorId);
    
}
