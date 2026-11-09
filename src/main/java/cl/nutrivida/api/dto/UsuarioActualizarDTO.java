package cl.nutrivida.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Campos que el usuario puede modificar de su perfil.
 * Excluye: id, correo, rut, rol, clave (tienen endpoints propios).
 */
public class UsuarioActualizarDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    private String telefono;

    private boolean recibirNoticias;

    // ── Getters & Setters ──────────────────────────────────────────────
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public boolean isRecibirNoticias() { return recibirNoticias; }
    public void setRecibirNoticias(boolean recibirNoticias) { this.recibirNoticias = recibirNoticias; }
}
