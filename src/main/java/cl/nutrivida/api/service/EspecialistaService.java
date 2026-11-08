package cl.nutrivida.api.service;

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

    public List<Especialista> listarTodos() {
        return repository.findAll();
    }

    public List<Especialista> listarActivos() {
        return repository.findByActivoTrue();
    }

    public Especialista obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Especialista no encontrado"));
    }

    public Especialista crear(Especialista especialista) {
        especialista.setId(null);
        return repository.save(especialista);
    }

    public Especialista actualizar(Long id, Especialista datos) {
        Especialista especialista = obtenerPorId(id);
        especialista.setNombre(datos.getNombre());
        especialista.setEspecialidad(datos.getEspecialidad());
        especialista.setModalidad(datos.getModalidad());
        especialista.setDescripcion(datos.getDescripcion());
        especialista.setActivo(datos.isActivo());
        return repository.save(especialista);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Especialista no encontrado");
        }
        repository.deleteById(id);
    }
}
