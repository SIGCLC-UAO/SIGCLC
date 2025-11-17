package com.sigclc.backend.Reseñas.Mapper;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.sigclc.backend.Reseñas.DTOs.ReseniasCreateDTO;
import com.sigclc.backend.Reseñas.Models.ReseniasModel;

@Component
public class ReseniasMapper {

    public ReseniasModel toModel(ReseniasCreateDTO dto) {
        ReseniasModel model = new ReseniasModel();
        
        model.setLibroId(new ObjectId(dto.getLibroId()));
        model.setAutorId(new ObjectId(dto.getAutorId()));
        
        model.setCalificacion(dto.getCalificacion());
        model.setOpinion(dto.getOpinion());

        if (dto.getArchivosAdjuntos() != null && !dto.getArchivosAdjuntos().isEmpty()) {
            model.setArchivosAdjuntos(
                dto.getArchivosAdjuntos().stream()
                    .map(archivo -> {
                        String ext = archivo.toLowerCase().trim();
                        if (!ext.startsWith(".")) {
                            ext = "." + ext;
                        }
                        if (!ext.equals(".png") && !ext.equals(".docx") && !ext.equals(".pdf")) {
                            throw new IllegalArgumentException("Extensión no permitida: " + archivo);
                        }
                        return ext;
                    })
                    .toList()
            );
        }

        return model;
    }
}