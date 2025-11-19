package com.sigclc.backend.Comentarios.Repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.sigclc.backend.Comentarios.DTOs.ComentariosResponseDTO;
import com.sigclc.backend.Comentarios.DTOs.ComentariosUpdateDTO;
import com.sigclc.backend.Comentarios.Models.ComentariosModel;

@Repository

public interface  IComentariosRepository extends  MongoRepository<ComentariosModel, ObjectId>{
        
    @Aggregation(pipeline = {
        "{ $match: {} }"
    })
    public List<ComentariosResponseDTO> listarComentarios();
    @Aggregation(pipeline={
        "{$match: {_id:?0 } }"
    })
    public ComentariosResponseDTO buscarPorId(String Id);
    @Aggregation(pipeline = {
        "{ $match: { foroId:?0 } }"
    })
    public List<ComentariosResponseDTO> buscarPorForoId(String foroId);
    @Aggregation(pipeline = {
        "{ $match: { fechaComentario: ?0 } }"
    })
    public List<ComentariosResponseDTO> buscarPorFecha (Date fecha);
    @Aggregation(pipeline = {
        "{ $match: { usuarioId: ObjectId(?0) } }"
    })
    public List<ComentariosResponseDTO> buscarPorUsuario (String usuarioId);
    @Aggregation(pipeline = {
        "{ $match: { comentarioId: ObjectId(?0) } }"
    })
    public List<ComentariosResponseDTO> buscarPorComentarioId (String comentarioId);
    public ComentariosResponseDTO actualizarComentario(String id, ComentariosUpdateDTO dto);
    public void eliminarComentario (String id);
    
}

