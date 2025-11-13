package com.sigclc.backend.Reuniones.Repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.sigclc.backend.Reuniones.DTOs.ReunionResponseDTO;
import com.sigclc.backend.Reuniones.Models.ReunionesModel;

public interface IReunionesRepository extends MongoRepository<ReunionesModel, ObjectId> {

    // 1) Listado general (orden cronológico asc), con join a Libros
    @Aggregation(pipeline = {
        "{ $lookup: { from: 'Libros', localField: 'libroId', foreignField: '_id', as: 'libro' } }",
        "{ $unwind: { path: '$libro', preserveNullAndEmptyArrays: true } }",
        "{ $sort: { fechaHoraInicio: 1 } }",
        "{ $project: { _id: 0, " +
            "fechaHoraInicio: { $dateToString: { format: '%Y-%m-%dT%H:%M:%S.%LZ', date: '$fechaHoraInicio' } }, " +
            "modalidad: 1, " +
            "libroTitulo: '$libro.titulo', " +
            "libroAutor:  '$libro.autor', " +
            "extensionesAdjuntas: '$archivosAdjuntos' } }"
    })
    List<ReunionResponseDTO> resumenTodas();

    // 2) Resumen por ID de reunión
    @Aggregation(pipeline = {
        "{ $match: { _id: ?0 } }",
        "{ $lookup: { from: 'Libros', localField: 'libroId', foreignField: '_id', as: 'libro' } }",
        "{ $unwind: { path: '$libro', preserveNullAndEmptyArrays: true } }",
        "{ $project: { _id: 0, " +
            "fechaHoraInicio: { $dateToString: { format: '%Y-%m-%dT%H:%M:%S.%LZ', date: '$fechaHoraInicio' } }, " +
            "modalidad: 1, " +
            "libroTitulo: '$libro.titulo', " +
            "libroAutor:  '$libro.autor', " +
            "extensionesAdjuntas: '$archivosAdjuntos' } }"
    })
    List<ReunionResponseDTO> resumenPorId(ObjectId reunionId);

    // 3) Resumen por libro (orden cronológico)
    @Aggregation(pipeline = {
        "{ $match: { libroId: ?0 } }",
        "{ $lookup: { from: 'Libros', localField: 'libroId', foreignField: '_id', as: 'libro' } }",
        "{ $unwind: { path: '$libro', preserveNullAndEmptyArrays: true } }",
        "{ $sort: { fechaHoraInicio: 1 } }",
        "{ $project: { _id: 0, " +
            "fechaHoraInicio: { $dateToString: { format: '%Y-%m-%dT%H:%M:%S.%LZ', date: '$fechaHoraInicio' } }, " +
            "modalidad: 1, " +
            "libroTitulo: '$libro.titulo', " +
            "libroAutor:  '$libro.autor', " +
            "extensionesAdjuntas: '$archivosAdjuntos' } }"
    })
    List<ReunionResponseDTO> resumenPorLibro(ObjectId libroId);

    // 4) Resumen por modalidad + rango de fechas
    @Aggregation(pipeline = {
        "{ $match: { modalidad: ?0, fechaHoraInicio: { $gte: ?1, $lte: ?2 } } }",
        "{ $lookup: { from: 'Libros', localField: 'libroId', foreignField: '_id', as: 'libro' } }",
        "{ $unwind: { path: '$libro', preserveNullAndEmptyArrays: true } }",
        "{ $sort: { fechaHoraInicio: 1 } }",
        "{ $project: { _id: 0, " +
            "fechaHoraInicio: { $dateToString: { format: '%Y-%m-%dT%H:%M:%S.%LZ', date: '$fechaHoraInicio' } }, " +
            "modalidad: 1, " +
            "libroTitulo: '$libro.titulo', " +
            "libroAutor:  '$libro.autor', " +
            "extensionesAdjuntas: '$archivosAdjuntos' } }"
    })
    List<ReunionResponseDTO> resumenPorModalidadYRango(String modalidad, Date desde, Date hasta);
}
