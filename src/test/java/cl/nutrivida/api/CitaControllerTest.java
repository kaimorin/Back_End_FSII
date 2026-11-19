package cl.nutrivida.api;

import cl.nutrivida.api.model.Especialista;
import cl.nutrivida.api.repository.CitaRepository;
import cl.nutrivida.api.repository.EspecialistaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CitaControllerTest {

    @Autowired MockMvc mvc;
    @Autowired CitaRepository repo;
    @Autowired EspecialistaRepository especialistaRepo;
    @Autowired ObjectMapper mapper;

    @Test
    @WithMockUser(username = "admin@nutrivida.cl", roles = {"ADMIN"})
    void listarCitas_devuelveOk() throws Exception {
        mvc.perform(get("/api/citas"))
           .andExpect(status().isOk())
           .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin@nutrivida.cl", roles = {"ADMIN"})
    void crearCita_devuelveCreatedYCalculaTotales() throws Exception {
        Especialista especialista = new Especialista();
        especialista.setNombre("Maria de Judas");
        especialista.setEspecialidad("Nutrición Clínica");
        especialista.setModalidad("Presencial y Online");
        especialista.setPrecioBase(40000);
        especialista.setActivo(true);
        especialista = especialistaRepo.save(especialista);

        LocalDate fecha = LocalDate.now(ZoneId.of("America/Santiago")).plusDays(1);
        while (fecha.getDayOfWeek().getValue() == 7) fecha = fecha.plusDays(1);

        Map<String, Object> datos = Map.of(
                "especialistaId", especialista.getId(),
                "modalidad", "Presencial",
                "fecha", "Fecha futura de prueba",
                "fechaISO", fecha.toString(),
                "hora", "11:00 AM",
                "pacienteNombre", "Test",
                "pacienteApellido", "Paciente",
                "pacienteRut", "11.111.111-1"
        );

        mvc.perform(post("/api/citas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(datos)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").isNumber())
           .andExpect(jsonPath("$.estado").value("Pendiente"))
           .andExpect(jsonPath("$.doctor").value("Maria de Judas"))
           .andExpect(jsonPath("$.precioBase").value(40000))
           .andExpect(jsonPath("$.iva").value(7600))
           .andExpect(jsonPath("$.total").value(47600));
    }

    @Test
    @WithMockUser(username = "admin@nutrivida.cl", roles = {"ADMIN"})
    void obtenerCita_inexistenteDevuelve404() throws Exception {
        mvc.perform(get("/api/citas/99999"))
           .andExpect(status().isNotFound());
    }
}
