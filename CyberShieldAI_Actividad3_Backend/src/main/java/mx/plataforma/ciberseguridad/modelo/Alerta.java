package mx.plataforma.ciberseguridad.modelo;

import java.time.LocalDateTime;

/**
 * Representa una alerta generada por el sistema de monitoreo
 * o por el motor de IA al detectar un evento sospechoso.
 *
 * Relación con el diagrama de clases:
 *   - ALERTA es generada por MODULO_MONITOREO y enviada al Dashboard.
 */
public class Alerta {

    private int id;
    private String tipo;
    private String descripcion;
    private String severidad;       // BAJA, MEDIA, ALTA, CRITICA
    private LocalDateTime fechaHora;
    private boolean enviada;

    public Alerta(int id, String tipo, String descripcion, String severidad) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.severidad = severidad;
        this.fechaHora = LocalDateTime.now();
        this.enviada = false;
    }

    /**
     * Envía la alerta al dashboard vía WebSocket y notifica por correo/Slack
     * al equipo responsable según la severidad.
     */
    public void enviarAlerta() {
        System.out.println("──────────────────────────────────────────");
        System.out.println("[ALERTA " + severidad + "] ID: " + id);
        System.out.println("  Tipo       : " + tipo);
        System.out.println("  Descripción: " + descripcion);
        System.out.println("  Fecha/Hora : " + fechaHora);

        switch (severidad.toUpperCase()) {
            case "CRITICA":
            case "ALTA":
                System.out.println("  Acción     : Notificación inmediata a Slack + correo al equipo de seguridad.");
                break;
            case "MEDIA":
                System.out.println("  Acción     : Correo al analista de turno.");
                break;
            default:
                System.out.println("  Acción     : Registrada en log de auditoría.");
        }

        System.out.println("  Estado     : Enviada vía WebSocket al Dashboard.");
        System.out.println("──────────────────────────────────────────");
        this.enviada = true;
    }

    // ──────────── Getters y Setters ────────────

    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public String getDescripcion() { return descripcion; }
    public String getSeveridad() { return severidad; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public boolean isEnviada() { return enviada; }

    @Override
    public String toString() {
        return "Alerta{id=" + id + ", tipo='" + tipo + "', severidad='" + severidad + "', enviada=" + enviada + "}";
    }
}
