package cl.nutrivida.api.service;

import cl.nutrivida.api.model.Paciente;
import cl.nutrivida.api.repository.PacienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    public List<Paciente> listarTodos() {
        return repository.findAll();
    }

    public Paciente obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Paciente no encontrado"));
    }

    public Paciente obtenerPorRut(String rut) {
        return repository.findByRut(rut)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Paciente no encontrado"));
    }

    // Upsert: si ya existe el RUT lo actualiza, si no lo crea
    public Paciente guardar(Paciente paciente) {
        return repository.findByRut(paciente.getRut())
                .map(existente -> {
                    existente.setNombre(paciente.getNombre());
                    existente.setApellido(paciente.getApellido());
                    existente.setTelefono(paciente.getTelefono());
                    existente.setComuna(paciente.getComuna());
                    existente.setCiudad(paciente.getCiudad());
                    existente.setPatologias(paciente.getPatologias());
                    existente.setDestinatario(paciente.getDestinatario());
                    return repository.save(existente);
                })
                .orElseGet(() -> {
                    paciente.setId(null);
                    return repository.save(paciente);
                });
    }

    public Paciente actualizar(Long id, Paciente datos) {
        Paciente paciente = obtenerPorId(id);
        paciente.setNombre(datos.getNombre());
        paciente.setApellido(datos.getApellido());
        paciente.setTelefono(datos.getTelefono());
        paciente.setComuna(datos.getComuna());
        paciente.setCiudad(datos.getCiudad());
        paciente.setPatologias(datos.getPatologias());
        return repository.save(paciente);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado");
        }
        repository.deleteById(id);
    }
}
