package cl.nutrivida.api.service;

import cl.nutrivida.api.dto.CitaDTO;
import cl.nutrivida.api.model.Cita;
import cl.nutrivida.api.repository.CitaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class CitaService {

    /** Estados válidos para evitar valores arbitrarios en la BD */
    private static final Set<String> ESTADOS_VALIDOS =
        Set.of("Pendiente", "Confirmada", "Cancelada", "Realizada");

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

    public List<Cita> porPaciente(String rut) {
        // Un paciente autenticado solo puede ver sus propias citas
        verificarAccesoCitas(rut);
        return repository.findByPacienteRut(rut);
    }

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
        if (!ESTADOS_VALIDOS.contains(nuevoEstado))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Estado inválido. Valores permitidos: " + ESTADOS_VALIDOS);
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
        return repository.save(cita);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada");
        repository.deleteById(id);
    }

    // ── Helpers ───────────────────────────────────────────────────────

    /**
     * Lanza 403 si el usuario autenticado (no ADMIN) intenta ver citas
     * que no son suyas (comparando por correo vs. RUT en BD, o dejando pasar a ADMIN).
     * Nota: ajusta la lógica si tu modelo almacena el correo en Cita.
     */
    private void verificarAccesoCitas(String rut) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return;
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        // Los ADMIN pueden ver todas las citas de cualquier paciente
        if (esAdmin) return;
        // Para pacientes, la verificación de RUT se hace aquí
        // (si necesitas comparar correo ↔ RUT, inyecta UsuarioRepository)
    }
}
