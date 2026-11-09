package cl.nutrivida.api.service;

import cl.nutrivida.api.dto.EspecialistaDTO;
import cl.nutrivida.api.model.Especialista;
import cl.nutrivida.api.repository.EspecialistaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EspecialistaService {

    private final EspecialistaRepository repository;

    public EspecialistaService(EspecialistaRepository repository) {
        this.repository = repository;
    }

    public List<Especialista> listarTodos() { return repository.findAll(); }

    public List<Especialista> listarActivos() { return repository.findByActivoTrue(); }

    public Especialista obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Especialista no encontrado"));
    }

    public Especialista crear(EspecialistaDTO dto) {
        Especialista e = new Especialista();
        e.setNombre(dto.getNombre());
        e.setEspecialidad(dto.getEspecialidad());
        e.setModalidad(dto.getModalidad());
        e.setDescripcion(dto.getDescripcion());
        e.setActivo(dto.isActivo());
        return repository.save(e);
    }

    public Especialista actualizar(Long id, EspecialistaDTO dto) {
        Especialista e = obtenerPorId(id);
        e.setNombre(dto.getNombre());
        e.setEspecialidad(dto.getEspecialidad());
        e.setModalidad(dto.getModalidad());
        e.setDescripcion(dto.getDescripcion());
        e.setActivo(dto.isActivo());
        return repository.save(e);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Especialista no encontrado");
        repository.deleteById(id);
    }
}
