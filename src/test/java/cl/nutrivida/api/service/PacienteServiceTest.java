package cl.nutrivida.api.service;

import cl.nutrivida.api.dto.PacienteDTO;
import cl.nutrivida.api.model.Paciente;
import cl.nutrivida.api.repository.PacienteRepository;
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
@DisplayName("PacienteService – pruebas unitarias")
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteService pacienteService;

    private Paciente pacienteEjemplo;
    private PacienteDTO pacienteDtoEjemplo;

    @BeforeEach
    void setUp() {
        pacienteEjemplo = new Paciente();
        pacienteEjemplo.setId(1L);
        pacienteEjemplo.setNombre("María");
        pacienteEjemplo.setApellido("González");
        pacienteEjemplo.setRut("11.111.111-1");
        pacienteEjemplo.setComuna("Providencia");
        pacienteEjemplo.setCiudad("Santiago");

        pacienteDtoEjemplo = new PacienteDTO();
        pacienteDtoEjemplo.setNombre("María");
        pacienteDtoEjemplo.setApellido("González");
        pacienteDtoEjemplo.setRut("11.111.111-1");
        pacienteDtoEjemplo.setComuna("Providencia");
        pacienteDtoEjemplo.setCiudad("Santiago");
    }

    // ── guardar (upsert) ──────────────────────────────────────────────────

    @Test
    @DisplayName("guardar con RUT nuevo crea un paciente")
    void guardar_rutNuevo_creaRegistro() {
        when(pacienteRepository.findByRut("11.111.111-1")).thenReturn(Optional.empty());
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(inv -> inv.getArgument(0));

        Paciente resultado = pacienteService.guardar(pacienteDtoEjemplo);

        assertThat(resultado.getNombre()).isEqualTo("María");
        verify(pacienteRepository).save(any(Paciente.class));
    }

    @Test
    @DisplayName("guardar con RUT existente actualiza el paciente")
    void guardar_rutExistente_actualizaRegistro() {
        when(pacienteRepository.findByRut("11.111.111-1")).thenReturn(Optional.of(pacienteEjemplo));
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(inv -> inv.getArgument(0));

        pacienteDtoEjemplo.setCiudad("Valparaíso");
        Paciente resultado = pacienteService.guardar(pacienteDtoEjemplo);

        assertThat(resultado.getCiudad()).isEqualTo("Valparaíso");
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    // ── obtenerPorRut ─────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorRut con RUT existente devuelve paciente")
    void obtenerPorRut_existente_devuelvePaciente() {
        when(pacienteRepository.findByRut("11.111.111-1")).thenReturn(Optional.of(pacienteEjemplo));

        Paciente resultado = pacienteService.obtenerPorRut("11.111.111-1");

        assertThat(resultado).isEqualTo(pacienteEjemplo);
    }

    @Test
    @DisplayName("obtenerPorRut con RUT inexistente lanza 404")
    void obtenerPorRut_inexistente_lanza404() {
        when(pacienteRepository.findByRut("99.999.999-9")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pacienteService.obtenerPorRut("99.999.999-9"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Paciente no encontrado");
    }

    // ── obtenerPorId ──────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId con ID existente devuelve paciente")
    void obtenerPorId_existente_devuelvePaciente() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteEjemplo));

        Paciente resultado = pacienteService.obtenerPorId(1L);

        assertThat(resultado).isEqualTo(pacienteEjemplo);
    }

    @Test
    @DisplayName("obtenerPorId con ID inexistente lanza 404")
    void obtenerPorId_inexistente_lanza404() {
        when(pacienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pacienteService.obtenerPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Paciente no encontrado");
    }

    // ── actualizar ────────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizar con ID existente aplica cambios del DTO")
    void actualizar_existente_aplicaCambios() {
        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(pacienteEjemplo));
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(inv -> inv.getArgument(0));

        pacienteDtoEjemplo.setComuna("Las Condes");
        Paciente resultado = pacienteService.actualizar(1L, pacienteDtoEjemplo);

        assertThat(resultado.getComuna()).isEqualTo("Las Condes");
    }

    // ── eliminar ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar con ID existente llama deleteById")
    void eliminar_existente_llamaDeleteById() {
        when(pacienteRepository.existsById(1L)).thenReturn(true);

        pacienteService.eliminar(1L);

        verify(pacienteRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar con ID inexistente lanza 404")
    void eliminar_inexistente_lanza404() {
        when(pacienteRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> pacienteService.eliminar(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Paciente no encontrado");
    }

    // ── listarTodos ───────────────────────────────────────────────────────

    @Test
    @DisplayName("listarTodos devuelve todos los pacientes")
    void listarTodos_devuelveListaCompleta() {
        when(pacienteRepository.findAll()).thenReturn(List.of(pacienteEjemplo));

        List<Paciente> resultado = pacienteService.listarTodos();

        assertThat(resultado).hasSize(1);
    }
}