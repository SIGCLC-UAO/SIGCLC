package com.sigclc.backend.Reuniones.Services;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Reuniones.DTOs.ReunionResponseDTO;
import com.sigclc.backend.Reuniones.Repository.IReunionesRepository;

@Service
public class ReunionesListadosImp implements IReunionesListados {

    @Autowired
    private IReunionesRepository reunionesRepository;

    @Override
    public List<ReunionResponseDTO> listarResumenTodas() {
        return reunionesRepository.resumenTodas();
    }

    @Override
    public List<ReunionResponseDTO> listarResumenPorId(String reunionIdHex) {
        Objects.requireNonNull(reunionIdHex, "reunionId no puede ser null");
        return reunionesRepository.resumenPorId(convertirAObjectId(reunionIdHex));
    }

    @Override
    public List<ReunionResponseDTO> listarResumenPorLibro(String libroIdHex) {
        Objects.requireNonNull(libroIdHex, "libroId no puede ser null");
        return reunionesRepository.resumenPorLibro(convertirAObjectId(libroIdHex));
    }

    @Override
    public List<ReunionResponseDTO> listarResumenPorModalidadYRango(String modalidad,
                                                                    Date fechaDesde,
                                                                    Date fechaHasta) {
        Objects.requireNonNull(modalidad, "modalidad no puede ser null");
        if (fechaDesde == null || fechaHasta == null || fechaDesde.after(fechaHasta)) {
            throw new IllegalArgumentException("Rango de fechas inválido");
        }
        return reunionesRepository.resumenPorModalidadYRango(modalidad.toLowerCase(), fechaDesde, fechaHasta);
    }

    @Override
    public List<ReunionResponseDTO> listarResumenProximas(Date referenciaTiempo) {
        Date fechaReferencia = (referenciaTiempo == null) ? new Date() : referenciaTiempo;
        return reunionesRepository.resumenProximas(fechaReferencia);
    }

    /* Util */
    private ObjectId convertirAObjectId(String idHex) {
        try {
            return new ObjectId(idHex);
        } catch (Exception ex) {
            throw new IllegalArgumentException("ObjectId inválido: " + idHex);
        }
    }
}
