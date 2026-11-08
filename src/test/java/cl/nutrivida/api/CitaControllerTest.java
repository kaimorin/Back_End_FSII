package cl.nutrivida.api;

import cl.nutrivida.api.model.Cita;
import cl.nutrivida.api.repository.CitaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CitaControllerTest {

    @Autowired MockMvc mvc;
    @Autowired CitaRepository repo;
    @Autowired ObjectMapper mapper;

    @Test
    void listarCitas_devuelveOk() throws Exception {
        mvc.perform(get("/api/citas"))
           .andExpect(status().isOk())
           .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void crearCita_devuelveCreated() throws Exception {
        Cita cita = new Cita();
        cita.setDoctor("Maria de Judas");
        cita.setEspecialidad("control-peso");
        cita.setModalidad("Presencial");
        cita.setFecha("Lunes, 10 de marzo de 2025");
        cita.setHora("10:00 AM");
        cita.setPacienteNombre("Test");
        cita.setPacienteApellido("Paciente");
        cita.setPacienteRut("11.111.111-1");

        mvc.perform(post("/api/citas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(cita)))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").isNumber())
           .andExpect(jsonPath("$.estado").value("Pendiente"));
    }

    @Test
    void obtenerCita_inexistente_devuelve404() throws Exception {
        mvc.perform(get("/api/citas/99999"))
           .andExpect(status().isNotFound());
    }
}
