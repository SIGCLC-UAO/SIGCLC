package com.sigclc.backend.Reseñas.DTOs;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatsReseniasDTO {

    private String libroId;
    private double promedio;
    private long total;
    private Map<Integer, Long> distribucion;
}