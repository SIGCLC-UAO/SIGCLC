package com.sigclc.backend.Reuniones.Services;

import java.util.Date;
import java.util.List;

import com.sigclc.backend.Reuniones.DTOs.GeneroCountDTO;
import com.sigclc.backend.Reuniones.DTOs.LibroCountDTO;
import com.sigclc.backend.Reuniones.DTOs.ReunionResponseDTO;
import com.sigclc.backend.Reuniones.DTOs.UsuarioActivoDTO;

public interface IReunionesListados {

    // Listados / resúmenes de reuniones
    List<ReunionResponseDTO> listarResumenTodas();

    List<ReunionResponseDTO> listarResumenPorId(String reunionIdHex);

    List<ReunionResponseDTO> listarResumenPorLibro(String libroIdHex);

    List<ReunionResponseDTO> listarResumenPorModalidadYRango(String modalidad, Date desde, Date hasta);

    List<ReunionResponseDTO> listarResumenProximas(Date referenciaTiempo);

    List<LibroCountDTO> obtenerTopLibros();

    List<GeneroCountDTO> obtenerTopGeneros();

    List<UsuarioActivoDTO> obtenerUsuariosMasActivos();


}
