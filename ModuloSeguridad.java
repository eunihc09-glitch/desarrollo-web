package mx.plataforma.ciberseguridad.modelo;

/**
 * Módulo de seguridad responsable de la autenticación de usuarios,
 * el cifrado de datos y el análisis de patrones de comportamiento
 * apoyado por el Motor de IA.
 *
 * Relación con el diagrama de clases:
 *   - MODULO_SEGURIDAD está contenido en PLATAFORMA_SEGURIDAD
 *   - Utiliza MOTORIA para analizarPatrones() y predecirRiesgo()
 */
public class ModuloSeguridad {

    private int id;
    private String nombre;
    private String descripcion;
    private MotorIA motorIA;

    public ModuloSeguridad(int id, String nombre, String descripcion, MotorIA motorIA) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.motorIA = motorIA;
    }

    /**
     * Autentica a un usuario verificando sus credenciales contra
     * la base de datos y activando el flujo MFA.
     * Emite un JWT firmado con RS256 válido por 15 minutos.
     * @param usuario  Objeto usuario con credenciales a verificar.
     * @param contrasena Contraseña en texto plano.
     * @param codigoMfa  Código OTP de la app autenticadora.
     * @return Token JWT simulado o mensaje de error.
     */
    public String autenticarUsuario(Usuario usuario, String contrasena, String codigoMfa) {
        System.out.println("[SEGURIDAD] Iniciando autenticación para: " + usuario.getEmail());
        boolean ok = usuario.iniciarSesion(contrasena, codigoMfa);
        if (ok) {
            String token = "JWT_RS256_" + usuario.getId() + "_" + System.currentTimeMillis();
            System.out.println("[SEGURIDAD] Token JWT emitido (expira en 15 min): " + token);
            return token;
        }
        System.out.println("[SEGURIDAD] Autenticación fallida. Sin token generado.");
        return null;
    }

    /**
     * Aplica cifrado AES-256 al dato sensible proporcionado.
     * En producción utiliza Bouncy Castle / JCE.
     * @param dato Texto plano a cifrar.
     * @return Representación cifrada simulada.
     */
    public String realizarCifrado(String dato) {
        System.out.println("[SEGURIDAD] Cifrando dato con AES-256...");
        String cifrado = "AES256[" + dato.hashCode() + "]";
        System.out.println("[SEGURIDAD] Dato cifrado: " + cifrado);
        return cifrado;
    }

    /**
     * Delega en el Motor de IA el análisis de patrones del evento recibido.
     * @param eventoRaw Evento normalizado para analizar.
     * @return Puntuación de riesgo 0-100.
     */
    public int analizarPatrones(String eventoRaw) {
        System.out.println("[SEGURIDAD] Delegando análisis de patrones al Motor IA...");
        return motorIA.analizarPatrones(eventoRaw);
    }

    /**
     * Delega en el Motor de IA la predicción de riesgo para un activo.
     * @param asset         Activo a evaluar.
     * @param diasHorizonte Horizonte de predicción en días.
     * @return Nivel de riesgo predicho.
     */
    public String predecirRiesgo(String asset, int diasHorizonte) {
        System.out.println("[SEGURIDAD] Solicitando predicción de riesgo para: " + asset);
        return motorIA.predecirRiesgo(asset, diasHorizonte);
    }

    // ──────────── Getters y Setters ────────────

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public MotorIA getMotorIA() { return motorIA; }
    public void setMotorIA(MotorIA motorIA) { this.motorIA = motorIA; }

    @Override
    public String toString() {
        return "ModuloSeguridad{id=" + id + ", nombre='" + nombre + "'}";
    }
}
