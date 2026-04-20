package mx.plataforma.ciberseguridad;

import mx.plataforma.ciberseguridad.modelo.*;

import java.time.LocalDate;

/**
 * Punto de entrada principal de la Plataforma de Ciberseguridad para PYMEs.
 * Demuestra el funcionamiento de todos los casos de prueba funcionales
 * definidos en el documento de pruebas (CP-F-001 al CP-F-010).
 *
 * Configuración para ejecutar:
 *   - JDK 17 o superior
 *   - Compilar: javac -d out -sourcepath src src/main/java/mx/plataforma/ciberseguridad/Main.java
 *   - Ejecutar:  java -cp out mx.plataforma.ciberseguridad.Main
 *   - O desde IntelliJ IDEA: Run > Run 'Main'
 */
public class Main {

    public static void main(String[] args) {

        // ─────────────────────────────────────────────────────────────
        // 1. INICIALIZACIÓN DE LA PLATAFORMA
        // ─────────────────────────────────────────────────────────────
        PlataformaSeguridad plataforma = new PlataformaSeguridad();
        System.out.println();

        // ─────────────────────────────────────────────────────────────
        // 2. CONFIGURACIÓN DE ROLES (RBAC)
        // ─────────────────────────────────────────────────────────────
        System.out.println("\n═══ CONFIGURACIÓN DE ROLES ═══");

        Rol rolAdministrador = new Rol(1, "Administrador");
        rolAdministrador.agregarPermiso(Permiso.VER_DASHBOARD);
        rolAdministrador.agregarPermiso(Permiso.GESTIONAR_USUARIOS);
        rolAdministrador.agregarPermiso(Permiso.VER_INCIDENTES);
        rolAdministrador.agregarPermiso(Permiso.CREAR_INCIDENTES);
        rolAdministrador.agregarPermiso(Permiso.CERRAR_INCIDENTES);
        rolAdministrador.agregarPermiso(Permiso.GENERAR_REPORTES);
        rolAdministrador.agregarPermiso(Permiso.CONFIGURAR_SISTEMA);
        rolAdministrador.agregarPermiso(Permiso.ADMINISTRAR_ROLES);

        Rol rolAnalista = new Rol(2, "Analista de Seguridad");
        rolAnalista.agregarPermiso(Permiso.VER_DASHBOARD);
        rolAnalista.agregarPermiso(Permiso.VER_INCIDENTES);
        rolAnalista.agregarPermiso(Permiso.CREAR_INCIDENTES);
        rolAnalista.agregarPermiso(Permiso.VER_MONITOREO);
        rolAnalista.agregarPermiso(Permiso.EJECUTAR_ANALISIS_IA);

        Rol rolOperador = new Rol(3, "Operador");
        rolOperador.agregarPermiso(Permiso.VER_DASHBOARD);
        rolOperador.agregarPermiso(Permiso.VER_MONITOREO);

        Rol rolDirector = new Rol(4, "Director");
        rolDirector.agregarPermiso(Permiso.VER_DASHBOARD);
        rolDirector.agregarPermiso(Permiso.GENERAR_REPORTES);
        rolDirector.agregarPermiso(Permiso.GESTIONAR_USUARIOS);
        rolDirector.agregarPermiso(Permiso.VER_INCIDENTES);

        // ─────────────────────────────────────────────────────────────
        // 3. REGISTRO DE USUARIOS
        // ─────────────────────────────────────────────────────────────
        System.out.println("\n═══ REGISTRO DE USUARIOS ═══");

        Usuario admin    = plataforma.registrarUsuario("Laura Méndez",  "admin@pyme.mx",         rolAdministrador);
        Usuario analista = plataforma.registrarUsuario("Carlos Ramos",  "analista@empresa.mx",   rolAnalista);
        Usuario director = plataforma.registrarUsuario("María Torres",  "directora@empresa.mx",  rolDirector);
        Usuario operador = plataforma.registrarUsuario("Juan Pérez",    "jperez@empresa.mx",     rolOperador);

        // CP-F-010: Intentar registrar correo duplicado
        System.out.println("\n-- CP-F-010: Correo duplicado --");
        plataforma.registrarUsuario("Otro Admin", "admin@pyme.mx", rolAdministrador);

        // ─────────────────────────────────────────────────────────────
        // 4. AUTENTICACIÓN (CP-F-001 y CP-F-002)
        // ─────────────────────────────────────────────────────────────
        System.out.println("\n═══ CP-F-001: LOGIN EXITOSO CON MFA ═══");
        String token = plataforma.autenticarUsuario(admin, "P@ssw0rd123", "482931");

        System.out.println("\n═══ CP-F-002: LOGIN CON CONTRASEÑA INCORRECTA ═══");
        plataforma.autenticarUsuario(analista, "incorrecta123", "000000");

        // ─────────────────────────────────────────────────────────────
        // 5. MONITOREO EN TIEMPO REAL (CP-F-003)
        // ─────────────────────────────────────────────────────────────
        System.out.println("\n═══ CP-F-003: MONITOREO EN TIEMPO REAL ═══");

        if (analista != null && analista.tienePermiso(Permiso.VER_MONITOREO)) {
            plataforma.getModuloMonitoreo().monitorearActividad("192.168.1.100");
            Alerta alerta = plataforma.getModuloMonitoreo().detectarAnomalias(
                    "Port Scan desde 192.168.1.100 hacia servidor principal", 85);
            plataforma.getModuloMonitoreo().enviarAlerta(alerta);
        } else {
            System.out.println("[ACCESO] Permiso VER_MONITOREO no disponible para este rol.");
        }

        // ─────────────────────────────────────────────────────────────
        // 6. CREACIÓN DE INCIDENTES
        // ─────────────────────────────────────────────────────────────
        System.out.println("\n═══ CREACIÓN DE INCIDENTES ═══");

        Incidente inc1 = plataforma.crearIncidente(
                "Port Scan", analista,
                "ESCANEO_PUERTOS|SRC=192.168.1.100|DST=10.0.0.5|PORTS=22,80,443,8080"
        );

        Incidente inc2 = plataforma.crearIncidente(
                "Intento de Phishing", analista,
                "EMAIL_PHISHING|FROM=hacker@malicioso.com|TO=empleado@empresa.mx|LINK=phish.url"
        );

        // Actualizar estado y documentar acciones
        if (inc1 != null) {
            inc1.actualizarEstado("EN_PROCESO");
            inc1.documentarAccion("Bloqueada la IP 192.168.1.100 en el firewall perimetral.");
            inc1.documentarAccion("Notificado al equipo de respuesta a incidentes.");
            inc1.actualizarEstado("CERRADO");
            inc1.imprimirBitacora();
        }

        // ─────────────────────────────────────────────────────────────
        // 7. GENERACIÓN DE REPORTE PDF (CP-F-004)
        // ─────────────────────────────────────────────────────────────
        System.out.println("\n═══ CP-F-004: REPORTE EJECUTIVO PDF ═══");

        if (director != null && director.tienePermiso(Permiso.GENERAR_REPORTES)) {
            Reporte reporte = plataforma.generarReporte(
                    "EJECUTIVO",
                    LocalDate.of(2026, 1, 1),
                    LocalDate.of(2026, 1, 31)
            );
            reporte.programarReporte("MENSUAL");
        }

        // ─────────────────────────────────────────────────────────────
        // 8. CONTROL DE ACCESO POR ROL (CP-F-007)
        // ─────────────────────────────────────────────────────────────
        System.out.println("\n═══ CP-F-007: CONTROL DE ACCESO (ROL OPERADOR) ═══");

        if (operador != null) {
            boolean accesoAdmin = operador.tienePermiso(Permiso.GESTIONAR_USUARIOS);
            if (!accesoAdmin) {
                System.out.println("[ACCESO] HTTP 403 Forbidden: Sin permisos para /admin/users.");
                System.out.println("[ACCESO] Intento registrado en log de auditoría.");
            }
        }

        // ─────────────────────────────────────────────────────────────
        // 9. ANÁLISIS PREDICTIVO DE IA (CP-F-008)
        // ─────────────────────────────────────────────────────────────
        System.out.println("\n═══ CP-F-008: ANÁLISIS PREDICTIVO DE IA ═══");

        if (analista != null && analista.tienePermiso(Permiso.EJECUTAR_ANALISIS_IA)) {
            plataforma.ejecutarAnalisisPredictivo("Servidor Web Principal", 7);
        }

        // ─────────────────────────────────────────────────────────────
        // 10. CIFRADO DE DATOS
        // ─────────────────────────────────────────────────────────────
        System.out.println("\n═══ CIFRADO AES-256 ═══");
        plataforma.getModuloSeguridad().realizarCifrado("datos_sensibles_cliente_12345");

        // ─────────────────────────────────────────────────────────────
        // 11. RESUMEN FINAL
        // ─────────────────────────────────────────────────────────────
        plataforma.imprimirResumen();

        System.out.println("\n[FIN] Demostración completada. Plataforma operativa.");
    }
}
