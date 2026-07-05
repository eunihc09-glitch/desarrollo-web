package mx.plataforma.ciberseguridad.modelo;

/**
 * Representa a un usuario registrado en la plataforma de ciberseguridad.
 * Puede ser Administrador, Analista, Operador, Director, etc.,
 * según el rol que tenga asignado.
 *
 * Relación con el diagrama de clases:
 *   - USUARIO tiene un ROL (asociación)
 *   - USUARIO es referenciado por INCIDENTE como "responsable"
 */
public class Usuario {

    private int id;
    private String nombre;
    private String email;
    private String contrasenaHash;
    private Rol role;
    private boolean activo;
    private int intentosFallidos;

    public Usuario(int id, String nombre, String email, String contrasenaHash, Rol role) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.contrasenaHash = contrasenaHash;
        this.role = role;
        this.activo = true;
        this.intentosFallidos = 0;
    }

    /**
     * Inicia sesión verificando credenciales y el código MFA.
     * Tras 5 intentos fallidos, bloquea la cuenta 15 minutos.
     * @param contrasena Contraseña ingresada por el usuario.
     * @param codigoMfa  Código OTP de la aplicación autenticadora.
     * @return true si el login es exitoso, false en caso contrario.
     */
    public boolean iniciarSesion(String contrasena, String codigoMfa) {
        if (!activo) {
            System.out.println("[USUARIO] Cuenta bloqueada. Intente en 15 minutos.");
            return false;
        }

        boolean credencialesOk = contrasenaHash.equals(simularHash(contrasena));
        boolean mfaOk          = validarMfa(codigoMfa);

        if (credencialesOk && mfaOk) {
            intentosFallidos = 0;
            System.out.println("[USUARIO] Inicio de sesión exitoso. Bienvenido, " + nombre + ".");
            System.out.println("[USUARIO] Rol asignado: " + role.getNombre());
            return true;
        } else {
            intentosFallidos++;
            System.out.println("[USUARIO] Credenciales inválidas. Intento " + intentosFallidos + " de 5.");
            if (intentosFallidos >= 5) {
                activo = false;
                System.out.println("[USUARIO] Cuenta bloqueada por 15 minutos.");
            }
            return false;
        }
    }

    /**
     * Cierra la sesión activa del usuario e invalida el token JWT.
     */
    public void cerrarSesion() {
        System.out.println("[USUARIO] Sesión cerrada para " + nombre + ". Token JWT invalidado.");
    }

    /**
     * Inicia el flujo de recuperación de contraseña enviando
     * un enlace seguro al correo registrado.
     */
    public void recuperarContrasena() {
        System.out.println("[USUARIO] Enlace de recuperación enviado a: " + email);
    }

    /**
     * Verifica si el usuario tiene un permiso específico a través de su rol.
     * @param permiso Permiso a verificar.
     * @return true si tiene el permiso, false en caso contrario.
     */
    public boolean tienePermiso(Permiso permiso) {
        return role != null && role.tienePermiso(permiso);
    }

    // ──────────── Métodos auxiliares privados ────────────

    /** Simulación de hash SHA-256 (en producción usar BCrypt). */
    private String simularHash(String texto) {
        return "HASH_" + texto;
    }

    /** Simulación de validación TOTP/SMS. */
    private boolean validarMfa(String codigo) {
        return codigo != null && codigo.length() == 6;
    }

    // ──────────── Getters y Setters ────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Rol getRole() { return role; }
    public void setRole(Rol role) { this.role = role; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nombre='" + nombre + "', email='" + email
                + "', rol=" + (role != null ? role.getNombre() : "Sin rol")
                + ", activo=" + activo + "}";
    }
}
