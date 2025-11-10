package com.sigclc.backend.Usuarios;

import java.util.List;

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
        // _id lo genera MongoDB
        model.setNombre(dto.getNombre());
        model.setApellido(dto.getApellido());
        model.setEmail(dto.getEmail());
        model.setRol(dto.getRol());
        model.setTelefono(dto.getTelefono()); // Long
        model.setEdad(dto.getEdad());         // Long
        model.setOcupacion(dto.getOcupacion());
        // listas embebidas se inicializan en el constructor del model (o aquí si lo prefieres)
        return model;
    }

    /* =========================
     *   Model  ->  ResponseDTO
     * ========================= */
    public UsuarioResponseDTO toResponseDTO(UsuariosModel model) {
        if (model == null) return null;

        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(model.getIdAsString());
        dto.setNombre(model.getNombre());
        dto.setApellido(model.getApellido());
        dto.setEmail(model.getEmail());
        dto.setRol(model.getRol());
        dto.setTelefono(model.getTelefono());
        dto.setEdad(model.getEdad());
        dto.setOcupacion(model.getOcupacion());

        // Propuestas embebidas
        dto.setLibroPropuesto(
        model.getLibroPropuesto()
        .stream()
        .map(this::toLibroPropuestoDTO) 
        .toList()
        );
        return dto;
    }

        

    /* ==========================================
     *  List<Model>  ->  List<ResponseDTO>
     * ========================================== */
    public List<UsuarioResponseDTO> toResponseDTOList(List<UsuariosModel> models) {
        return models == null ? List.of()
                              : models.stream()
                              .map(this::toResponseDTO)
                              .toList();
    }

    /* =========================
     *      PATCH (UPDATE)
     * ========================= */
    public void patchModel(UsuariosModel model, UsuarioUpdateDTO dto) {
        if (model == null || dto == null) return;

        if (dto.getNombre()    != null) model.setNombre(dto.getNombre());
        if (dto.getApellido()  != null) model.setApellido(dto.getApellido());
        if (dto.getEmail()     != null) model.setEmail(dto.getEmail());
        if (dto.getRol()       != null) model.setRol(dto.getRol());
        if (dto.getTelefono()  != null) model.setTelefono(dto.getTelefono());
        if (dto.getEdad()      != null) model.setEdad(dto.getEdad());
        if (dto.getOcupacion() != null) model.setOcupacion(dto.getOcupacion());
        // Las propuestas y votaciones se gestionan por métodos específicos del Service.
    }

    /* =========================
     *     Helpers privados
     * ========================= */
    private UsuarioResponseDTO.LibroPropuestoDTO toLibroPropuestoDTO(LibroPropuesto lp) {
        if (lp == null) return null;

        UsuarioResponseDTO.LibroPropuestoDTO dto = new UsuarioResponseDTO.LibroPropuestoDTO();
        dto.setEstado(lp.getEstado());
        dto.setLibroId(lp.getLibroId() != null ? lp.getLibroId().toHexString() : null);

        if (lp.getVotaciones() != null) {
            dto.setVotaciones(
                lp.getVotaciones()
                  .stream()
                  .map(this::toVotacionDTO)
                  .toList()
            );
        }
        return dto;
    }

    private UsuarioResponseDTO.VotacionDTO toVotacionDTO(Votacion v) {
        if (v == null) return null;

        UsuarioResponseDTO.VotacionDTO dto = new UsuarioResponseDTO.VotacionDTO();
        dto.setVoto(v.getVoto());                // "Si" | "No" (String en el Model)
        dto.setFechaVoto(v.getFechaVoto());      // Date en el Model (si lo dejaste como Date)
        dto.setUsuarioId(v.getUsuarioId() != null ? v.getUsuarioId().toHexString() : null);
        return dto;
    }
}
