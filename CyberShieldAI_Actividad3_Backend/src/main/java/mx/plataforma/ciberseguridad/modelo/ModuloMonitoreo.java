package mx.plataforma.ciberseguridad.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Módulo de monitoreo continuo de red y endpoints.
 * Captura logs, normaliza eventos y los publica en Kafka
 * para su análisis por el Motor de IA.
 *
 * Relación con el diagrama de clases:
 *   - MODULO_MONITOREO está contenido en PLATAFORMA_SEGURIDAD
 *   - Genera ALERTA cuando detecta comportamiento sospechoso
 *   - Tiene relación con ROL (solo usuarios con permisos adecuados pueden verlo)
 */
public class ModuloMonitoreo {

    private int id;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private List<String> logEventos;
    private List<Alerta> alertasGeneradas;

    public ModuloMonitoreo(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = true;
        this.logEventos = new ArrayList<>();
        this.alertasGeneradas = new ArrayList<>();
    }

    /**
     * Monitorea la actividad de red y endpoints de forma continua.
     * Captura eventos CEF, los normaliza y publica en Kafka raw-events.
     * @param endpoint Nombre o IP del endpoint a monitorear.
     */
    public void monitorearActividad(String endpoint) {
        if (!activo) {
            System.out.println("[MONITOREO] El módulo está inactivo. Active el agente primero.");
            return;
        }
        String log = "Capturando tráfico en: " + endpoint + " | Canal: TLS 1.3 cifrado";
        logEventos.add(log);
        System.out.println("[MONITOREO] " + log);
        System.out.println("[MONITOREO] Pipeline: captura → normalización → desduplicación → Kafka raw-events");
    }

    /**
     * Detecta anomalías en el stream de eventos.
     * Si la puntuación de riesgo supera el umbral configurado, genera una alerta.
     * @param evento    Descripción del evento detectado.
     * @param puntuacion Puntuación de riesgo 0-100 del Motor IA.
     * @return Alerta generada, o null si el evento es normal.
     */
    public Alerta detectarAnomalias(String evento, int puntuacion) {
        System.out.println("[MONITOREO] Evaluando evento: " + evento + " | Riesgo: " + puntuacion + "/100");

        if (puntuacion >= 70) {
            String severidad = puntuacion >= 90 ? "CRITICA" : puntuacion >= 80 ? "ALTA" : "MEDIA";
            Alerta alerta = new Alerta(
                    alertasGeneradas.size() + 1,
                    "Anomalía detectada",
                    evento,
                    severidad
            );
            alertasGeneradas.add(alerta);
            System.out.println("[MONITOREO] Anomalía registrada. Severidad: " + severidad);
            return alerta;
        } else {
            System.out.println("[MONITOREO] Evento dentro de parámetros normales.");
            return null;
        }
    }

    /**
     * Envía una alerta existente al Dashboard web vía WebSocket
     * y notifica al equipo responsable.
     * @param alerta Alerta a enviar.
     */
    public void enviarAlerta(Alerta alerta) {
        if (alerta != null) {
            alerta.enviarAlerta();
        } else {
            System.out.println("[MONITOREO] No hay alerta que enviar.");
        }
    }

    // ──────────── Getters y Setters ────────────

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public List<String> getLogEventos() { return logEventos; }
    public List<Alerta> getAlertasGeneradas() { return alertasGeneradas; }

    @Override
    public String toString() {
        return "ModuloMonitoreo{id=" + id + ", nombre='" + nombre
                + "', activo=" + activo + ", alertas=" + alertasGeneradas.size() + "}";
    }
}
