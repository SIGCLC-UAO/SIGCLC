package com.sigclc.backend.Retos.DTOs;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RetosResponseDTO {
    
    private String id;
    private String titulo;
    private Date fechaInicio;
    private Date fechaFin;
    private String descripcion;
    private List<librosAsociadosDTO> librosAsociados;
    private List<ParticipantesDTO> participantes;


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

