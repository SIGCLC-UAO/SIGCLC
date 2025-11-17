package com.sigclc.backend.Usuarios.Repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.sigclc.backend.Usuarios.DTOs.PropuestaCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.TopLibroVotosDTO;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;

public interface IUsuariosRepository extends MongoRepository<UsuariosModel, ObjectId> {

    // Unicidad básicas
    boolean existsByEmail(String email);
    boolean existsByTelefono(Object telefono);

    // ¿El usuario ya tiene propuesta para ese libro?
    @Query(value = "{ '_id': ?0, 'libroPropuesto': { $elemMatch: { 'libroId': ?1 } } }", exists = true)
    boolean existsPropuesta(ObjectId usuarioId, ObjectId libroId);

    // Buscar todos los usuarios que contengan votos del usuario indicado
    @Query(value = "{ 'libroPropuesto.votaciones.usuarioId': ?0 }")
    List<UsuariosModel> findAllConVotoDeUsuario(ObjectId usuarioId);

    // Agregación: Top global por votos "Si"
    @Aggregation(pipeline = {
        "{ $unwind: '$libroPropuesto' }",
        "{ $addFields: { " +
            "'libroPropuesto.votosSi': { $size: { $filter: { input: { $ifNull: ['$libroPropuesto.votaciones', []] }, as: 'v', cond: { $eq: ['$$v.voto', 'Si'] } } } }," +
            "'libroPropuesto.votosNo': { $size: { $filter: { input: { $ifNull: ['$libroPropuesto.votaciones', []] }, as: 'v', cond: { $eq: ['$$v.voto', 'No'] } } } }," +
            "'libroPropuesto.totalVotos': { $size: { $ifNull: ['$libroPropuesto.votaciones', []] } }" +
        "} }",
        "{ $group: { _id: '$libroPropuesto.libroId', " +
            "votosSi: { $sum: '$libroPropuesto.votosSi' }, " +
            "votosNo: { $sum: '$libroPropuesto.votosNo' }, " +
            "totalVotos: { $sum: '$libroPropuesto.totalVotos' } } }",
        "{ $lookup: { from: 'Libros', localField: '_id', foreignField: '_id', as: 'libro' } }",
        "{ $unwind: { path: '$libro', preserveNullAndEmptyArrays: true } }",
        "{ $project: { _id: 0, " +
            "libroId: { $toString: '$_id' }, titulo: '$libro.titulo', autor: '$libro.autor', " +
            "votosSi: 1, votosNo: 1, totalVotos: 1 } }",
        "{ $sort: { votosSi: -1, totalVotos: -1 } }",
        "{ $limit: 1 }"
    })
    List<TopLibroVotosDTO> topLibroMasVotado();

    // Agregación: propuestas de un usuario con métricas
    @Aggregation(pipeline = {
        "{ $match: { _id: ?0 } }",
        "{ $unwind: { path: '$libroPropuesto', preserveNullAndEmptyArrays: false } }",
        "{ $addFields: { " +
            "'libroPropuesto.votosSi': { $size: { $filter: { input: { $ifNull: ['$libroPropuesto.votaciones', []] }, as: 'v', cond: { $eq: ['$$v.voto', 'Si'] } } } }," +
            "'libroPropuesto.votosNo': { $size: { $filter: { input: { $ifNull: ['$libroPropuesto.votaciones', []] }, as: 'v', cond: { $eq: ['$$v.voto', 'No'] } } } }," +
            "'libroPropuesto.totalVotos': { $size: { $ifNull: ['$libroPropuesto.votaciones', []] } }" +
        "} }",
        "{ $project: { _id: 0, " +
            "nombreUsuario: { $concat: [ { $ifNull: ['$nombre',''] }, ' ', { $ifNull: ['$apellido',''] } ] }, " +
            "libroId: { $toString: '$libroPropuesto.libroId' }, " +
            "estado: '$libroPropuesto.estado', " +
            "votosSi: '$libroPropuesto.votosSi', votosNo: '$libroPropuesto.votosNo', totalVotos: '$libroPropuesto.totalVotos' } }",
        "{ $sort: { estado: 1 } }"
    })
    List<PropuestaCreateDTO> propuestasDetalladasDeUsuario(ObjectId usuarioId);
}
