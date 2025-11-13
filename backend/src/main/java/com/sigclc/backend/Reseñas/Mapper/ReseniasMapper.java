package com.sigclc.backend.Reseñas.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sigclc.backend.Reseñas.DTOs.ReseniasCreateDTO;
import com.sigclc.backend.Reseñas.Models.ReseniasModel;
import com.sigclc.backend.Reseñas.Models.TipoArchivo;

@Component
public class ReseniasMapper {

    public ReseniasModel toModel(ReseniasCreateDTO dto) {
        ReseniasModel model = new ReseniasModel();
        model.setLibroId(dto.getLibroId());
        model.setAutorId(dto.getAutorId());
        model.setCalificacion(dto.getCalificacion());
        model.setOpinion(dto.getOpinion());

        if (dto.getArchivosAdjuntos() != null) {
            List<String> archivos = dto.getArchivosAdjuntos().stream()
                .map(TipoArchivo::fromString)
                .map(TipoArchivo::getExtension)
                .collect(Collectors.toList());
            model.setArchivosAdjuntos(archivos);
        }

        return model;
    }
}
