package com.sigclc.backend.Comentarios.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.sigclc.backend.Comentarios.DTOs.ComentariosCreateDTO;
import com.sigclc.backend.Comentarios.DTOs.ComentariosResponseDTO;
import com.sigclc.backend.Comentarios.DTOs.ComentariosUpdateDTO;
import com.sigclc.backend.Comentarios.Models.ComentariosModel;

@Component

public class ComentariosMapper {

    public ComentariosModel toModel(ComentariosCreateDTO dto){
        if(dto == null) return  null;

        ComentariosModel model = new ComentariosModel();

        model.setForoId(dto.getForoId());
        model.setMensaje((dto.getMensaje()));
        model.setFechaComentario(dto.getFechaComentario());
        model.setUsuarioId(new ObjectId(dto.getUsuarioId()));
        model.setComentarioId((dto.getComentarioId() != null && !dto.getComentarioId().isBlank()
                ? new ObjectId(dto.getComentarioId()) 
                : null 
        ));

        return  model;
    }


    public ComentariosResponseDTO toResponseDTO(ComentariosModel model){

        ComentariosResponseDTO dto = new ComentariosResponseDTO();

        dto.setId(model.getIdAsString());
        dto.setForoId(model.getForoId());
        dto.setMensaje((model.getMensaje()));
        dto.setFechaComentario(model.getFechaComentario());
        dto.setUsuarioId(model.getUsuarioId() != null ? model.getUsuarioId().toHexString() : null);
        dto.setComentarioId(model.getComentarioId() != null ? model.getComentarioId().toHexString() : null);


        return dto;

        
    }

    public void UpdateDTO(ComentariosUpdateDTO dto, ComentariosModel model) {
        if (dto == null || model == null) return;  // Si el DTO o el modelo son nulos, no hacemos nada

        // Actualizamos solo los campos que no sean nulos
        if (dto.getForoId() != null) model.setForoId(dto.getForoId());
        if (dto.getMensaje() != null) model.setMensaje(dto.getMensaje());
        if (dto.getFechaComentario() != null) model.setFechaComentario(dto.getFechaComentario());
        if (dto.getUsuarioId() != null) model.setUsuarioId(new ObjectId(dto.getUsuarioId()));

        if (dto.getComentarioId() != null) {
            model.setComentarioId(new ObjectId(dto.getComentarioId()));
        }
    }

    public List<ComentariosResponseDTO> toResponseDTOList(List<ComentariosModel> comentarios) {
        if(comentarios == null) return null;
    
        return comentarios.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
    

