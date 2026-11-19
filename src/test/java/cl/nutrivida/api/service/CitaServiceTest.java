package cl.nutrivida.api.service;

import cl.nutrivida.api.dto.CitaDTO;
import cl.nutrivida.api.model.Cita;
import cl.nutrivida.api.model.Especialista;
import cl.nutrivida.api.repository.CitaRepository;
import cl.nutrivida.api.repository.EspecialistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CitaService – pruebas unitarias")
class CitaServiceTest {

    @Mock private CitaRepository citaRepository;
    @Mock private EspecialistaRepository especialistaRepository;

    @InjectMocks private CitaService citaService;

    private Cita citaEjemplo;
    private CitaDTO citaDtoEjemplo;
    private Especialista especialistaEjemplo;

    @BeforeEach
    void setUp() {
        especialistaEjemplo = new Especialista();
        especialistaEjemplo.setId(10L);
        especialistaEjemplo.setNombre("Dra. Ana López");
        especialistaEjemplo.setEspecialidad("Nutrición Clínica");
        especialistaEjemplo.setModalidad("Presencial y Online");
        especialistaEjemplo.setPrecioBase(42000);
        especialistaEjemplo.setActivo(true);

        citaEjemplo = new Cita();
        citaEjemplo.setId(1L);
        citaEjemplo.setEspecialistaId(10L);
        citaEjemplo.setDoctor("Dra. Ana López");
        citaEjemplo.setEspecialidad("Nutrición Clínica");
        citaEjemplo.setModalidad("Presencial");
        citaEjemplo.setFecha("Lunes, 10 de marzo de 2025");
        citaEjemplo.setFechaISO(fechaFutura().toString());
        citaEjemplo.setHora("11:00 AM");
        citaEjemplo.setEstado("Pendiente");
        citaEjemplo.setPacienteNombre("Juan");
        citaEjemplo.setPacienteApellido("Pérez");
        citaEjemplo.setPacienteRut("12.345.678-9");

        citaDtoEjemplo = new CitaDTO();
        citaDtoEjemplo.setEspecialistaId(10L);
        citaDtoEjemplo.setModalidad("Presencial");
        citaDtoEjemplo.setFecha("Fecha de prueba");
        citaDtoEjemplo.setFechaISO(fechaFutura().toString());
        citaDtoEjemplo.setHora("11:00 AM");
        citaDtoEjemplo.setPacienteNombre("Juan");
        citaDtoEjemplo.setPacienteApellido("Pérez");
        citaDtoEjemplo.setPacienteRut("12.345.678-9");

        lenient().when(especialistaRepository.findById(10L)).thenReturn(Optional.of(especialistaEjemplo));
        SecurityContextHolder.clearContext();
    }

    private LocalDate fechaFutura() {
        LocalDate fecha = LocalDate.now(ZoneId.of("America/Santiago")).plusDays(1);
        while (fecha.getDayOfWeek().getValue() == 7) fecha = fecha.plusDays(1);
        return fecha;
    }

