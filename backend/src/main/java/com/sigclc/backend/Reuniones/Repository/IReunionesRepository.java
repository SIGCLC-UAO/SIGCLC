package com.sigclc.backend.Reuniones.Repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.sigclc.backend.Reuniones.DTOs.GeneroCountDTO;
import com.sigclc.backend.Reuniones.DTOs.LibroCountDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.UsuarioActivoDTO;
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

    // 5) Próximas (fecha >= now), orden cronológico
    @Aggregation(pipeline = {
        "{ $match: { fechaHoraInicio: { $gte: ?0 } } }",
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
    List<ReunionResponseDTO> resumenProximas(Date now);



    @Aggregation(pipeline = {
        "{ $match: { libroId: { $ne: null } } }", 
        "{ $group: { _id: '$libroId', count: { $sum: 1 } } }",   
        "{ $sort: { count: -1 } }",
        "{ $limit: 5 }",
        "{ $project: { _id: 0, libroId: '$_id', count: 1 } }" 
    })
    List<LibroCountDTO> obtenerTopLibros(); 

    @Aggregation(pipeline = {
        "{ $match: { libroId: { $ne: null } } }",
        "{ $lookup: { from: 'Libros', localField: 'libroId', foreignField: '_id', as: 'libroInfo' } }",
        "{ $unwind: { path: '$libroInfo', preserveNullAndEmptyArrays: false } }",
        "{ $match: { 'libroInfo.genero': { $ne: null } } }", 
        "{ $group: { _id: '$libroInfo.genero', count: { $sum: 1 } } }", 
        "{ $sort: { count: -1 } }",
        "{ $limit: 5 }",
        "{ $project: { _id: 0, genero: '$_id', count: 1 } }"
    })
    List<GeneroCountDTO> obtenerTopGeneros();

    @Aggregation(pipeline = {
        "{ $match: { asistentes: { $ne: null, $exists: true, $not: { $size: 0 } } } }",
        "{ $unwind: '$asistentes' }",
        "{ $group: { _id: '$asistentes', totalAsistencias: { $sum: 1 } } }", // Agrupa directo por string
        "{ $sort: { totalAsistencias: -1 } }",
        "{ $limit: 10 }",
        "{ $project: { _id: 0, usuarioId: '$_id', totalAsistencias: 1 } }" // Ya viene como string
    })
    List<UsuarioActivoDTO> obtenerUsuariosMasActivos();



}
