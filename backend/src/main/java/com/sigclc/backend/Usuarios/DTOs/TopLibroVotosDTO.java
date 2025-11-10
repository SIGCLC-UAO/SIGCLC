package com.sigclc.backend.Usuarios.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopLibroVotosDTO {
  private String  libroId;     
  private String  titulo;
  private String  autor;
  private Integer votosSi;
  private Integer votosNo;
  private Integer totalVotos;
}
