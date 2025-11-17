package com.sigclc.backend.Usuarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import com.sigclc.backend.Usuarios.DTOs.UsuarioCreateDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioResponseDTO;
import com.sigclc.backend.Usuarios.DTOs.UsuarioUpdateDTO;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;
import com.sigclc.backend.Usuarios.Models.UsuariosModel.LibroPropuesto;
import com.sigclc.backend.Usuarios.Models.UsuariosModel.Votacion;

@Component
public class Mapper {

    /* =========================
     *  CreateDTO  ->  Model
     * ========================= */
    public UsuariosModel toModel(UsuarioCreateDTO dto) {
        if (dto == null) return null;

        UsuariosModel model = new UsuariosModel();
        model.setNombre(dto.getNombre());
        model.setApellido(dto.getApellido());
        model.setEmail(dto.getEmail());
        model.setRol(dto.getRol());
        model.setTelefono(dto.getTelefono());
        model.setEdad(dto.getEdad());
        model.setOcupacion(dto.getOcupacion());
        model.setLibroPropuesto(new ArrayList<>());
        return model;
    }

    /* =========================
     *   Model  ->  ResponseDTO
     * ========================= */
    public UsuarioResponseDTO toResponseDTO(UsuariosModel model) {
        if (model == null) return null;

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(model.getId() != null ? model.getId().toHexString() : null);
        dto.setNombre(model.getNombre());
        dto.setApellido(model.getApellido());
        dto.setEmail(model.getEmail());
        dto.setRol(model.getRol());
        dto.setTelefono(model.getTelefono());
        dto.setEdad(model.getEdad());
        dto.setOcupacion(model.getOcupacion());

        List<LibroPropuesto> propuestas = model.getLibroPropuesto();
        if (propuestas != null && !propuestas.isEmpty()) {
            dto.setLibroPropuesto(
                propuestas.stream()
                         .filter(Objects::nonNull)
                         .map(this::toLibroPropuestoDTO)
                         .collect(Collectors.toList())
            );
        } else {
            dto.setLibroPropuesto(List.of());
        }

        return dto;
    }

    public List<UsuarioResponseDTO> toResponseDTOList(List<UsuariosModel> models) {
        if (models == null || models.isEmpty()) return List.of();
        return models.stream()
                     .filter(Objects::nonNull)
                     .map(this::toResponseDTO)
                     .collect(Collectors.toList());
    }

    /* =========================
     *      PATCH (UPDATE)
     * ========================= */
    public void patchModel(UsuariosModel model, UsuarioUpdateDTO dto) {
        if (model == null || dto == null) return;

        if (dto.getNombre()    != null) model.setNombre(dto.getNombre());
        if (dto.getApellido()  != null) model.setApellido(dto.getApellido());
        if (dto.getTelefono()  != null) model.setTelefono((Long) dto.getTelefono());
        if (dto.getEdad()      != null) model.setEdad(dto.getEdad());
        if (dto.getOcupacion() != null) model.setOcupacion(dto.getOcupacion());
        // Email/rol fuera de PATCH por política actual.
    }

    /* =========================
     *     Helpers privados
     * ========================= */
    private UsuarioResponseDTO.LibroPropuestoDTO toLibroPropuestoDTO(LibroPropuesto lp) {
        if (lp == null) return null;

        UsuarioResponseDTO.LibroPropuestoDTO dto = new UsuarioResponseDTO.LibroPropuestoDTO();
        dto.setEstado(lp.getEstado());
        dto.setLibroId(lp.getLibroId() != null ? lp.getLibroId().toHexString() : null);

        List<Votacion> votos = lp.getVotaciones();
        if (votos != null && !votos.isEmpty()) {
            dto.setVotaciones(
                votos.stream()
                     .filter(Objects::nonNull)
                     .map(this::toVotacionDTO)
                     .collect(Collectors.toList())
            );
        } else {
            dto.setVotaciones(List.of());
        }
        return dto;
    }

    private UsuarioResponseDTO.VotacionDTO toVotacionDTO(Votacion v) {
        if (v == null) return null;

        UsuarioResponseDTO.VotacionDTO dto = new UsuarioResponseDTO.VotacionDTO();
        dto.setVoto(v.getVoto());
        dto.setFechaVoto(v.getFechaVoto());
        dto.setUsuarioId(v.getUsuarioId() != null ? v.getUsuarioId().toHexString() : null);
        return dto;
    }

    /** Utilidad opcional: convierte String a ObjectId o retorna null (útil en Services). */
    public ObjectId toObjectIdOrNull(String hex) {
        try {
            return (hex == null || hex.isBlank()) ? null : new ObjectId(hex);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
