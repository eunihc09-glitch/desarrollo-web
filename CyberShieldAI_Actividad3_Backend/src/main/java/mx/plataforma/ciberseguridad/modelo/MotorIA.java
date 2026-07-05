package mx.plataforma.ciberseguridad.modelo;

/**
 * Motor de Inteligencia Artificial encargado de analizar patrones
 * de comportamiento en los eventos de red y endpoints para detectar
 * anomalías y predecir niveles de riesgo.
 *
 * Relación con el diagrama de clases:
 *   - MOTORIA es utilizado por PLATAFORMA_SEGURIDAD y MODULO_SEGURIDAD
 *   - Genera puntuaciones de riesgo 0-100 para clasificar incidentes
 */
public class MotorIA {

    private String apiKey;
    private String version;
    private double umbralRiesgo;     // Si la puntuación supera este valor → genera incidente
    private int totalAnalisis;

    public MotorIA(String apiKey, String version, double umbralRiesgo) {
        this.apiKey = apiKey;
        this.version = version;
        this.umbralRiesgo = umbralRiesgo;
        this.totalAnalisis = 0;
    }

    /**
     * Analiza patrones en el stream de eventos recibidos desde Kafka
     * y extrae vectores de características para aplicar el modelo ML.
     * @param eventoRaw Evento en formato normalizado (CEF).
     * @return Puntuación de riesgo entre 0 y 100.
     */
    public int analizarPatrones(String eventoRaw) {
        totalAnalisis++;
        System.out.println("[MOTOR IA] Analizando evento: " + eventoRaw);
        System.out.println("[MOTOR IA] Extrayendo vectores de características...");

        // Simulación del modelo de detección de anomalías
        int puntuacion = simularModelo(eventoRaw);
        System.out.println("[MOTOR IA] Puntuación de riesgo: " + puntuacion + "/100");

        if (puntuacion >= umbralRiesgo) {
            System.out.println("[MOTOR IA] ⚠ Umbral superado (" + umbralRiesgo + "). Emitiendo incidente...");
        } else {
            System.out.println("[MOTOR IA] Evento dentro de parámetros normales. Registrado.");
        }
        return puntuacion;
    }

    /**
     * Predice el nivel de riesgo de un activo específico
     * para un horizonte de tiempo determinado.
     * @param asset    Nombre del activo a analizar (ej: "Servidor Web Principal").
     * @param diasHorizonte Número de días hacia el futuro.
     * @return Nivel de riesgo textual: BAJO, MEDIO, ALTO, CRITICO.
     */
    public String predecirRiesgo(String asset, int diasHorizonte) {
        System.out.println("[MOTOR IA] Predicción para: " + asset + " | Horizonte: " + diasHorizonte + " días");
        System.out.println("[MOTOR IA] Consultando datos históricos de los últimos 30 días...");

        // Simulación de predicción
        String[] niveles = {"BAJO", "MEDIO", "ALTO", "CRITICO"};
        int indice = (asset.length() + diasHorizonte) % 4;
        String nivelPrediccion = niveles[indice];
        int confianza = 70 + (diasHorizonte % 25);

        System.out.println("[MOTOR IA] Nivel predicho   : " + nivelPrediccion);
        System.out.println("[MOTOR IA] Confianza        : " + confianza + "%");
        System.out.println("[MOTOR IA] Recomendaciones  :");
        System.out.println("           1. Aplicar parches de seguridad pendientes.");
        System.out.println("           2. Revisar configuraciones de firewall.");
        System.out.println("           3. Incrementar frecuencia de monitoreo.");

        return nivelPrediccion;
    }

    /**
     * Dispara el reentrenamiento incremental del modelo con los datos
     * validados de las últimas 24 horas.
     */
    public void reentrenarModelo() {
        System.out.println("[MOTOR IA] Iniciando reentrenamiento incremental del modelo...");
        System.out.println("[MOTOR IA] Procesando datos de las últimas 24 horas...");
        System.out.println("[MOTOR IA] Modelo actualizado correctamente. Versión: " + version + "-updated");
    }

    /** Simulación del modelo ML (en producción: TensorFlow/Scikit-learn via API). */
    private int simularModelo(String evento) {
        return (evento.hashCode() % 100 + 100) % 100;
    }

    // ──────────── Getters y Setters ────────────

    public String getApiKey() { return apiKey; }
    public String getVersion() { return version; }
    public double getUmbralRiesgo() { return umbralRiesgo; }
    public void setUmbralRiesgo(double umbralRiesgo) { this.umbralRiesgo = umbralRiesgo; }
    public int getTotalAnalisis() { return totalAnalisis; }

    @Override
    public String toString() {
        return "MotorIA{version='" + version + "', umbral=" + umbralRiesgo
                + ", totalAnalisis=" + totalAnalisis + "}";
    }
}
