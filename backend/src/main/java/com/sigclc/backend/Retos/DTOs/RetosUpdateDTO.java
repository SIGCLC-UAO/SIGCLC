package com.sigclc.backend.Retos.DTOs;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RetosUpdateDTO {
    
    @NotBlank(message="El titulo es obligatorios")
    private String titulo;
    @NotNull(message="La fecha de inicio es obligatorios")
    private Date fechaInicio;
    @NotNull(message="La fecha de fin es obligatorios")
    private Date fechaFin;
    @NotBlank(message="La descripcion es obligatorios")
    private String descripcion;
    @NotNull(message="Los libros Asociados son obligatorios")
    private List<RetosCreateDTO.librosAsociadosDTO>librosAsociados;
    @NotNull(message="Los participantes son obligatorios")
    private List<RetosCreateDTO.ParticipantesDTO>participantes;   
    
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public  static class librosAsociadosDTO{
        private ObjectId libroId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor    
    public static class ParticipantesDTO{
        private ObjectId usuarioId;
        private List<ProgresoDTO>progreso;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProgresoDTO{
        private ObjectId libroId;
        private String progreso;
    }
}
