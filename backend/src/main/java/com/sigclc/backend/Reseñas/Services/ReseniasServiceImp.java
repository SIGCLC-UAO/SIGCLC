package com.sigclc.backend.Reseñas.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Libros.Services.ILibrosService;
import com.sigclc.backend.Reseñas.DTOs.ReseniasCreateDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniasResponseDTO;
import com.sigclc.backend.Reseñas.Mapper.ReseniasMapper;
import com.sigclc.backend.Reseñas.Models.ComentarioResenia;
import com.sigclc.backend.Reseñas.Models.ReseniasModel;
import com.sigclc.backend.Reseñas.Repository.IReseniasRepository;

@Service
public class ReseniasServiceImp implements IReseniasService {

    @Autowired
    private IReseniasRepository repository;

    @Autowired
    private ILibrosService librosService;

    @Autowired
    private ReseniasMapper mapper;

    @Override
    public ReseniasResponseDTO crearResenia(ReseniasCreateDTO dto) {
        librosService.buscarLibroPorId(dto.getLibroId());
        ReseniasModel model = mapper.toModel(dto);
        repository.save(model);
        return toDTO(model);
    }

    @Override
    public List<ReseniasResponseDTO> listarPorLibro(ObjectId libroId) {
        if (libroId == null) return listarTodas();
        return repository.findByLibroId(libroId).stream().map(this::toDTO).toList();
    }

    @Override
    public List<ReseniasResponseDTO> listarPorAutor(ObjectId autorId) {
        if (autorId == null) return listarTodas();
        return repository.findAll().stream()
                .filter(m -> m.getAutorId() != null && m.getAutorId().equals(autorId))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReseniasResponseDTO> listarTodas() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public void marcarUtil(ObjectId idResenia) {
        Optional<ReseniasModel> opt = repository.findById(idResenia);
        if (opt.isPresent()) {
            ReseniasModel model = opt.get();
            model.setMeGusta((model.getMeGusta() == null ? 0 : model.getMeGusta()) + 1);
            repository.save(model);
        } else {
            throw new RuntimeException("No se encontró la reseña con el ID proporcionado");
        }
    }

    @Override
    public void comentar(ObjectId idResenia, String utilidad, ObjectId usuarioId, String comentario) {
        Optional<ReseniasModel> opt = repository.findById(idResenia);
        if (opt.isPresent()) {
            ReseniasModel model = opt.get();

            if (model.getComentarios() == null) {
                model.setComentarios(new ArrayList<>());
            }

            model.getComentarios().add(new ComentarioResenia(utilidad, usuarioId, comentario));
            repository.save(model);
        } else {
            throw new RuntimeException("No se encontró la reseña con el ID proporcionado");
        }
    }

    private ReseniasResponseDTO toDTO(ReseniasModel model) {
        ReseniasResponseDTO dto = new ReseniasResponseDTO();
        dto.setId(model.getId());
        dto.setLibroId(model.getLibroId());
        dto.setAutorId(model.getAutorId());
        dto.setCalificacion(model.getCalificacion());
        dto.setOpinion(model.getOpinion());
        dto.setArchivosAdjuntos(model.getArchivosAdjuntos());
        dto.setComentarios(model.getComentarios());
        dto.setMeGusta(model.getMeGusta());
        dto.setFecha(model.getFecha());
        return dto;
    }
}

