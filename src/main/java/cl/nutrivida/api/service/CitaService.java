package cl.nutrivida.api.service;

import cl.nutrivida.api.model.Cita;
import cl.nutrivida.api.repository.CitaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CitaService {

    private final CitaRepository repository;

    public CitaService(CitaRepository repository) {
        this.repository = repository;
    }

    public List<Cita> listarTodas() {
        return repository.findAll();
    }

    public Cita obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cita no encontrada"));
    }

    public List<Cita> porEstado(String estado) {
        return repository.findByEstado(estado);
    }

    public List<Cita> porPaciente(String rut) {
        return repository.findByPacienteRut(rut);
    }

    public Cita crear(Cita cita) {
        cita.setId(null);
        cita.setEstado("Pendiente");
        return repository.save(cita);
    }

    public Cita cambiarEstado(Long id, String nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado no puede estar vacío");
        }
        Cita cita = obtenerPorId(id);
        cita.setEstado(nuevoEstado);
        return repository.save(cita);
    }

    public Cita actualizar(Long id, Cita datos) {
        Cita cita = obtenerPorId(id);
        cita.setDoctor(datos.getDoctor());
        cita.setEspecialidad(datos.getEspecialidad());
        cita.setModalidad(datos.getModalidad());
        cita.setFecha(datos.getFecha());
        cita.setHora(datos.getHora());
        cita.setMetodoPago(datos.getMetodoPago());
        cita.setEstado(datos.getEstado());
        return repository.save(cita);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada");
        }
        repository.deleteById(id);
    }
}
