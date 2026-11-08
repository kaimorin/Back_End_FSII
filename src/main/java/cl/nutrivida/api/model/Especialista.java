package cl.nutrivida.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "especialistas")
public class Especialista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    // Nutrición Clínica | Nutrición Deportiva | Salud Digestiva | Control de Peso
    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;

    // Presencial | Online | Presencial y Online
    @NotBlank(message = "La modalidad es obligatoria")
    private String modalidad;

    @Column(length = 500)
    private String descripcion;

    private boolean activo = true;

    // ── Getters & Setters ──────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
