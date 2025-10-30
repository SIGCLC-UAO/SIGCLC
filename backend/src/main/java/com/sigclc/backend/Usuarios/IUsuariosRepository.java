package com.sigclc.backend.Usuarios;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface IUsuariosRepository extends MongoRepository<UsuariosModel, ObjectId> {

    /**
     * Top global: libro con más votos "Si" (y métricas).
     * Devuelve 0..1 resultados ordenados por votosSi y totalVotos.
     */
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
            "libroId: { $toString: '$_id' }, " +
            "titulo: '$libro.titulo', " +
            "autor: '$libro.autor', " +
            "votosSi: 1, votosNo: 1, totalVotos: 1 } }",
        "{ $sort: { votosSi: -1, totalVotos: -1 } }",
        "{ $limit: 1 }"
    })
    List<TopLibroVotosDTO> topLibroMasVotado();

    /**
     * Propuestas de un usuario por _id con detalle del libro y métricas.
     */
    @Aggregation(pipeline = {
        "{ $match: { _id: ?0 } }",
        "{ $unwind: { path: '$libroPropuesto', preserveNullAndEmptyArrays: false } }",
        "{ $addFields: { " +
            "'libroPropuesto.votosSi': { $size: { $filter: { input: { $ifNull: ['$libroPropuesto.votaciones', []] }, as: 'v', cond: { $eq: ['$$v.voto', 'Si'] } } } }," +
            "'libroPropuesto.votosNo': { $size: { $filter: { input: { $ifNull: ['$libroPropuesto.votaciones', []] }, as: 'v', cond: { $eq: ['$$v.voto', 'No'] } } } }," +
            "'libroPropuesto.totalVotos': { $size: { $ifNull: ['$libroPropuesto.votaciones', []] } }" +
        "} }",
        "{ $lookup: { from: 'Libros', localField: 'libroPropuesto.libroId', foreignField: '_id', as: 'libro' } }",
        "{ $unwind: { path: '$libro', preserveNullAndEmptyArrays: true } }",
        "{ $project: { _id: 0, " +
            "nombreUsuario: '$nombre', " +
            "libroId: { $toString: '$libroPropuesto.libroId' }, " +
            "titulo: '$libro.titulo', " +
            "autor: '$libro.autor', " +
            "estado: '$libroPropuesto.estado', " +
            "votosSi: '$libroPropuesto.votosSi', " +
            "votosNo: '$libroPropuesto.votosNo', " +
            "totalVotos: '$libroPropuesto.totalVotos' } }",
        "{ $sort: { estado: 1 } }"
    })
    List<UsuarioPropuestaDTO> propuestasDetalladasDeUsuario(ObjectId usuarioId);
}
