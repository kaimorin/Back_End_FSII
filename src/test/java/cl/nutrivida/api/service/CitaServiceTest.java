package cl.nutrivida.api.service;

import cl.nutrivida.api.dto.CitaDTO;
import cl.nutrivida.api.model.Cita;
import cl.nutrivida.api.repository.CitaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CitaService – pruebas unitarias")
class CitaServiceTest {

    @Mock
    private CitaRepository citaRepository;

    @InjectMocks
    private CitaService citaService;

    private Cita citaEjemplo;
    private CitaDTO citaDtoEjemplo;

    @BeforeEach
    void setUp() {
        citaEjemplo = new Cita();
        citaEjemplo.setId(1L);
        citaEjemplo.setDoctor("Dra. Ana López");
        citaEjemplo.setEspecialidad("control-peso");
        citaEjemplo.setModalidad("Presencial");
        citaEjemplo.setFecha("Lunes, 10 de marzo de 2025");
        citaEjemplo.setHora("10:00 AM");
        citaEjemplo.setEstado("Pendiente");
        citaEjemplo.setPacienteNombre("Juan");
        citaEjemplo.setPacienteApellido("Pérez");
        citaEjemplo.setPacienteRut("12.345.678-9");

        citaDtoEjemplo = new CitaDTO();
        citaDtoEjemplo.setDoctor("Dra. Ana López");
        citaDtoEjemplo.setEspecialidad("control-peso");
        citaDtoEjemplo.setModalidad("Presencial");
        citaDtoEjemplo.setFecha("Lunes, 10 de marzo de 2025");
        citaDtoEjemplo.setHora("10:00 AM");
        citaDtoEjemplo.setPacienteNombre("Juan");
        citaDtoEjemplo.setPacienteApellido("Pérez");
        citaDtoEjemplo.setPacienteRut("12.345.678-9");
    }

    // ── listarTodas ───────────────────────────────────────────────────────

    @Test
    @DisplayName("listarTodas devuelve todas las citas del repositorio")
    void listarTodas_devuelveListaCompleta() {
        when(citaRepository.findAll()).thenReturn(List.of(citaEjemplo));

        List<Cita> resultado = citaService.listarTodas();

        assertThat(resultado).hasSize(1).containsExactly(citaEjemplo);
        verify(citaRepository).findAll();
    }

    // ── obtenerPorId ──────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId con ID existente devuelve la cita")
    void obtenerPorId_existente_devuelveCita() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEjemplo));

        Cita resultado = citaService.obtenerPorId(1L);

        assertThat(resultado).isEqualTo(citaEjemplo);
    }

    @Test
    @DisplayName("obtenerPorId con ID inexistente lanza 404")
    void obtenerPorId_inexistente_lanza404() {
        when(citaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> citaService.obtenerPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Cita no encontrada");
    }

    // ── crear ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("crear con DTO válido asigna estado Pendiente y precioBase 35000")
    void crear_conDtoValido_asignaEstadoYPrecio() {
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        Cita resultado = citaService.crear(citaDtoEjemplo);

        assertThat(resultado.getEstado()).isEqualTo("Pendiente");
        assertThat(resultado.getPrecioBase()).isEqualTo(35000);
        assertThat(resultado.getDoctor()).isEqualTo("Dra. Ana López");
        verify(citaRepository).save(any(Cita.class));
    }

    // ── cambiarEstado ─────────────────────────────────────────────────────

    @Test
    @DisplayName("cambiarEstado con estado válido actualiza la cita")
    void cambiarEstado_conEstadoValido_actualizaEstado() {
        when(citaRepository.findById(1L)).thenReturn(Optional.of(citaEjemplo));
        when(citaRepository.save(any(Cita.class))).thenAnswer(inv -> inv.getArgument(0));

        Cita resultado = citaService.cambiarEstado(1L, "Confirmado");

        assertThat(resultado.getEstado()).isEqualTo("Confirmado");
    }

    @Test
    @DisplayName("cambiarEstado con estado vacío lanza 400")
    void cambiarEstado_conEstadoVacio_lanza400() {
        assertThatThrownBy(() -> citaService.cambiarEstado(1L, ""))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("estado no puede estar vacío");
    }

    @Test
    @DisplayName("cambiarEstado con estado null lanza 400")
    void cambiarEstado_conEstadoNull_lanza400() {
        assertThatThrownBy(() -> citaService.cambiarEstado(1L, null))
                .isInstanceOf(ResponseStatusException.class);
    }

    // ── eliminar ──────────────────────────────────────────────────────────

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

    // ── porEstado y porPaciente ───────────────────────────────────────────

    @Test
    @DisplayName("porEstado delega en repositorio")
    void porEstado_devuelveResultadosDelRepo() {
        when(citaRepository.findByEstado("Pendiente")).thenReturn(List.of(citaEjemplo));

        List<Cita> resultado = citaService.porEstado("Pendiente");

        assertThat(resultado).containsExactly(citaEjemplo);
    }

    @Test
    @DisplayName("porPaciente delega en repositorio")
    void porPaciente_devuelveResultadosDelRepo() {
        when(citaRepository.findByPacienteRut("12.345.678-9")).thenReturn(List.of(citaEjemplo));

        List<Cita> resultado = citaService.porPaciente("12.345.678-9");

        assertThat(resultado).containsExactly(citaEjemplo);
    }
}
