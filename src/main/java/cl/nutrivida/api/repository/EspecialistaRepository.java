package cl.nutrivida.api.repository;

import cl.nutrivida.api.model.Especialista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EspecialistaRepository extends JpaRepository<Especialista, Long> {
    List<Especialista> findByActivoTrue();
    List<Especialista> findByEspecialidad(String especialidad);
}
