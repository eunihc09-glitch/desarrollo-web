package mx.plataforma.ciberseguridad.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase central de la plataforma de ciberseguridad para PYMEs.
 * Coordina todos los módulos: autenticación, monitoreo, IA,
 * gestión de incidentes y generación de reportes.
 *
 * Relación con el diagrama de clases:
 *   - PLATAFORMA_SEGURIDAD agrega INCIDENTE y REPORTE (generalización)
 *   - Contiene listas de USUARIO, ROL, INCIDENTE
 *   - Referencia a MODULO_MONITOREO, MODULO_SEGURIDAD y MOTOR_IA
 */
public class PlataformaSeguridad {

    private List<Usuario>    usuarios;
    private List<Rol>        roles;
    private List<Incidente>  incidentes;
    private ModuloMonitoreo  moduloMonitoreo;
    private ModuloSeguridad  moduloSeguridad;
    private MotorIA          motorIA;
    private int              contadorIncidentes;
    private int              contadorReportes;

    public PlataformaSeguridad() {
        this.usuarios           = new ArrayList<>();
        this.roles              = new ArrayList<>();
        this.incidentes         = new ArrayList<>();
        this.contadorIncidentes = 0;
        this.contadorReportes   = 0;

        // Inicializar Motor IA con umbral de riesgo en 70/100
        this.motorIA = new MotorIA("API-KEY-CIBERSEG-2026", "v1.0.0", 70.0);

        // Inicializar módulos
        this.moduloMonitoreo = new ModuloMonitoreo(1, "Monitor Central",
                "Recolección y normalización de eventos de red y endpoints");
        this.moduloSeguridad = new ModuloSeguridad(2, "Módulo de Seguridad",
                "Autenticación MFA, cifrado AES-256 y análisis IA", motorIA);

        System.out.println("════════════════════════════════════════════════");
        System.out.println("  Plataforma de Ciberseguridad para PYMEs");
        System.out.println("  Versión 1.0.0 | Arquitectura: Microservicios");
        System.out.println("════════════════════════════════════════════════");
    }

    /**
     * Registra un nuevo usuario en la plataforma con el rol indicado.
     * @param nombre   Nombre completo del usuario.
     * @param email    Correo electrónico único.
     * @param rol      Rol asignado.
     * @return Usuario creado.
     */
    public Usuario registrarUsuario(String nombre, String email, Rol rol) {
        // Verificar duplicado de correo
        boolean duplicado = usuarios.stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
        if (duplicado) {
            System.out.println("[PLATAFORMA] Error: El correo '" + email + "' ya está registrado.");
            return null;
        }

        int nuevoId = usuarios.size() + 1;
        String hashSimulado = "HASH_P@ssw0rd123";   // En producción: BCrypt
        Usuario nuevoUsuario = new Usuario(nuevoId, nombre, email, hashSimulado, rol);
        usuarios.add(nuevoUsuario);

        System.out.println("[PLATAFORMA] Usuario registrado: " + nuevoUsuario);
        System.out.println("[PLATAFORMA] Correo de activación enviado a: " + email);
        return nuevoUsuario;
    }

    /**
     * Crea y registra un incidente de seguridad.
     * El nivel de riesgo es calculado por el Motor de IA.
     * @param tipo       Tipo de incidente (Port Scan, Phishing, etc.).
     * @param responsable Usuario responsable de su resolución.
     * @param eventoRaw  Evento normalizado para que el IA calcule el riesgo.
     * @return Incidente creado y registrado.
     */
    public Incidente crearIncidente(String tipo, Usuario responsable, String eventoRaw) {
        int nivelRiesgo = motorIA.analizarPatrones(eventoRaw);
        contadorIncidentes++;
        Incidente incidente = new Incidente(contadorIncidentes, tipo, responsable, nivelRiesgo);
        incidente.registrarIncidente();
        incidentes.add(incidente);

        // Si el riesgo es alto, generar alerta inmediata
        if (nivelRiesgo >= 70) {
            Alerta alerta = moduloMonitoreo.detectarAnomalias(tipo + ": " + eventoRaw, nivelRiesgo);
            if (alerta != null) {
                moduloMonitoreo.enviarAlerta(alerta);
            }
        }
        return incidente;
    }

    /**
     * Genera un reporte ejecutivo para el período indicado.
     * @param tipo          Tipo de reporte (EJECUTIVO, TÉCNICO, etc.).
     * @param periodoInicio Fecha de inicio del período.
     * @param periodoFin    Fecha de fin del período.
     * @return Reporte generado y exportado a PDF.
     */
    public Reporte generarReporte(String tipo, LocalDate periodoInicio, LocalDate periodoFin) {
        contadorReportes++;
        Reporte reporte = new Reporte(contadorReportes, tipo, periodoInicio, periodoFin);
        reporte.generarReporte();
        reporte.exportarPDF();
        return reporte;
    }

    /**
     * Ejecuta una alerta inmediata sobre un evento detectado.
     * @param tipo        Tipo de alerta.
     * @param descripcion Descripción del evento.
     * @param severidad   Nivel de severidad (BAJA, MEDIA, ALTA, CRITICA).
     */
    public void ejecutarAlerta(String tipo, String descripcion, String severidad) {
        Alerta alerta = new Alerta(0, tipo, descripcion, severidad);
        alerta.enviarAlerta();
    }

    /**
     * Ejecuta el análisis predictivo del Motor IA para un activo dado.
     * @param asset         Activo a evaluar.
     * @param diasHorizonte Horizonte en días.
     */
    public void ejecutarAnalisisPredictivo(String asset, int diasHorizonte) {
        System.out.println("[PLATAFORMA] Iniciando análisis predictivo...");
        String nivel = motorIA.predecirRiesgo(asset, diasHorizonte);
        System.out.println("[PLATAFORMA] Resultado para '" + asset + "': " + nivel);
    }

    /**
     * Autentica a un usuario a través del Módulo de Seguridad.
     * @param usuario    Usuario a autenticar.
     * @param contrasena Contraseña.
     * @param codigoMfa  Código MFA.
     * @return Token JWT o null si falla.
     */
    public String autenticarUsuario(Usuario usuario, String contrasena, String codigoMfa) {
        return moduloSeguridad.autenticarUsuario(usuario, contrasena, codigoMfa);
    }

    // ──────────── Getters ────────────

    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Rol> getRoles() { return roles; }
    public List<Incidente> getIncidentes() { return incidentes; }
    public ModuloMonitoreo getModuloMonitoreo() { return moduloMonitoreo; }
    public ModuloSeguridad getModuloSeguridad() { return moduloSeguridad; }
    public MotorIA getMotorIA() { return motorIA; }

    public void imprimirResumen() {
        System.out.println("\n──── Resumen de la Plataforma ────");
        System.out.println("  Usuarios registrados : " + usuarios.size());
        System.out.println("  Incidentes totales   : " + incidentes.size());
        System.out.println("  Reportes generados   : " + contadorReportes);
        System.out.println("  Alertas emitidas     : " + moduloMonitoreo.getAlertasGeneradas().size());
        System.out.println("  Total análisis IA    : " + motorIA.getTotalAnalisis());
    }
}
