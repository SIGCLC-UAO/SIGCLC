package com.sigclc.backend.Reuniones.Services;

import java.util.Date;
import java.util.List;

import com.sigclc.backend.Reuniones.DTOs.ReunionResponseDTO;

public interface IReunionesListados {

    // Listados / resúmenes de reuniones
    List<ReunionResponseDTO> listarResumenTodas();

    List<ReunionResponseDTO> listarResumenPorId(String reunionIdHex);

    List<ReunionResponseDTO> listarResumenPorLibro(String libroIdHex);

    List<ReunionResponseDTO> listarResumenPorModalidadYRango(String modalidad, Date desde, Date hasta);

    List<ReunionResponseDTO> listarResumenProximas(Date referenciaTiempo);
}
