package com.sigclc.backend.Reseñas.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigclc.backend.Libros.DTOs.LibrosResponseDTO;
import com.sigclc.backend.Libros.Services.ILibrosService;
import com.sigclc.backend.Reseñas.DTOs.ComentarioCreateDTO;
import com.sigclc.backend.Reseñas.DTOs.FiltroReseniasDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniaDetailDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniaListViewDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniaUpdateDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniasCreateDTO;
import com.sigclc.backend.Reseñas.DTOs.ReseniasResponseDTO;
import com.sigclc.backend.Reseñas.DTOs.StatsReseniasDTO;
import com.sigclc.backend.Reseñas.DTOs.TopReviewerDTO;
import com.sigclc.backend.Reseñas.Mapper.ReseniasMapper;
import com.sigclc.backend.Reseñas.Models.ComentarioResenia;
import com.sigclc.backend.Reseñas.Models.ReseniasModel;
import com.sigclc.backend.Reseñas.Repository.IReseniasRepository;
import com.sigclc.backend.Usuarios.Models.UsuariosModel;
import com.sigclc.backend.Usuarios.Services.IUsuariosService;

@Service
public class ReseniasServiceImp implements IReseniasService {

    @Autowired
    private IReseniasRepository repository;

    @Autowired
    private ILibrosService librosService;

    @Autowired
    private IUsuariosService usuariosService;

    @Autowired
    private ReseniasMapper mapper;

    @Override
    public ReseniasResponseDTO crearResenia(ReseniasCreateDTO dto) {
        
        librosService.buscarLibroPorId(dto.getLibroId());
        
        ObjectId autorObjectId = new ObjectId(dto.getAutorId());
        usuariosService.buscarUsuarioPorId(autorObjectId);
        
        ObjectId libroObjectId = new ObjectId(dto.getLibroId());
        Optional<ReseniasModel> existente = repository.findByAutorIdAndLibroId(autorObjectId, libroObjectId);
        
        if (existente.isPresent()) {
            throw new RuntimeException("Ya existe una reseña de este usuario para este libro (409)");
        }
        
        if (dto.getArchivosAdjuntos() != null) {
            for (String archivo : dto.getArchivosAdjuntos()) {
                if (!archivo.matches("\\.(png|docx|pdf)$")) {
                    throw new IllegalArgumentException("Archivo no permitido: " + archivo);
                }
            }
        }
        
        ReseniasModel model = mapper.toModel(dto);
        repository.save(model);
        
        return toDTO(model);
    }

    @Override
    public ReseniasResponseDTO actualizarResenia(String id, ReseniaUpdateDTO dto, String usuarioAutenticadoId) {
        
        ObjectId objectId = new ObjectId(id);
        ReseniasModel model = repository.findById(objectId)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada (404)"));
        
        if (!model.getAutorId().toHexString().equals(usuarioAutenticadoId)) {
            throw new RuntimeException("No tienes permiso para actualizar esta reseña (403)");
        }
        
        if (dto.getCalificacion() != null) {
            model.setCalificacion(dto.getCalificacion());
        }
        if (dto.getOpinion() != null) {
            model.setOpinion(dto.getOpinion());
        }
        if (dto.getArchivosAdjuntos() != null) {
            model.setArchivosAdjuntos(dto.getArchivosAdjuntos());
        }
        
        repository.save(model);
        return toDTO(model);
    }

