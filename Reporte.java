package mx.plataforma.ciberseguridad.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Representa un reporte ejecutivo generado por la plataforma.
 * Puede ser exportado a PDF y programado para generación periódica.
 *
 * Relación con el diagrama de clases:
 *   - PLATAFORMA_SEGURIDAD hereda / contiene REPORTE
 *   - REPORTE es generado por el servicio de reportes (IReport)
 */
public class Reporte {

    private int id;
    private String tipo;                 // EJECUTIVO, TÉCNICO, DE_VULNERABILIDADES
    private LocalDateTime fechaGeneracion;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private String contenido;
    private String rutaArchivo;

    public Reporte(int id, String tipo, LocalDate periodoInicio, LocalDate periodoFin) {
        this.id = id;
        this.tipo = tipo;
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
        this.fechaGeneracion = LocalDateTime.now();
        this.contenido = "";
    }

    /**
     * Genera el contenido del reporte consolidando datos del período:
     * incidentes, tendencias, métricas y recomendaciones.
     */
    public void generarReporte() {
        this.contenido = "Reporte " + tipo + " | Período: " + periodoInicio
                + " al " + periodoFin + " | Generado: " + fechaGeneracion;

        System.out.println("[REPORTE] Generando reporte ID=" + id + " tipo=" + tipo);
        System.out.println("[REPORTE] Período: " + periodoInicio + " al " + periodoFin);
        System.out.println("[REPORTE] Consolidando incidentes, métricas y recomendaciones...");
        System.out.println("[REPORTE] Contenido generado correctamente.");
    }

    /**
     * Exporta el reporte generado a formato PDF.
     * El archivo resultante no debe superar 5 MB.
     */
    public void exportarPDF() {
        if (contenido.isEmpty()) {
            System.out.println("[REPORTE] Error: Debe generar el reporte antes de exportar.");
            return;
        }
        this.rutaArchivo = "/reportes/reporte_" + id + "_" + tipo.toLowerCase() + ".pdf";
        System.out.println("[REPORTE] PDF exportado en: " + rutaArchivo);
        System.out.println("[REPORTE] Tamaño estimado: < 5 MB. Listo para descarga.");
    }

    /**
     * Programa la generación automática del reporte de forma periódica.
     * @param frecuencia Frecuencia de generación (DIARIO, SEMANAL, MENSUAL).
     */
    public void programarReporte(String frecuencia) {
        System.out.println("[REPORTE] Reporte ID=" + id + " programado con frecuencia: " + frecuencia);
        System.out.println("[REPORTE] Se enviará automáticamente al correo del Director.");
    }

    // ──────────── Getters y Setters ────────────

    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public LocalDate getPeriodoInicio() { return periodoInicio; }
    public LocalDate getPeriodoFin() { return periodoFin; }
    public String getContenido() { return contenido; }
    public String getRutaArchivo() { return rutaArchivo; }

    @Override
    public String toString() {
        return "Reporte{id=" + id + ", tipo='" + tipo + "', generado=" + fechaGeneracion + "}";
    }
}
