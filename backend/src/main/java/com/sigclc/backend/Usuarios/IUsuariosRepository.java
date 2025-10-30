package com.sigclc.backend.Usuarios;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.apirest.backend.UsuarioPropuestaDTO.DTO;
import com.apirest.backend.Usuarios.UsuariosModel;

public interface IUsuariosRepository extends MongoRepository<UsuariosModel, ObjectId> {

    // utilidades básicas
    boolean existsByEmailIgnoreCase(String email);
    List<UsuariosModel> findByRolIgnoreCase(String rol);

    // Métricas de propuestas del usuario (votos Sí/No y total)
    @Aggregation(pipeline = {
        "{ $match: { _id: ?0 } }",
        "{ $unwind: { path: '$libroPropuesto', preserveNullAndEmptyArrays: true } }",
        "{ $addFields: { " +
            "'libroPropuesto.votosSi': { $size: { $filter: { input: { $ifNull: ['$libroPropuesto.votaciones', []] }, as: 'v', cond: { $eq: ['$$v.voto', 'Si'] } } } }," +
            "'libroPropuesto.votosNo': { $size: { $filter: { input: { $ifNull: ['$libroPropuesto.votaciones', []] }, as: 'v', cond: { $eq: ['$$v.voto', 'No'] } } } }," +
            "'libroPropuesto.totalVotos': { $size: { $ifNull: ['$libroPropuesto.votaciones', []] } }" +
        "} }",
        "{ $project: { _id: 0, " +
            "nombreUsuario: '$nombre', " +
            "libroId: '$libroPropuesto.libroId', " +
            "estado: '$libroPropuesto.estado', " +
            "votosSi: '$libroPropuesto.votosSi', " +
            "votosNo: '$libroPropuesto.votosNo', " +
            "totalVotos: '$libroPropuesto.totalVotos' } }"
    })
    List<UsuarioPropuestaDTO> metricasPropuestasPorUsuario(ObjectId usuarioId);
}