    @Override
    public void eliminarResenia(String id, String usuarioAutenticadoId) {
        
        ObjectId objectId = new ObjectId(id);
        ReseniasModel model = repository.findById(objectId)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada (404)"));
        
        if (!model.getAutorId().toHexString().equals(usuarioAutenticadoId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta reseña (403)");
        }
        
        repository.deleteById(objectId);
    }

    @Override
    public List<ReseniaListViewDTO> listarConFiltros(FiltroReseniasDTO filtros) {
        
        List<ReseniasModel> resenias = repository.findAll();
        
        if (filtros.getLibroId() != null) {
            ObjectId libroId = new ObjectId(filtros.getLibroId());
            resenias = resenias.stream()
                    .filter(r -> r.getLibroId().equals(libroId))
                    .collect(Collectors.toList());
        }
        
        if (filtros.getAutorId() != null) {
            ObjectId autorId = new ObjectId(filtros.getAutorId());
            resenias = resenias.stream()
                    .filter(r -> r.getAutorId().equals(autorId))
                    .collect(Collectors.toList());
        }
        
        if (filtros.getCalificacionMin() != null) {
            resenias = resenias.stream()
                    .filter(r -> r.getCalificacion() >= filtros.getCalificacionMin())
                    .collect(Collectors.toList());
        }
        
        if (filtros.getCalificacionMax() != null) {
            resenias = resenias.stream()
                    .filter(r -> r.getCalificacion() <= filtros.getCalificacionMax())
                    .collect(Collectors.toList());
        }
        
        if (filtros.getTieneAdjuntos() != null) {
            resenias = resenias.stream()
                    .filter(r -> (r.getArchivosAdjuntos() != null && !r.getArchivosAdjuntos().isEmpty()) == filtros.getTieneAdjuntos())
                    .collect(Collectors.toList());
        }
        
        if (filtros.getTexto() != null && !filtros.getTexto().isEmpty()) {
            resenias = resenias.stream()
                    .filter(r -> r.getOpinion().toLowerCase().contains(filtros.getTexto().toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (filtros.getComentadoPor() != null) {
            ObjectId usuarioId = new ObjectId(filtros.getComentadoPor());
            resenias = resenias.stream()
                    .filter(r -> r.getComentarios() != null && r.getComentarios().stream()
                            .anyMatch(c -> c.getUsuarioId().equals(usuarioId)))
                    .collect(Collectors.toList());
        }
        
        return resenias.stream()
                .map(this::toListViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReseniaDetailDTO obtenerDetalle(String id, String usuarioAutenticadoId) {
        
        ObjectId objectId = new ObjectId(id);
        ReseniasModel model = repository.findById(objectId)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada (404)"));
        
        return toDetailDTO(model, usuarioAutenticadoId);
    }

    @Override
    public List<ReseniaListViewDTO> misResenias(String usuarioId) {
        
        ObjectId autorId = new ObjectId(usuarioId);
        List<ReseniasModel> resenias = repository.findByAutorId(autorId);
        
        return resenias.stream()
                .map(this::toListViewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void agregarComentario(String idResenia, ComentarioCreateDTO dto) {
        
        ObjectId reseniaId = new ObjectId(idResenia);
        ReseniasModel model = repository.findById(reseniaId)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada (404)"));
        
        ObjectId usuarioId = new ObjectId(dto.getUsuarioId());
        usuariosService.buscarUsuarioPorId(usuarioId);
        
        if (model.getComentarios() == null) {
            model.setComentarios(new ArrayList<>());
        }
        
        model.getComentarios().add(new ComentarioResenia(dto.getUtilidad(), usuarioId, dto.getComentario()));
        repository.save(model);
    }

    @Override
    public void eliminarComentario(String idResenia, String usuarioId, String textoComentario) {
        
        ObjectId reseniaId = new ObjectId(idResenia);
        ObjectId usuarioObjectId = new ObjectId(usuarioId);
        
        ReseniasModel model = repository.findById(reseniaId)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada (404)"));
        
        if (model.getComentarios() == null) {
            throw new RuntimeException("No hay comentarios en esta reseña");
        }
        
        boolean eliminado = model.getComentarios().removeIf(c -> 
            c.getUsuarioId().equals(usuarioObjectId) && c.getComentario().equals(textoComentario)
        );
        
        if (!eliminado) {
            throw new RuntimeException("Comentario no encontrado o no tienes permiso (403)");
        }
        
        repository.save(model);
    }

    @Override
    public StatsReseniasDTO obtenerEstadisticasPorLibro(String libroId) {
        
        ObjectId objectId = new ObjectId(libroId);
        List<ReseniasModel> resenias = repository.findByLibroId(objectId);
        
        if (resenias.isEmpty()) {
            StatsReseniasDTO stats = new StatsReseniasDTO();
            stats.setLibroId(libroId);
            stats.setTotal(0);
            stats.setPromedio(0.0);
            stats.setDistribucion(new HashMap<>());
            return stats;
        }
        
        double promedio = resenias.stream()
                .mapToInt(ReseniasModel::getCalificacion)
                .average()
                .orElse(0.0);
        
        Map<Integer, Long> distribucion = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int estrella = i;
            long count = resenias.stream()
                    .filter(r -> r.getCalificacion() == estrella)
                    .count();
            distribucion.put(i, count);
        }
        
        StatsReseniasDTO stats = new StatsReseniasDTO();
        stats.setLibroId(libroId);
        stats.setTotal(resenias.size());
        stats.setPromedio(Math.round(promedio * 100.0) / 100.0);
        stats.setDistribucion(distribucion);
        
        return stats;
    }

    @Override
    public List<TopReviewerDTO> obtenerTopReviewers(int limite) {
        
        List<ReseniasModel> todasResenias = repository.findAll();
        
        Map<ObjectId, Long> conteo = todasResenias.stream()
                .collect(Collectors.groupingBy(ReseniasModel::getAutorId, Collectors.counting()));
        
        return conteo.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(limite)
                .map(entry -> {
                    UsuariosModel usuario = usuariosService.buscarUsuarioPorId(entry.getKey());
                    return new TopReviewerDTO(
                        entry.getKey().toHexString(),
                        usuario.getNombre(),
                        entry.getValue()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ReseniasResponseDTO> listarPorLibro(String libroId) {
        ObjectId objectId = new ObjectId(libroId);
        return repository.findByLibroId(objectId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public List<ReseniasResponseDTO> listarPorAutor(String autorId) {
        ObjectId objectId = new ObjectId(autorId);
        return repository.findByAutorId(objectId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public List<ReseniasResponseDTO> listarTodas() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public void marcarUtil(String idResenia) {
        ObjectId objectId = new ObjectId(idResenia);
        Optional<ReseniasModel> opt = repository.findById(objectId);
        
        if (opt.isEmpty())
            throw new RuntimeException("Reseña no encontrada");
        
        ReseniasModel model = opt.get();
        model.setMeGusta(model.getMeGusta() + 1);
        
        repository.save(model);
    }

    @Override
    public void comentar(String idResenia, String utilidad, String usuarioId, String comentario) {
        ObjectId reseniaObjectId = new ObjectId(idResenia);
        ObjectId usuarioObjectId = new ObjectId(usuarioId);
        
        Optional<ReseniasModel> opt = repository.findById(reseniaObjectId);
        
        if (opt.isEmpty())
            throw new RuntimeException("Reseña no encontrada");
        
        ReseniasModel model = opt.get();
        
        if (model.getComentarios() == null)
            model.setComentarios(new ArrayList<>());
        
        model.getComentarios().add(new ComentarioResenia(utilidad, usuarioObjectId, comentario));
        
        repository.save(model);
    }

    private ReseniasResponseDTO toDTO(ReseniasModel model) {
        ReseniasResponseDTO dto = new ReseniasResponseDTO();
        
        dto.setId(model.getIdAsString());
        dto.setLibroId(model.getLibroIdAsString());
        dto.setAutorId(model.getAutorIdAsString());
        
        dto.setCalificacion(model.getCalificacion());
        dto.setOpinion(model.getOpinion());
        dto.setArchivosAdjuntos(model.getArchivosAdjuntos());
        dto.setComentarios(model.getComentarios());
        dto.setMeGusta(model.getMeGusta());
        dto.setFecha(model.getFecha());
        
        return dto;
    }

    private ReseniaListViewDTO toListViewDTO(ReseniasModel model) {
        ReseniaListViewDTO dto = new ReseniaListViewDTO();
        
        dto.setId(model.getIdAsString());
        dto.setLibroId(model.getLibroIdAsString());
        dto.setAutorId(model.getAutorIdAsString());
        dto.setCalificacion(model.getCalificacion());
        
        String opinion = model.getOpinion();
        dto.setResumenOpinion(opinion.length() > 100 ? opinion.substring(0, 100) + "..." : opinion);
        
        dto.setTotalComentarios(model.getComentarios() != null ? model.getComentarios().size() : 0);
        dto.setTieneAdjuntos(model.getArchivosAdjuntos() != null && !model.getArchivosAdjuntos().isEmpty());
        dto.setCreadaEnUTC(model.getFecha());
        
        try {
            LibrosResponseDTO libro = librosService.buscarLibroPorId(model.getLibroIdAsString());
            dto.setLibroTitulo(libro.getTitulo());
        } catch (Exception e) {
            dto.setLibroTitulo("Desconocido");
        }
        
        try {
            UsuariosModel usuario = usuariosService.buscarUsuarioPorId(model.getAutorId());
            dto.setAutorNombreCompleto(usuario.getNombre());
        } catch (Exception e) {
            dto.setAutorNombreCompleto("Desconocido");
        }
        
        return dto;
    }

    private ReseniaDetailDTO toDetailDTO(ReseniasModel model, String usuarioAutenticadoId) {
        ReseniaDetailDTO dto = new ReseniaDetailDTO();
        
        dto.setId(model.getIdAsString());
        dto.setLibroId(model.getLibroIdAsString());
        dto.setAutorId(model.getAutorIdAsString());
        dto.setCalificacion(model.getCalificacion());
        dto.setOpinionCompleta(model.getOpinion());
        dto.setComentarios(model.getComentarios());
        dto.setArchivosAdjuntos(model.getArchivosAdjuntos());
        dto.setCreadaEnUTC(model.getFecha());
        
        dto.setEsAutor(model.getAutorIdAsString().equals(usuarioAutenticadoId));
        dto.setPuedeEditarEliminar(dto.isEsAutor());
        
        try {
            LibrosResponseDTO libro = librosService.buscarLibroPorId(model.getLibroIdAsString());
            dto.setLibroTitulo(libro.getTitulo());
            dto.setLibroAutor(libro.getAutor());
        } catch (Exception e) {
            dto.setLibroTitulo("Desconocido");
            dto.setLibroAutor("Desconocido");
        }
        
        try {
            UsuariosModel usuario = usuariosService.buscarUsuarioPorId(model.getAutorId());
            dto.setAutorNombreCompleto(usuario.getNombre());
        } catch (Exception e) {
            dto.setAutorNombreCompleto("Desconocido");
        }
        
        return dto;
    }
}