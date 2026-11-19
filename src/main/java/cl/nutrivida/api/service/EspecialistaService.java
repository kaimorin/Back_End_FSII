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

    private static final int PRECIO_INICIAL = 35000;

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
        Especialista especialista = new Especialista();
        aplicarDatos(especialista, dto);
        return repository.save(especialista);
    }

    public Especialista actualizar(Long id, EspecialistaDTO dto) {
        Especialista especialista = obtenerPorId(id);
        aplicarDatos(especialista, dto);
        return repository.save(especialista);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Especialista no encontrado");
        repository.deleteById(id);
    }

    private void aplicarDatos(Especialista especialista, EspecialistaDTO dto) {
        especialista.setNombre(dto.getNombre().trim());
        especialista.setEspecialidad(dto.getEspecialidad());
        especialista.setModalidad(dto.getModalidad());
        especialista.setDescripcion(dto.getDescripcion());
        especialista.setPrecioBase(dto.getPrecioBase() > 0 ? dto.getPrecioBase() : PRECIO_INICIAL);
        especialista.setActivo(dto.isActivo());
    }
}
