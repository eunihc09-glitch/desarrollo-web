package mx.plataforma.ciberseguridad.web;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;

public class WebServer {

    private final int port;
    private final DatabaseManager dbManager;
    private HttpServer server;

    public WebServer(int port, DatabaseManager dbManager) {
        this.port = port;
        this.dbManager = dbManager;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
            
            // Contexto API
            server.createContext("/api/", new ApiHandler(dbManager));
            
            // Contexto estático para el Frontend en public/
            server.createContext("/", new StaticFileHandler());
            
            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();
            
            System.out.println("CyberShield AI (Java) disponible en http://localhost:" + port);
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor web: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    /**
     * Manejador de Archivos Estáticos del Frontend
     */
    private static class StaticFileHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }

            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) {
                path = "/index.html";
            }

            File publicDir = new File("public").getCanonicalFile();
            File target = new File(publicDir, path.substring(1)).getCanonicalFile();

            // Protección de Directory Traversal y validación de existencia de archivo
            if (!target.getPath().startsWith(publicDir.getPath()) || !target.isFile()) {
                byte[] errBytes = "404 - Recurso no encontrado".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(404, errBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(errBytes);
                }
                return;
            }

            String mime = getMimeType(target.getName());
            byte[] data = Files.readAllBytes(target.toPath());

            exchange.getResponseHeaders().set("Content-Type", mime);
            exchange.sendResponseHeaders(200, data.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(data);
            }
        }

        private String getMimeType(String filename) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".html")) return "text/html; charset=utf-8";
            if (lower.endsWith(".css")) return "text/css; charset=utf-8";
            if (lower.endsWith(".js")) return "text/javascript; charset=utf-8";
            if (lower.endsWith(".json")) return "application/json; charset=utf-8";
            if (lower.endsWith(".png")) return "image/png";
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
            if (lower.endsWith(".gif")) return "image/gif";
            if (lower.endsWith(".svg")) return "image/svg+xml";
            if (lower.endsWith(".ico")) return "image/x-icon";
            return "application/octet-stream";
        }
    }

    /**
     * Manejador de la API REST
     */
    private static class ApiHandler implements HttpHandler {

        private final DatabaseManager dbManager;

        public ApiHandler(DatabaseManager dbManager) {
            this.dbManager = dbManager;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            // Manejo de CORS Preflight
            if (method.equalsIgnoreCase("OPTIONS")) {
                Headers headers = exchange.getResponseHeaders();
                headers.set("Access-Control-Allow-Origin", "*");
                headers.set("Access-Control-Allow-Headers", "Content-Type, Authorization");
                headers.set("Access-Control-Allow-Methods", "GET, POST, PATCH, OPTIONS");
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            try {
                // Endpoint público: Salud
                if (path.equals("/api/health") && method.equalsIgnoreCase("GET")) {
                    Map<String, String> res = new HashMap<>();
                    res.put("status", "ok");
                    res.put("service", "CyberShield AI Backend (Java)");
                    res.put("database", "cybershield.db");
                    sendJson(exchange, 200, res);
                    return;
                }

                // Endpoint público: Login
                if (path.equals("/api/auth/login") && method.equalsIgnoreCase("POST")) {
                    handleLogin(exchange);
                    return;
                }

                // Endpoint público: Logout (elimina token si se envía)
                if (path.equals("/api/auth/logout") && method.equalsIgnoreCase("POST")) {
                    handleLogout(exchange);
                    return;
                }

                // Los demás endpoints requieren autenticación (Bearer Token)
                Map<String, Object> user = authUser(exchange);
                if (user == null) {
                    return; // authUser ya envía el error 401
                }

                // Endpoint: Dashboard
                if (path.equals("/api/dashboard") && method.equalsIgnoreCase("GET")) {
                    Map<String, Object> res = new HashMap<>();
                    res.put("user", user);
                    res.put("dashboard", dbManager.getDashboardData());
                    sendJson(exchange, 200, res);
                    return;
                }

                // Endpoint: Alertas (Listar/Buscar/Filtrar)
                if (path.equals("/api/alerts") && method.equalsIgnoreCase("GET")) {
                    Map<String, String> query = parseQueryParams(exchange.getRequestURI().getQuery());
                    String severity = query.get("severity");
                    String q = query.get("q");
                    Map<String, Object> res = new HashMap<>();
                    res.put("alerts", dbManager.getAlerts(severity, q));
                    sendJson(exchange, 200, res);
                    return;
                }

                // Endpoint: Obtener Alerta única por ID (GET /api/alerts/{id})
                if (path.startsWith("/api/alerts/") && method.equalsIgnoreCase("GET")) {
                    String[] parts = path.substring(12).split("/");
                    if (parts.length == 1 && parts[0].matches("\\d+")) {
                        int id = Integer.parseInt(parts[0]);
                        Map<String, Object> alert = dbManager.getAlertById(id);
                        if (alert == null) {
                            sendError(exchange, 404, "Alerta no encontrada.");
                            return;
                        }
                        Map<String, Object> res = new HashMap<>();
                        res.put("alert", alert);
                        sendJson(exchange, 200, res);
                        return;
                    }
                }

                // Endpoint: Atender Alerta (PATCH /api/alerts/{id}/attend)
                if (path.startsWith("/api/alerts/") && method.equalsIgnoreCase("PATCH")) {
                    String[] parts = path.substring(12).split("/");
                    if (parts.length == 2 && parts[0].matches("\\d+") && parts[1].equals("attend")) {
                        int id = Integer.parseInt(parts[0]);
                        Map<String, Object> alert = dbManager.attendAlert(id);
                        if (alert == null) {
                            sendError(exchange, 404, "Alerta no encontrada.");
                            return;
                        }
                        Map<String, Object> res = new HashMap<>();
                        res.put("message", "Alerta marcada como atendida.");
                        res.put("alert", alert);
                        sendJson(exchange, 200, res);
                        return;
                    }
                }

                // Endpoint: Escalar Alerta a Incidente (POST /api/alerts/{id}/escalate)
                if (path.startsWith("/api/alerts/") && method.equalsIgnoreCase("POST")) {
                    String[] parts = path.substring(12).split("/");
                    if (parts.length == 2 && parts[0].matches("\\d+") && parts[1].equals("escalate")) {
                        int id = Integer.parseInt(parts[0]);
                        String userName = (String) user.get("full_name");
                        Map<String, Object> incident = dbManager.escalateAlertToIncident(id, userName);
                        if (incident == null) {
                            sendError(exchange, 404, "Alerta no encontrada.");
                            return;
                        }
                        Map<String, Object> res = new HashMap<>();
                        res.put("message", "Alerta escalada a incidente correctamente.");
                        res.put("incident", incident);
                        sendJson(exchange, 201, res);
                        return;
                    }
                }

                // Endpoint: Incidentes (Listar GET y Registrar POST)
                if (path.equals("/api/incidents")) {
                    if (method.equalsIgnoreCase("GET")) {
                        Map<String, Object> res = new HashMap<>();
                        res.put("incidents", dbManager.getIncidents());
                        sendJson(exchange, 200, res);
                        return;
                    } else if (method.equalsIgnoreCase("POST")) {
                        handleCreateIncident(exchange);
                        return;
                    }
                }

                // Endpoint: Reportes (Listar GET y Generar POST)
                if (path.equals("/api/reports")) {
                    if (method.equalsIgnoreCase("GET")) {
                        Map<String, Object> res = new HashMap<>();
                        res.put("reports", dbManager.getReports());
                        sendJson(exchange, 200, res);
                        return;
                    } else if (method.equalsIgnoreCase("POST")) {
                        handleCreateReport(exchange);
                        return;
                    }
                }

                // Endpoint: Validaciones de módulos (POST)
                if (path.equals("/api/validations") && method.equalsIgnoreCase("POST")) {
                    handleCreateValidation(exchange);
                    return;
                }

                sendError(exchange, 404, "Recurso no encontrado.");
            } catch (IllegalArgumentException e) {
                sendError(exchange, 400, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                sendError(exchange, 500, "Error interno del servidor: " + e.getMessage());
            }
        }

        /**
         * Lógica de subprocesamiento y respuestas HTTP
         */
        private void sendJson(HttpExchange exchange, int status, Object payload) throws IOException {
            String body = new Gson().toJson(payload);
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            Headers headers = exchange.getResponseHeaders();
            headers.set("Content-Type", "application/json; charset=utf-8");
            headers.set("Access-Control-Allow-Origin", "*");
            headers.set("Access-Control-Allow-Headers", "Content-Type, Authorization");
            headers.set("Access-Control-Allow-Methods", "GET, POST, PATCH, OPTIONS");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }

        private void sendError(HttpExchange exchange, int status, String message) throws IOException {
            Map<String, String> error = new HashMap<>();
            error.put("error", message);
            sendJson(exchange, status, error);
        }

        private Map<String, Object> readJson(HttpExchange exchange) throws IOException {
            try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, Object>>(){}.getType();
                Map<String, Object> map = new Gson().fromJson(reader, type);
                if (map == null) return new HashMap<>();
                return map;
            } catch (Exception e) {
                throw new IllegalArgumentException("El cuerpo de la petición no es JSON válido.");
            }
        }

        private String requireText(Map<String, Object> data, String key, int minLen) {
            Object val = data.get(key);
            String str = val == null ? "" : val.toString().trim();
            if (str.length() < minLen) {
                throw new IllegalArgumentException("El campo " + key + " es obligatorio.");
            }
            return str;
        }

        private String requireChoice(Map<String, Object> data, String key, String... choices) {
            String val = requireText(data, key, 1);
            for (String choice : choices) {
                if (choice.equals(val)) return val;
            }
            throw new IllegalArgumentException("El campo " + key + " no tiene un valor permitido.");
        }

        private Map<String, Object> authUser(HttpExchange exchange) throws IOException {
            String auth = exchange.getRequestHeaders().getFirst("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                sendError(exchange, 401, "Sesión no autorizada o expirada.");
                return null;
            }
            String token = auth.substring(7).trim();
            if (token.isEmpty()) {
                sendError(exchange, 401, "Sesión no autorizada o expirada.");
                return null;
            }
            Map<String, Object> user = dbManager.getUserFromToken(token);
            if (user == null) {
                sendError(exchange, 401, "Sesión no autorizada o expirada.");
                return null;
            }
            return user;
        }

        private Map<String, String> parseQueryParams(String query) {
            Map<String, String> params = new HashMap<>();
            if (query == null || query.isEmpty()) return params;
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                try {
                    String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8.name());
                    String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name()) : "";
                    params.put(key, value);
                } catch (UnsupportedEncodingException e) {
                    // Ignorar error de encoding estándar UTF-8
                }
            }
            return params;
        }

        /**
         * Manejadores específicos de Endpoint
         */
        private void handleLogin(HttpExchange exchange) throws IOException {
            Map<String, Object> data = readJson(exchange);
            String email = requireText(data, "email", 1).toLowerCase();
            String password = requireText(data, "password", 1);

            Map<String, Object> user = dbManager.verifyCredentials(email, password);
            if (user == null) {
                sendError(exchange, 401, "Correo o contraseña incorrectos.");
                return;
            }

            // Generar un token aleatorio simple y seguro
            String token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
            
            // Expiración de 8 horas en formato ISO 8601 UTC
            String expiresAt = DateTimeFormatter.ISO_INSTANT
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now().plus(8, ChronoUnit.HOURS).truncatedTo(ChronoUnit.SECONDS));

            dbManager.createSession((Integer) user.get("id"), token, expiresAt);

            Map<String, Object> res = new HashMap<>();
            res.put("token", token);

            Map<String, Object> userPayload = new LinkedHashMap<>();
            userPayload.put("id", user.get("id"));
            userPayload.put("full_name", user.get("full_name"));
            userPayload.put("email", user.get("email"));
            userPayload.put("role", user.get("role"));
            res.put("user", userPayload);

            sendJson(exchange, 200, res);
        }

        private void handleLogout(HttpExchange exchange) throws IOException {
            String auth = exchange.getRequestHeaders().getFirst("Authorization");
            String token = "";
            if (auth != null && auth.startsWith("Bearer ")) {
                token = auth.substring(7).trim();
            }
            if (!token.isEmpty()) {
                dbManager.deleteSessionByToken(token);
            }
            Map<String, String> res = new HashMap<>();
            res.put("message", "Sesión cerrada correctamente.");
            sendJson(exchange, 200, res);
        }

        private void handleCreateIncident(HttpExchange exchange) throws IOException {
            Map<String, Object> data = readJson(exchange);
            String type = requireText(data, "type", 1);
            String incidentDate = requireText(data, "incident_date", 1);
            String severity = requireChoice(data, "severity", "alta", "media", "baja");
            String description = requireText(data, "description", 10);
            String responsible = requireText(data, "responsible", 1);

            Integer alertId = null;
            if (data.containsKey("alert_id") && data.get("alert_id") != null) {
                try {
                    alertId = ((Double) data.get("alert_id")).intValue();
                } catch (Exception e) {
                    // Ignorar o parsear si es string
                }
            }

            Map<String, Object> incident = dbManager.createIncident(type, incidentDate, severity, description, responsible, alertId);
            Map<String, Object> res = new HashMap<>();
            res.put("message", "Incidente registrado correctamente.");
            res.put("incident", incident);
            sendJson(exchange, 201, res);
        }

        private void handleCreateReport(HttpExchange exchange) throws IOException {
            Map<String, Object> data = readJson(exchange);
            String reportType = requireText(data, "report_type", 1);
            String dateRange = requireText(data, "date_range", 1);

            String formattedTime = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
                    .withZone(ZoneOffset.systemDefault())
                    .format(Instant.now());
            String name = ("Reporte_" + reportType + "_" + formattedTime + ".pdf").replace(" ", "_");

            Map<String, Object> report = dbManager.createReport(reportType, dateRange, name);
            Map<String, Object> res = new HashMap<>();
            res.put("message", "Reporte generado y guardado correctamente.");
            res.put("report", report);
            sendJson(exchange, 201, res);
        }

        private void handleCreateValidation(HttpExchange exchange) throws IOException {
            Map<String, Object> data = readJson(exchange);
            String module = requireText(data, "module", 1);
            String result = "Validación realizada correctamente para el módulo " + module + ".";

            Map<String, Object> validation = dbManager.createValidation(module, result);
            Map<String, Object> res = new HashMap<>();
            res.put("message", result);
            res.put("validation", validation);
            sendJson(exchange, 201, res);
        }
    }
}