    private void autenticarComoAdmin() {
        var auth = new UsernamePasswordAuthenticationToken(
                "admin@test.cl", null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void autenticarComoPaciente(String correo) {
        var auth = new UsernamePasswordAuthenticationToken(
                correo, null,
                List.of(new SimpleGrantedAuthority("ROLE_PACIENTE")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("listarTodas devuelve todas las citas del repositorio")
    void listarTodas_devuelveListaCompleta() {
        when(citaRepository.findAll()).thenReturn(List.of(citaEjemplo));
        assertThat(citaService.listarTodas()).containsExactly(citaEjemplo);
    }

    @Test
    @DisplayName("obtenerPorId con ID existente devuelve la cita")
    void obtenerPorId_existente_devuelveCita() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEjemplo));
        assertThat(citaService.obtenerPorId(1L)).isEqualTo(citaEjemplo);
    }

    @Test
    @DisplayName("obtenerPorId con ID inexistente lanza 404")
    void obtenerPorId_inexistente_lanza404() {
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> citaService.obtenerPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Cita no encontrada");
    }

    @Test
    @DisplayName("crear toma el precio del especialista y calcula IVA y total")
    void crear_calculaPrecioSegunEspecialista() {
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        Cita resultado = citaService.crear(citaDtoEjemplo);

        assertThat(resultado.getEstado()).isEqualTo("Pendiente");
        assertThat(resultado.getEspecialistaId()).isEqualTo(10L);
        assertThat(resultado.getDoctor()).isEqualTo("Dra. Ana López");
        assertThat(resultado.getPrecioBase()).isEqualTo(42000);
        assertThat(resultado.getIva()).isEqualTo(7980);
        assertThat(resultado.getTotal()).isEqualTo(49980);
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    @DisplayName("crear rechaza fechas anteriores a hoy")
    void crear_rechazaFechaPasada() {
        citaDtoEjemplo.setFechaISO(LocalDate.now(ZoneId.of("America/Santiago")).minusDays(1).toString());
        assertThatThrownBy(() -> citaService.crear(citaDtoEjemplo))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ya pasó");
        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    @DisplayName("crear rechaza domingos")
    void crear_rechazaDomingo() {
        LocalDate domingo = LocalDate.now(ZoneId.of("America/Santiago")).plusDays(1);
        while (domingo.getDayOfWeek().getValue() != 7) domingo = domingo.plusDays(1);
        citaDtoEjemplo.setFechaISO(domingo.toString());
        assertThatThrownBy(() -> citaService.crear(citaDtoEjemplo))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("domingos");
    }

    @Test
    @DisplayName("crear rechaza un horario que ya está reservado")
    void crear_rechazaHorarioOcupado() {
        when(citaRepository.existsByEspecialistaIdAndFechaISOAndHoraAndEstadoNotIn(
                eq(10L), anyString(), eq("11:00 AM"), anyCollection())).thenReturn(true);

        assertThatThrownBy(() -> citaService.crear(citaDtoEjemplo))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("ya tiene una reserva");
    }

    @Test
    @DisplayName("cambiarEstado con estado válido actualiza la cita")
    void cambiarEstado_conEstadoConfirmado_actualizaEstado() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEjemplo));
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));
        assertThat(citaService.cambiarEstado(1L, "Confirmado").getEstado()).isEqualTo("Confirmado");
    }

    @Test
    @DisplayName("cambiarEstado con estado inválido lanza 400")
    void cambiarEstado_conEstadoInvalido_lanza400() {
        assertThatThrownBy(() -> citaService.cambiarEstado(1L, "Estado inventado"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Estado inválido");
    }

    @Test
    @DisplayName("cambiarEstado con estado vacío lanza 400")
    void cambiarEstado_conEstadoVacio_lanza400() {
        assertThatThrownBy(() -> citaService.cambiarEstado(1L, ""))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("no puede estar vacío");
    }

    @Test
    @DisplayName("cambiarEstado con estado null lanza 400")
    void cambiarEstado_conEstadoNull_lanza400() {
        assertThatThrownBy(() -> citaService.cambiarEstado(1L, null))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @DisplayName("eliminar con ID existente llama deleteById")
    void eliminar_citaExistente_llamaDeleteById() {
        when(citaRepository.existsById(1L)).thenReturn(true);
        citaService.eliminar(1L);
        verify(citaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar con ID inexistente lanza 404")
    void eliminar_citaInexistente_lanza404() {
        when(citaRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> citaService.eliminar(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Cita no encontrada");
    }

    @Test
    @DisplayName("porEstado delega en repositorio")
    void porEstado_devuelveResultadosDelRepo() {
        when(citaRepository.findByEstado("Pendiente")).thenReturn(List.of(citaEjemplo));
        assertThat(citaService.porEstado("Pendiente")).containsExactly(citaEjemplo);
    }

    @Test
    @DisplayName("porPaciente como ADMIN devuelve citas del paciente")
    void porPaciente_comoAdmin_devuelveResultados() {
        autenticarComoAdmin();
        when(citaRepository.findByPacienteRut("12.345.678-9")).thenReturn(List.of(citaEjemplo));
        assertThat(citaService.porPaciente("12.345.678-9")).containsExactly(citaEjemplo);
    }

    @Test
    @DisplayName("porPaciente como PACIENTE devuelve citas del paciente")
    void porPaciente_comoPaciente_devuelveResultados() {
        autenticarComoPaciente("juan@test.cl");
        when(citaRepository.findByPacienteRut("12.345.678-9")).thenReturn(List.of(citaEjemplo));
        assertThat(citaService.porPaciente("12.345.678-9")).containsExactly(citaEjemplo);
    }
}
