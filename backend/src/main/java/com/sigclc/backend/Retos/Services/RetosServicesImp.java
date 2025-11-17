package com.sigclc.backend.Retos.Services;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Exception.RecursoNoEncontradoException;
import com.sigclc.backend.Retos.DTOs.RetosCreateDTO;
import com.sigclc.backend.Retos.DTOs.RetosResponseDTO;
import com.sigclc.backend.Retos.DTOs.RetosUpdateDTO;
import com.sigclc.backend.Retos.Mapper.RetosMapper;
import com.sigclc.backend.Retos.Models.RetosModel;
import com.sigclc.backend.Retos.Repository.IRetosRepository;

@Service



public class RetosServicesImp implements IRetosService{

    @Autowired private IRetosRepository retosRepository;
    @Autowired private RetosMapper retosMapper;


    @Override
    public RetosResponseDTO crearReto(RetosCreateDTO dto) {
        
        RetosModel model = retosMapper.toModel(dto);
        retosRepository.save(model);
        
        return retosMapper.toResponseDTO(model);

    }

    @Override
    public RetosResponseDTO obtenerRetoPorId(String idReto) {
        if(idReto == null){
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        ObjectId objectId;
        try {
            objectId = new ObjectId(idReto);
        } catch (IllegalArgumentException e) {
            throw  new IllegalArgumentException("El formato del id no es válido");
        }

        RetosModel retosModel = retosRepository.findById(objectId)
        .orElseThrow(() -> new RecursoNoEncontradoException("El reto con id "+idReto+ " no se ha encontrado o está mal escrito" 
        ));

        return retosMapper.toResponseDTO(retosModel);
    }

    @Override
    public List<RetosResponseDTO> listarRetos() {
        return retosMapper.toResponseDTOList(retosRepository.findAll());
    }

    @Override
    public RetosResponseDTO actualizarReto(String idReto, RetosUpdateDTO dto) {
        if(idReto == null){
            throw new IllegalArgumentException("El id no puede ser nulo");
        }


        ObjectId objectId;
        try {
            objectId = new ObjectId(idReto);
        } catch (IllegalArgumentException e) {
            throw  new IllegalArgumentException("El formato del id no es válido");
        }

        RetosModel retoExistente = retosRepository.findById(objectId)
        .orElseThrow(()-> new RecursoNoEncontradoException(
            "El reto con id "+idReto+ " no se ha encontrado o está mal escrito"
        ));
        
        retosMapper.UpdateDTO(dto, retoExistente);

        RetosModel actualizado = retosRepository.save(retoExistente);
        return retosMapper.toResponseDTO(actualizado);

    }

    @Override
    public List<RetosResponseDTO> buscarPorTitulo(String titulo) {
           List<RetosModel> retos = retosRepository.buscarPorTitulo(titulo);
           return retosMapper.toResponseDTOList(retos);
    }

    @Override
    public List<RetosResponseDTO> buscarPorDescripcion(String descripcion) {
           List<RetosModel> retos = retosRepository.buscarPorDescripcion(descripcion);
           return retosMapper.toResponseDTOList(retos);
    }

    @Override
    public List<RetosResponseDTO> buscarRetosPorLibro(String libroId) {

        if(!ObjectId.isValid(libroId)){
            throw new IllegalArgumentException("El id de libro es invalidp");
        }
        ObjectId objectId = new ObjectId(libroId);
        List<RetosModel> retos = retosRepository.buscarRetosPorLibro(objectId);
        return retosMapper.toResponseDTOList(retos);
    }

    public List<String> listarLibrosEnRetos(String idReto) {
        if(!ObjectId.isValid(idReto)){
            throw new IllegalArgumentException("El id de reto es inválido");
        }
        ObjectId objectId = new ObjectId(idReto);
        return retosRepository.listarLibrosEnRetos(objectId);
    }

    @Override
    public List<String> listarRetosPorUsuario(String usuarioId) {
        if(!ObjectId.isValid(usuarioId)){
        throw new IllegalArgumentException("El id de usuario es inválido");
        }
        ObjectId objectId = new ObjectId(usuarioId);
        return retosRepository.listarRetosPorUsuario(objectId);
    }

    @Override
    public Integer contarParticipantes(String idReto) {
        return retosRepository.contarParticipantes(new ObjectId(idReto));
    }

    @Override
    public List<String> listarUsuariosInscritos(String idReto) {
        return retosRepository.listarUsuariosInscritos(new ObjectId(idReto));
    }



    @Override
    public void eliminarRetoPorId(String idReto) {
        if(idReto == null){
            throw new IllegalArgumentException("El id no puede ser nulo");            
        }

        ObjectId objectId;
        try {
            objectId = new ObjectId(idReto);
        } catch (Exception e) {
            throw new IllegalArgumentException("El formato del id no es valido");
        }

        retosRepository.deleteById(objectId);
    }

    @Override
    public List<RetosResponseDTO> buscarRetosPorFechaInicio(Date fechaInicio) {
        if (fechaInicio == null) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser nula");
        }
        List<RetosModel> retos = retosRepository.buscarRetosPorFechaInicio(fechaInicio);
        return retosMapper.toResponseDTOList(retos);
    }

    @Override
    public List<RetosResponseDTO> buscarRetosPorFechaFin(Date fechaFin) {
        if (fechaFin == null) {
            throw new IllegalArgumentException("La fecha de fin no puede ser nula");
        }
        List<RetosModel> retos = retosRepository.buscarRetosPorFechaFin(fechaFin);
        return retosMapper.toResponseDTOList(retos);
    }

    




 
}
