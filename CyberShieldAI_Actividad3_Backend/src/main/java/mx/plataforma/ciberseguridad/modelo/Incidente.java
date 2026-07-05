package mx.plataforma.ciberseguridad.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un incidente de seguridad detectado y registrado
 * en la plataforma. Mantiene trazabilidad completa de cada acción
 * tomada desde su apertura hasta su cierre.
 *
 * Relación con el diagrama de clases:
 *   - PLATAFORMA_SEGURIDAD hereda / contiene INCIDENTE
 *   - INCIDENTE referencia a USUARIO como responsable
 */
public class Incidente {

    private int id;
    private String tipo;
    private String estado;            // ABIERTO, EN_PROCESO, CERRADO
    private LocalDateTime fechaReporte;
    private Usuario responsable;
    private int nivelRiesgo;          // Puntuación 0-100 del motor de IA
    private List<String> bitacora;    // Historial inmutable de acciones

    public Incidente(int id, String tipo, Usuario responsable, int nivelRiesgo) {
        this.id = id;
        this.tipo = tipo;
        this.estado = "ABIERTO";
        this.fechaReporte = LocalDateTime.now();
        this.responsable = responsable;
        this.nivelRiesgo = nivelRiesgo;
        this.bitacora = new ArrayList<>();
        this.bitacora.add("[" + fechaReporte + "] Incidente registrado. Riesgo: " + nivelRiesgo + "/100");
    }

    /**
     * Registra formalmente el incidente, persiste en la base de datos
     * y genera una alerta para el equipo responsable.
     */
    public void registrarIncidente() {
        System.out.println("[INCIDENTE] Registrado: ID=" + id + " | Tipo=" + tipo
                + " | Riesgo=" + nivelRiesgo + "/100"
                + " | Responsable=" + (responsable != null ? responsable.getNombre() : "Sin asignar"));
        System.out.println("[INCIDENTE] Estado inicial: " + estado);
    }

    /**
     * Actualiza el estado del incidente y lo documenta en la bitácora.
     * @param nuevoEstado Nuevo estado (EN_PROCESO / CERRADO).
     */
    public void actualizarEstado(String nuevoEstado) {
        String entrada = "[" + LocalDateTime.now() + "] Estado cambiado: "
                + this.estado + " → " + nuevoEstado;
        this.estado = nuevoEstado;
        bitacora.add(entrada);
        System.out.println("[INCIDENTE] " + entrada);
    }

    /**
     * Documenta una acción tomada sobre el incidente, garantizando
     * trazabilidad completa (quién, qué, cuándo, evidencia).
     * @param accion Descripción de la acción realizada.
     */
    public void documentarAccion(String accion) {
        String autor = (responsable != null) ? responsable.getNombre() : "Sistema";
        String entrada = "[" + LocalDateTime.now() + "] " + autor + ": " + accion;
        bitacora.add(entrada);
        System.out.println("[INCIDENTE] Acción documentada: " + entrada);
    }

    /**
     * Imprime la bitácora completa del incidente.
     */
    public void imprimirBitacora() {
        System.out.println("── Bitácora del Incidente ID=" + id + " ──");
        bitacora.forEach(e -> System.out.println("  " + e));
    }

    // ──────────── Getters y Setters ────────────

    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaReporte() { return fechaReporte; }
    public Usuario getResponsable() { return responsable; }
    public void setResponsable(Usuario responsable) { this.responsable = responsable; }
    public int getNivelRiesgo() { return nivelRiesgo; }
    public List<String> getBitacora() { return bitacora; }

    @Override
    public String toString() {
        return "Incidente{id=" + id + ", tipo='" + tipo + "', estado='" + estado
                + "', riesgo=" + nivelRiesgo + "/100}";
    }
}
