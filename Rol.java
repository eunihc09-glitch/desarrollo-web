package mx.plataforma.ciberseguridad.modelo;

import java.util.HashSet;
import java.util.Set;

/**
 * Representa un rol dentro de la plataforma de ciberseguridad.
 * Cada rol agrupa un conjunto de permisos que determinan
 * las acciones que puede realizar un usuario.
 *
 * Relación con el diagrama de clases:
 *   - ROL tiene asociación con USUARIO (un usuario posee un rol)
 *   - ROL contiene un Set<Permiso>
 */
public class Rol {

    private int id;
    private String nombre;
    private Set<Permiso> permisos;

    public Rol(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.permisos = new HashSet<>();
    }

    /**
     * Agrega un permiso al rol, si aún no lo contiene.
     * @param permiso Permiso a agregar.
     */
    public void agregarPermiso(Permiso permiso) {
        if (permisos.contains(permiso)) {
            System.out.println("[ROL] El permiso " + permiso + " ya existe en el rol " + nombre);
        } else {
            permisos.add(permiso);
            System.out.println("[ROL] Permiso " + permiso + " agregado al rol " + nombre);
        }
    }

    /**
     * Elimina un permiso del rol.
     * @param permiso Permiso a remover.
     */
    public void removerPermiso(Permiso permiso) {
        if (permisos.remove(permiso)) {
            System.out.println("[ROL] Permiso " + permiso + " removido del rol " + nombre);
        } else {
            System.out.println("[ROL] El permiso " + permiso + " no existe en el rol " + nombre);
        }
    }

    /**
     * Verifica si el rol tiene un permiso específico.
     * @param permiso Permiso a verificar.
     * @return true si el permiso está asignado, false en caso contrario.
     */
    public boolean tienePermiso(Permiso permiso) {
        return permisos.contains(permiso);
    }

    // ──────────── Getters y Setters ────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Set<Permiso> getPermisos() { return permisos; }

    @Override
    public String toString() {
        return "Rol{id=" + id + ", nombre='" + nombre + "', permisos=" + permisos + "}";
    }
}
