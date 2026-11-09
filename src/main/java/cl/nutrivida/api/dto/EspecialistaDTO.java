package cl.nutrivida.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Campos permitidos al crear o editar un especialista.
 * Excluye: id (lo genera la BD).
 */
public class EspecialistaDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La especialidad es obligatoria")
    private String especialidad;

    @NotBlank(message = "La modalidad es obligatoria")
    private String modalidad;

    private String descripcion;

    private boolean activo = true;

    // ── Getters & Setters ──────────────────────────────────────────────
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
