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
        
        return comentariosMapper.toResponseDTOList(comentariosRepository.buscarPorForoId(foroId));

    }

    @Override
    public List<ComentariosResponseDTO> buscarPorFecha(Date fecha) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ComentariosResponseDTO> buscarPorUsuario(String usuarioId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ComentariosResponseDTO> buscarPorComentarioId(String comentarioId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ComentariosResponseDTO actualizarComentario(String id, ComentariosUpdateDTO dto) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void eliminarComentario(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    
}
