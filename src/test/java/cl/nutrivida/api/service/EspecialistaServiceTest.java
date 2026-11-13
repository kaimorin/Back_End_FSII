package cl.nutrivida.api.service;

import cl.nutrivida.api.dto.EspecialistaDTO;
import cl.nutrivida.api.model.Especialista;
import cl.nutrivida.api.repository.EspecialistaRepository;
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
@DisplayName("EspecialistaService – pruebas unitarias")
class EspecialistaServiceTest {

    @Mock
    private EspecialistaRepository especialistaRepository;

    @InjectMocks
    private EspecialistaService especialistaService;

    private Especialista especialistaEjemplo;
    private EspecialistaDTO especialistaDtoEjemplo;

    @BeforeEach
    void setUp() {
        especialistaEjemplo = new Especialista();
        especialistaEjemplo.setId(1L);
        especialistaEjemplo.setNombre("Dr. Carlos Ruiz");
        especialistaEjemplo.setEspecialidad("nutricion-clinica");
        especialistaEjemplo.setModalidad("Presencial");
        especialistaEjemplo.setActivo(true);

        especialistaDtoEjemplo = new EspecialistaDTO();
        especialistaDtoEjemplo.setNombre("Dr. Carlos Ruiz");
        especialistaDtoEjemplo.setEspecialidad("nutricion-clinica");
        especialistaDtoEjemplo.setModalidad("Presencial");
        especialistaDtoEjemplo.setActivo(true);
    }

    // ── crear ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("crear con DTO válido guarda y devuelve el especialista")
    void crear_conDtoValido_devuelveEspecialista() {
        when(especialistaRepository.save(any(Especialista.class))).thenAnswer(inv -> inv.getArgument(0));

        Especialista resultado = especialistaService.crear(especialistaDtoEjemplo);

        assertThat(resultado.getNombre()).isEqualTo("Dr. Carlos Ruiz");
        assertThat(resultado.isActivo()).isTrue();
        verify(especialistaRepository).save(any(Especialista.class));
    }

    // ── actualizar ────────────────────────────────────────────────────────

    @Test
    @DisplayName("actualizar con ID existente aplica cambios del DTO")
    void actualizar_existente_aplicaCambios() {
        when(especialistaRepository.findById(1L)).thenReturn(Optional.of(especialistaEjemplo));
        when(especialistaRepository.save(any(Especialista.class))).thenAnswer(inv -> inv.getArgument(0));

        especialistaDtoEjemplo.setModalidad("Online");
        Especialista resultado = especialistaService.actualizar(1L, especialistaDtoEjemplo);

        assertThat(resultado.getModalidad()).isEqualTo("Online");
    }

    @Test
    @DisplayName("actualizar con ID inexistente lanza 404")
    void actualizar_inexistente_lanza404() {
        when(especialistaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> especialistaService.actualizar(99L, especialistaDtoEjemplo))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Especialista no encontrado");
    }

    // ── listarActivos ─────────────────────────────────────────────────────

    @Test
    @DisplayName("listarActivos devuelve solo los especialistas con activo=true")
    void listarActivos_soloDevuelveActivos() {
        Especialista inactivo = new Especialista();
        inactivo.setActivo(false);

        when(especialistaRepository.findByActivoTrue()).thenReturn(List.of(especialistaEjemplo));

        List<Especialista> resultado = especialistaService.listarActivos();

        assertThat(resultado).allMatch(Especialista::isActivo);
        assertThat(resultado).hasSize(1);
    }

    // ── obtenerPorId ──────────────────────────────────────────────────────

    @Test
    @DisplayName("obtenerPorId con ID existente devuelve el especialista")
    void obtenerPorId_existente_devuelveEspecialista() {
        when(especialistaRepository.findById(1L)).thenReturn(Optional.of(especialistaEjemplo));

        Especialista resultado = especialistaService.obtenerPorId(1L);

        assertThat(resultado).isEqualTo(especialistaEjemplo);
    }

    @Test
    @DisplayName("obtenerPorId con ID inexistente lanza 404")
    void obtenerPorId_inexistente_lanza404() {
        when(especialistaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> especialistaService.obtenerPorId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Especialista no encontrado");
    }

    // ── eliminar ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("eliminar con ID existente llama deleteById")
    void eliminar_existente_llamaDeleteById() {
        when(especialistaRepository.existsById(1L)).thenReturn(true);

        especialistaService.eliminar(1L);

        verify(especialistaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar con ID inexistente lanza 404")
    void eliminar_inexistente_lanza404() {
        when(especialistaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> especialistaService.eliminar(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Especialista no encontrado");
    }

    // ── listarTodos ───────────────────────────────────────────────────────

    @Test
    @DisplayName("listarTodos devuelve todos los especialistas")
    void listarTodos_devuelveListaCompleta() {
        when(especialistaRepository.findAll()).thenReturn(List.of(especialistaEjemplo));

        List<Especialista> resultado = especialistaService.listarTodos();

        assertThat(resultado).containsExactly(especialistaEjemplo);
    }
}
