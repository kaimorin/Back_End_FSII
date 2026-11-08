package cl.nutrivida.api.repository;

import cl.nutrivida.api.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByEstado(String estado);
    List<Cita> findByDoctor(String doctor);
    List<Cita> findByPacienteRut(String rut);
    List<Cita> findByFecha(String fecha);
}
