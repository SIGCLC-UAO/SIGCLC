package com.sigclc.backend.Reuniones.DTOs;

import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class LibroCountDTO {
    private String libroId;
    private Long count;
}