package cl.nutrivida.api.service;

import cl.nutrivida.api.dto.CitaDTO;
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

    public List<Cita> listarTodas() { return repository.findAll(); }

    public Cita obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cita no encontrada"));
    }

    public List<Cita> porEstado(String estado) { return repository.findByEstado(estado); }

    public List<Cita> porPaciente(String rut) { return repository.findByPacienteRut(rut); }

    public Cita crear(CitaDTO dto) {
        Cita cita = new Cita();
        cita.setDoctor(dto.getDoctor());
        cita.setEspecialidad(dto.getEspecialidad());
        cita.setModalidad(dto.getModalidad());
        cita.setFecha(dto.getFecha());
        cita.setHora(dto.getHora());
        cita.setMetodoPago(dto.getMetodoPago());
        cita.setPacienteNombre(dto.getPacienteNombre());
        cita.setPacienteApellido(dto.getPacienteApellido());
        cita.setPacienteRut(dto.getPacienteRut());
        cita.setPacienteTelefono(dto.getPacienteTelefono());
        cita.setEstado("Pendiente");
        cita.setPrecioBase(35000);
        return repository.save(cita);
    }

    public Cita cambiarEstado(Long id, String nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado no puede estar vacío");
        Cita cita = obtenerPorId(id);
        cita.setEstado(nuevoEstado);
        return repository.save(cita);
    }

    public Cita actualizar(Long id, CitaDTO dto) {
        Cita cita = obtenerPorId(id);
        cita.setDoctor(dto.getDoctor());
        cita.setEspecialidad(dto.getEspecialidad());
        cita.setModalidad(dto.getModalidad());
        cita.setFecha(dto.getFecha());
        cita.setHora(dto.getHora());
        cita.setMetodoPago(dto.getMetodoPago());
        // estado no se toca aquí, usa el endpoint PATCH /estado
        return repository.save(cita);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada");
        repository.deleteById(id);
    }
}
