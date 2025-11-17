package com.sigclc.backend.Reuniones.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Reuniones.DTOs.InscripcionResponseDTO;
import com.sigclc.backend.Reuniones.Models.ReunionesModel;
import com.sigclc.backend.Reuniones.Repository.IReunionesRepository;

@Service
public class ReunionesAsistentesImp implements IReunionesAsistentes {

    @Autowired
    private IReunionesRepository reunionesRepository;

    @Override
    public InscripcionResponseDTO inscribirAsistente(String reunionIdHex, String usuarioIdHex) {
        Objects.requireNonNull(reunionIdHex, "reunionId no puede ser null");
        Objects.requireNonNull(usuarioIdHex, "usuarioId no puede ser null");

        ObjectId reunionObjectId = convertirAObjectId(reunionIdHex);
        ObjectId usuarioObjectId = convertirAObjectId(usuarioIdHex);

        ReunionesModel reunion = obtenerReunionPorObjectId(reunionObjectId);

        List<ObjectId> asistentes =
                (reunion.getAsistentes() == null) ? new ArrayList<>() : reunion.getAsistentes();

        if (!asistentes.contains(usuarioObjectId)) {
            asistentes.add(usuarioObjectId);
            reunion.setAsistentes(asistentes);
            reunionesRepository.save(reunion);
        }

        int totalAsistentes =
                (reunion.getAsistentes() == null) ? 0 : reunion.getAsistentes().size();

        return new InscripcionResponseDTO("inscrito", totalAsistentes);
    }

    @Override
    public InscripcionResponseDTO retirarAsistente(String reunionIdHex, String usuarioIdHex) {
        Objects.requireNonNull(reunionIdHex, "reunionId no puede ser null");
        Objects.requireNonNull(usuarioIdHex, "usuarioId no puede ser null");

        ObjectId reunionObjectId = convertirAObjectId(reunionIdHex);
        ObjectId usuarioObjectId = convertirAObjectId(usuarioIdHex);

        ReunionesModel reunion = obtenerReunionPorObjectId(reunionObjectId);

        if (reunion.getAsistentes() != null) {
            reunion.getAsistentes().remove(usuarioObjectId);
            reunionesRepository.save(reunion);
        }

        int totalAsistentes =
                (reunion.getAsistentes() == null) ? 0 : reunion.getAsistentes().size();

        return new InscripcionResponseDTO("retirado", totalAsistentes);
    }

    /* Utils locales */

    private ReunionesModel obtenerReunionPorObjectId(ObjectId reunionObjectId) {
        // Aquí dejamos claro que NO aceptamos null y el analizador lo entiende
        return reunionesRepository
                .findById(Objects.requireNonNull(reunionObjectId, "reunionId no puede ser null"))
                .orElseThrow(() -> new IllegalArgumentException("Reunión no encontrada"));
    }

    private ObjectId convertirAObjectId(String idHex) {
        if (idHex == null || idHex.isBlank()) {
            throw new IllegalArgumentException("El id es obligatorio");
        }
        try {
            return new ObjectId(idHex.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException("ObjectId inválido: " + idHex);
        }
    }
}
