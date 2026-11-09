package cl.nutrivida.api.service;

import cl.nutrivida.api.dto.PacienteDTO;
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

    public List<Paciente> listarTodos() { return repository.findAll(); }

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

    // Upsert: actualiza si ya existe el RUT, crea si es nuevo
    public Paciente guardar(PacienteDTO dto) {
        return repository.findByRut(dto.getRut())
                .map(existente -> aplicarDTO(existente, dto))
                .orElseGet(() -> aplicarDTO(new Paciente(), dto));
    }

    public Paciente actualizar(Long id, PacienteDTO dto) {
        return aplicarDTO(obtenerPorId(id), dto);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente no encontrado");
        repository.deleteById(id);
    }

    // Mapeo DTO → Entidad (nunca toca el id)
    private Paciente aplicarDTO(Paciente p, PacienteDTO dto) {
        p.setNombre(dto.getNombre());
        p.setApellido(dto.getApellido());
        p.setRut(dto.getRut());
        p.setTelefono(dto.getTelefono());
        p.setComuna(dto.getComuna());
        p.setCiudad(dto.getCiudad());
        p.setPatologias(dto.getPatologias());
        p.setDestinatario(dto.getDestinatario());
        return repository.save(p);
    }
}
