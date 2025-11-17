package com.sigclc.backend.Retos.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.sigclc.backend.Retos.DTOs.RetosCreateDTO;
import com.sigclc.backend.Retos.DTOs.RetosResponseDTO;
import com.sigclc.backend.Retos.DTOs.RetosUpdateDTO;
import com.sigclc.backend.Retos.Models.RetosModel;

@Component

public class RetosMapper{

    public RetosModel toModel(RetosCreateDTO dto){
        if(dto == null) return null;

        RetosModel model = new RetosModel();
        model.setTitulo(dto.getTitulo());
        model.setFechaInicio(dto.getFechaInicio());
        model.setFechaFin(dto.getFechaFin());
        model.setDescripcion(dto.getDescripcion());

        List<RetosModel.librosAsociados> librosAsociados = dto.getLibrosAsociados().stream()
            .map(l -> new RetosModel.librosAsociados(((l.getLibroId()))))
            .collect(Collectors.toList());
        model.setLibroAsociados(librosAsociados);

        List<RetosModel.Participantes> participantes = dto.getParticipantes().stream()
            .map(pdto -> {
                List<RetosModel.Progreso> Progreso = pdto.getProgreso().stream()
                    .map(pro -> new RetosModel.Progreso(pro.getLibroId(), pro.getProgreso()))
                    .collect(Collectors.toList());
                return new RetosModel.Participantes(pdto.getUsuarioId(),Progreso);
            })
            .collect(Collectors.toList());
            model.setParticipantes(participantes);
            return model;
    }

    public RetosResponseDTO toResponseDTO(RetosModel model){
        if(model == null) return null; 

        RetosResponseDTO dto = new RetosResponseDTO();
        dto.setId(model.getId() != null ? model.getId().toHexString() : null);
        dto.setTitulo(model.getTitulo());
        dto.setFechaInicio(model.getFechaInicio());
        dto.setFechaFin(model.getFechaFin());
        dto.setDescripcion(model.getDescripcion());

        List<RetosResponseDTO.librosAsociadosDTO> librosAsociadosDTO = model.getLibroAsociados().stream()
            .map(libroAsociado -> new RetosResponseDTO.librosAsociadosDTO(libroAsociado.getLibroId()))
            .collect(Collectors.toList());
        dto.setLibrosAsociados(librosAsociadosDTO);


        List<RetosResponseDTO.ParticipantesDTO> participantesDTO = model.getParticipantes().stream()
            .map(participante -> {
                List<RetosResponseDTO.ProgresoDTO> progresoDTO = participante.getProgreso().stream()
                    .map(p -> new RetosResponseDTO.ProgresoDTO(p.getLibroId(), p.getProgreso()))
                    .collect(Collectors.toList());
                return new RetosResponseDTO.ParticipantesDTO(participante.getUsuarioId(), progresoDTO);
            })
            .collect(Collectors.toList());
        dto.setParticipantes(participantesDTO);

        return dto;
    }

    public void UpdateDTO(RetosUpdateDTO dto, RetosModel model){
        if (dto == null || model == null) return;

        if (dto.getTitulo() != null) model.setTitulo(dto.getTitulo());
        if (dto.getFechaInicio() != null) model.setFechaInicio(dto.getFechaInicio());
        if (dto.getFechaFin() != null) model.setFechaFin(dto.getFechaFin());
        if (dto.getDescripcion() != null) model.setDescripcion(dto.getDescripcion());

        if(dto.getLibrosAsociados() != null){
            List<RetosModel.librosAsociados>librosAsociados = dto.getLibrosAsociados().stream()
                .map(laDTO -> new RetosModel.librosAsociados((laDTO.getLibroId())))
                .collect(Collectors.toList());
            model.setLibroAsociados(librosAsociados);
        }

        if(dto.getParticipantes() != null){
            List<RetosModel.Participantes> participantes = dto.getParticipantes().stream()
                .map(pDTO -> {
                    try {
                        ObjectId usuarioId = (pDTO.getUsuarioId());

                        List<RetosModel.Progreso> progreso = pDTO.getProgreso().stream()
                            .map(proDTO -> new RetosModel.Progreso(proDTO.getLibroId(), proDTO.getProgreso()))
                            .collect(Collectors.toList());
                        return new RetosModel.Participantes(usuarioId, progreso);
                    } catch (IllegalArgumentException e) {
                        throw  new RuntimeException("Error al convertir el usuarioId a ObjectId" +pDTO.getUsuarioId());
                    }
                    
                })
                .collect(Collectors.toList());
            model.setParticipantes(participantes);

        }
    
    }

    public List<RetosResponseDTO> toResponseDTOList(List<RetosModel> retosModelList){
        if(retosModelList == null){
            return null;
        }
        return retosModelList.stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    
    }

}



