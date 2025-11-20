package com.sigclc.backend.Foros.Services;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Exception.RecursoNoEncontradoException;
import com.sigclc.backend.Foros.DTOs.ForosCreateDTO;
import com.sigclc.backend.Foros.DTOs.ForosResponseDTO;
import com.sigclc.backend.Foros.DTOs.ForosUpdateDTO;
import com.sigclc.backend.Foros.Mapper.ForosMapper;
import com.sigclc.backend.Foros.Models.ForosModel;
import com.sigclc.backend.Foros.Repository.IForosRepository;

@Service

public class ForosServiceImp implements IForosService{

    @Autowired private IForosRepository forosRepository;
    @Autowired private ForosMapper forosMapper;

    @Override
    public ForosResponseDTO crearForo(ForosCreateDTO dto) {
        ForosModel model = forosMapper.toModel(dto);
        forosRepository.save(model);

        return forosMapper.toResponseDTO(model);
    }

    @Override
    public List<ForosResponseDTO> listarForos() {
        return forosMapper.toResponseDTOList(forosRepository.findAll());
    }

    @Override
    public ForosResponseDTO buscarForoPorId(String Id) {
        if (Id == null){
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        ObjectId objectId;
        try {
            objectId = new ObjectId(Id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El formato del id no es válido"); 
        }

        ForosModel model = forosRepository.findById(objectId)
        .orElseThrow(()-> new RecursoNoEncontradoException(
            "El foro con id "+Id+" no se ha encontrado o está mal escrito"
        ));
        return  forosMapper.toResponseDTO(model);     
    }

    @Override
    public List<ForosResponseDTO> buscarPorTipo(String tipo) {
       List<ForosModel>  foros = forosRepository.buscarPorTipo(tipo);
        return forosMapper.toResponseDTOList(foros);    }

    @Override
    public List<ForosResponseDTO> buscarPorCreadorId(String creadorId) {
            List<ForosModel> foros = forosRepository.buscarPorCreadorId(creadorId);
            return forosMapper.toResponseDTOList(foros);    }

    @Override
    public ForosResponseDTO actualizarForo(String id, ForosUpdateDTO dto) {
        if( id == null){
            throw new IllegalArgumentException("El id no puede ser nulo");
        }
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El formato del id no es válido"); 
        }

        ForosModel foros = forosRepository.findById(objectId)
        .orElseThrow(()-> new RecursoNoEncontradoException(
            "El foro con id "+id+" no se ha encontrado o está mal escrito"
        ));

        forosMapper.UpdateDTO(dto, foros);

        ForosModel actualizado = forosRepository.save(foros);

        return  forosMapper.toResponseDTO(actualizado); 
    }

    @Override
    public void eliminarForo(String id) {
        if (id == null){
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("El formato del id no es valido");
        }

        forosRepository.deleteById(objectId);
    }
    
}
