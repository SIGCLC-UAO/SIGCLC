package com.sigclc.backend.Comentarios.Services;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Comentarios.DTOs.ComentariosCreateDTO;
import com.sigclc.backend.Comentarios.DTOs.ComentariosResponseDTO;
import com.sigclc.backend.Comentarios.DTOs.ComentariosUpdateDTO;
import com.sigclc.backend.Comentarios.Mapper.ComentariosMapper;
import com.sigclc.backend.Comentarios.Models.ComentariosModel;
import com.sigclc.backend.Comentarios.Repository.IComentariosRepository;
import com.sigclc.backend.Exception.RecursoNoEncontradoException;

@Service

public class ComentariosServiceImp implements  IComentariosService {
    @Autowired IComentariosRepository comentariosRepository;
    @Autowired ComentariosMapper comentariosMapper;

    @Override
    public ComentariosResponseDTO crearComentarios(ComentariosCreateDTO comentarios){
        if (comentarios == null){
            throw new IllegalArgumentException("El comentario no puede ser nulo");
        }
        ComentariosModel comentariosModel = comentariosMapper.toModel(comentarios);
        comentariosRepository.save(comentariosModel);
        return comentariosMapper.toResponseDTO(comentariosModel);
    }                                                      

    
    @Override
    public List<ComentariosResponseDTO> listarComentarios() {
        return comentariosMapper.toResponseDTOList(comentariosRepository.findAll());
    }

    @Override
    public ComentariosResponseDTO buscarPorId(String Id) {
        if(Id == null){
            throw new IllegalArgumentException("El id de los comentarios no puede ser nulo");
        }

        ObjectId objectId;
        try {
            objectId = new ObjectId(Id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException ("El formato del id no es válido");
        }

        ComentariosModel comentariosModel = comentariosRepository.findById(objectId)
        .orElseThrow(() -> new RecursoNoEncontradoException(
            "El comentario con id " +Id+ " no se ha encontrado o está mal escrito"
        ));
        return comentariosMapper.toResponseDTO(comentariosModel);
    }

    @Override
    public List<ComentariosResponseDTO> buscarPorForoId(String foroId) {
            List<ComentariosModel> comentarios = comentariosRepository.buscarPorForoId(foroId);
            return comentariosMapper.toResponseDTOList(comentarios);
    }

    @Override
    public List<ComentariosResponseDTO> buscarPorFecha(Date fecha) {
        List<ComentariosModel> comentarios = comentariosRepository.buscarPorFecha(fecha);
        return comentariosMapper.toResponseDTOList(comentarios);
    }

    @Override
    public List<ComentariosResponseDTO> buscarPorUsuario(String usuarioId) {
        List<ComentariosModel> comentarios = comentariosRepository.buscarPorUsuario(usuarioId);
        return comentariosMapper.toResponseDTOList(comentarios);
    }

    @Override
    public List<ComentariosResponseDTO> buscarPorComentarioId(String comentarioId) {
        List<ComentariosModel> comentarios = comentariosRepository.buscarPorComentarioId(comentarioId);
        return comentariosMapper.toResponseDTOList(comentarios);
    }

    @Override
    public ComentariosResponseDTO actualizarComentario(String id, ComentariosUpdateDTO dto) {
        if( id == null){
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El formato del id no es válido"); 
        }

        ComentariosModel comentario = comentariosRepository.findById(objectId)
        .orElseThrow(()-> new RecursoNoEncontradoException(
            "El libro con id "+id+" no se ha encontrado o está mal escrito"
        ));

        comentariosMapper.UpdateDTO(dto, comentario);

        ComentariosModel actualizado = comentariosRepository.save(comentario);

        return  comentariosMapper.toResponseDTO(actualizado); 
    }

    @Override
    public void eliminarComentario(String id) {
        if (id == null){
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("El formato del id no es valido");
        }

        comentariosRepository.deleteById(objectId);

    }        
}
