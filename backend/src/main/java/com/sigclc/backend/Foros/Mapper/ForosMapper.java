package com.sigclc.backend.Foros.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sigclc.backend.Foros.DTOs.ForosCreateDTO;
import com.sigclc.backend.Foros.DTOs.ForosResponseDTO;
import com.sigclc.backend.Foros.DTOs.ForosUpdateDTO;
import com.sigclc.backend.Foros.Models.ForosModel;

@Component

public class ForosMapper {

    public ForosModel toModel(ForosCreateDTO dto){
        if(dto == null) return null;

        ForosModel model = new ForosModel();
        
        model.setTipo(dto.getTipo());
        model.setCreadorId(dto.getCreadorId());
        
        return model;
    }

    public ForosResponseDTO toResponseDTO(ForosModel model){
        if(model == null) return null;

        ForosResponseDTO dto = new ForosResponseDTO();

        dto.setId(model.getIdAsString());
        dto.setCreadorId(model.getCreadorId());
        dto.setTipo(model.getTipo());


        return  dto;

    }

    public void UpdateDTO(ForosUpdateDTO dto, ForosModel model){
        if(dto == null || model == null) return;
        
        if(dto.getTipo() != null ) model.setTipo(dto.getTipo());
        if(dto.getCreadorId() != null ) model.setCreadorId(dto.getCreadorId());
    }

    public List<ForosResponseDTO> toResponseDTOList(List<ForosModel> foros) {
        if(foros == null) return null;

        return foros.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
