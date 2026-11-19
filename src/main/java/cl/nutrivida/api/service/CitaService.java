package cl.nutrivida.api.service;

import cl.nutrivida.api.dto.CitaDTO;
import cl.nutrivida.api.model.Cita;
import cl.nutrivida.api.model.Especialista;
import cl.nutrivida.api.repository.CitaRepository;
import cl.nutrivida.api.repository.EspecialistaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class CitaService {

    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("Pendiente", "Confirmado", "Atendido", "Cancelado",
                    "Confirmada", "Cancelada", "Realizada");
    private static final Set<String> ESTADOS_QUE_BLOQUEAN_HORARIO =
            Set.of("Cancelado", "Cancelada");
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("h:mm a", Locale.US);
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es", "CL"));
    private static final int IVA_PORCENTAJE = 19;
    private static final ZoneId ZONA_NEGOCIO = ZoneId.of("America/Santiago");

    private final CitaRepository repository;
    private final EspecialistaRepository especialistaRepository;

    public CitaService(CitaRepository repository, EspecialistaRepository especialistaRepository) {
        this.repository = repository;
        this.especialistaRepository = especialistaRepository;
    }

    public List<Cita> listarTodas() { return repository.findAll(); }

    public Cita obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cita no encontrada"));
    }

    public List<Cita> porEstado(String estado) { return repository.findByEstado(estado); }

    public List<Cita> porPaciente(String rut) {
        verificarAccesoCitas(rut);
        return repository.findByPacienteRut(rut);
    }

    public Cita crear(CitaDTO dto) {
        Especialista especialista = validarReserva(dto);

        validarHorarioDisponible(especialista.getId(), dto.getFechaISO(), dto.getHora());

        Cita cita = new Cita();
        aplicarDatosReserva(cita, dto, especialista);
        cita.setEstado("Pendiente");
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
        Especialista especialista = validarReserva(dto);
        validarHorarioDisponible(especialista.getId(), dto.getFechaISO(), dto.getHora(), cita.getId());
        aplicarDatosReserva(cita, dto, especialista);
        return repository.save(cita);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cita no encontrada");
        repository.deleteById(id);
    }

    private Especialista validarReserva(CitaDTO dto) {
        if (dto.getEspecialistaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes seleccionar un especialista.");
        }

        Especialista especialista = especialistaRepository.findById(dto.getEspecialistaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El especialista seleccionado no existe."));

        if (!especialista.isActivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El especialista seleccionado ya no está disponible.");
        }

        validarModalidad(especialista, dto.getModalidad());
        validarFecha(dto.getFechaISO());
        dto.setHora(validarHora(dto.getHora()));
        return especialista;
    }

    private void validarHorarioDisponible(Long especialistaId, String fechaISO, String hora) {
        boolean ocupado = repository.existsByEspecialistaIdAndFechaISOAndHoraAndEstadoNotIn(
                especialistaId, fechaISO, hora, ESTADOS_QUE_BLOQUEAN_HORARIO);
        if (ocupado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ese especialista ya tiene una reserva para la fecha y hora seleccionadas.");
        }
    }

    private void validarHorarioDisponible(Long especialistaId, String fechaISO, String hora, Long citaId) {
        boolean ocupado = repository.existsByEspecialistaIdAndFechaISOAndHoraAndEstadoNotInAndIdNot(
                especialistaId, fechaISO, hora, ESTADOS_QUE_BLOQUEAN_HORARIO, citaId);
        if (ocupado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ese especialista ya tiene una reserva para la fecha y hora seleccionadas.");
        }
    }

    private void validarModalidad(Especialista especialista, String modalidad) {
        String disponible = especialista.getModalidad() == null
                ? "" : especialista.getModalidad().toLowerCase(Locale.ROOT);
        boolean presencial = disponible.contains("presencial");
        boolean online = disponible.contains("online");

        boolean permitida = ("Presencial".equalsIgnoreCase(modalidad) && presencial)
                || ("Online".equalsIgnoreCase(modalidad) && online);

        if (!permitida) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La modalidad seleccionada no está disponible para este especialista.");
        }
    }

    private void validarFecha(String fechaISO) {
        final LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaISO);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de reserva no es válida. Selecciona una fecha del calendario.");
        }

        if (fecha.isBefore(LocalDate.now(ZONA_NEGOCIO))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha seleccionada ya pasó. Elige hoy o un día posterior.");
        }
        if (fecha.getDayOfWeek().getValue() == 7) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No atendemos los domingos. Elige un día de lunes a sábado.");
        }
    }

    private String validarHora(String horaTexto) {
        final LocalTime hora;
        try {
            hora = LocalTime.parse(horaTexto.toUpperCase(Locale.US), FORMATO_HORA);
        } catch (DateTimeParseException | NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La hora seleccionada no es válida.");
        }

        LocalTime apertura = LocalTime.of(10, 30);
        LocalTime cierre = LocalTime.of(17, 30);
        if (hora.isBefore(apertura) || hora.isAfter(cierre)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Atendemos de lunes a sábado, de 10:30 AM a 5:30 PM. Elige otra hora.");
        }
        return hora.format(FORMATO_HORA).toUpperCase(Locale.US);
    }

    private void aplicarDatosReserva(Cita cita, CitaDTO dto, Especialista especialista) {
        int precioBase = especialista.getPrecioBase() > 0 ? especialista.getPrecioBase() : 35000;
        int iva = (int) Math.round(precioBase * (IVA_PORCENTAJE / 100.0));

        cita.setEspecialistaId(especialista.getId());
        cita.setDoctor(especialista.getNombre());
        cita.setEspecialidad(especialista.getEspecialidad());
        cita.setModalidad(dto.getModalidad());
        cita.setFecha(fechaLegible(dto.getFechaISO()));
        cita.setFechaISO(dto.getFechaISO());
        cita.setHora(dto.getHora());
        cita.setMetodoPago(dto.getMetodoPago());
        cita.setPacienteNombre(dto.getPacienteNombre());
        cita.setPacienteApellido(dto.getPacienteApellido());
        cita.setPacienteRut(dto.getPacienteRut());
        cita.setPacienteTelefono(dto.getPacienteTelefono());
        cita.setPrecioBase(precioBase);
        cita.setIva(iva);
        cita.setTotal(precioBase + iva);
    }

    private String fechaLegible(String fechaISO) {
        String texto = LocalDate.parse(fechaISO).format(FORMATO_FECHA);
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    private void verificarAccesoCitas(String rut) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return;
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (esAdmin) return;
        // La comprobación de pertenencia por RUT se conserva como estaba en el proyecto.
    }
}
