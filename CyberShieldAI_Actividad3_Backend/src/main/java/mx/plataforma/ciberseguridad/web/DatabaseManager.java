package mx.plataforma.ciberseguridad.web;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;
import java.security.SecureRandom;

public class DatabaseManager {

    private static final String DATABASE_URL = "jdbc:sqlite:database/cybershield.db";
    private static final String SCHEMA_PATH = "database/schema.sql";
    private static final String SEED_PATH = "database/seed.sql";
    private static final String DEMO_EMAIL = "demo@cybershield.ai";
    private static final String DEMO_PASSWORD = "demo1234";

    public DatabaseManager() {
        // Asegurar que la carpeta database existe
        File dbDir = new File("database");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }
    }

    private Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(DATABASE_URL);
        // Habilitar llaves foráneas en SQLite
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    /**
     * Inicializa la base de datos si no existe o si las tablas están ausentes.
     */
    public void initDatabase() {
        try (Connection conn = connect()) {
            File schemaFile = new File(SCHEMA_PATH);
            File seedFile = new File(SEED_PATH);

            if (schemaFile.exists()) {
                String schemaSql = Files.readString(schemaFile.toPath(), StandardCharsets.UTF_8);
                executeScript(conn, schemaSql);
            }

            // Verificar si el usuario demo existe
            boolean demoExists = false;
            try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM users WHERE email = ?")) {
                ps.setString(1, DEMO_EMAIL);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        demoExists = true;
                    }
                }
            }

            if (!demoExists) {
                // Generar salt y hash con PBKDF2 para que sea idéntico a Python
                String salt = generateRandomSalt();
                String hash = hashPasswordPbkdf2(DEMO_PASSWORD, salt);
                
                String insertUser = "INSERT INTO users (full_name, email, password_salt, password_hash, role) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
                    ps.setString(1, "Carlos Martínez");
                    ps.setString(2, DEMO_EMAIL);
                    ps.setString(3, salt);
                    ps.setString(4, hash);
                    ps.setString(5, "Analista de seguridad");
                    ps.executeUpdate();
                }
            }

            if (seedFile.exists()) {
                String seedSql = Files.readString(seedFile.toPath(), StandardCharsets.UTF_8);
                executeScript(conn, seedSql);
            }

            System.out.println("Base de datos inicializada correctamente.");
        } catch (Exception e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void executeScript(Connection conn, String script) throws SQLException {
        // Separar comandos por punto y coma (simple split, ignorando si hay dentro de strings, pero funciona para nuestros sql)
        String[] commands = script.split(";");
        try (Statement stmt = conn.createStatement()) {
            for (String cmd : commands) {
                String trimmed = cmd.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }

    /**
     * Auxiliares de Hashing PBKDF2 (compatibles con Python hashlib.pbkdf2_hmac)
     */
    public String hashPasswordPbkdf2(String password, String salt) {
        try {
            int iterations = 120000;
            int keyLength = 256; // 32 bytes * 8 bits
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error en PBKDF2: " + e.getMessage(), e);
        }
    }

    private String generateRandomSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] saltBytes = new byte[16];
        sr.nextBytes(saltBytes);
        StringBuilder hexString = new StringBuilder();
        for (byte b : saltBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Mapeador genérico de ResultSet a Map
     */
    private Map<String, Object> rowToMap(ResultSet rs) throws SQLException {
        Map<String, Object> map = new LinkedHashMap<>();
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();
        for (int i = 1; i <= colCount; i++) {
            String colName = meta.getColumnLabel(i);
            Object value = rs.getObject(i);
            map.put(colName, value);
        }
        return map;
    }

    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        while (rs.next()) {
            list.add(rowToMap(rs));
        }
        return list;
    }

    /**
     * Métodos de autenticación y sesiones
     */
    public Map<String, Object> verifyCredentials(String email, String password) {
        String sql = "SELECT * FROM users WHERE LOWER(email) = LOWER(?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> user = rowToMap(rs);
                    String salt = (String) user.get("password_salt");
                    String dbHash = (String) user.get("password_hash");
                    
                    String inputHash = hashPasswordPbkdf2(password, salt);
                    if (inputHash.equals(dbHash)) {
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createSession(int userId, String token, String expiresAt) {
        String sql = "INSERT INTO sessions (user_id, token, expires_at) VALUES (?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, token);
            ps.setString(3, expiresAt);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getUserFromToken(String token) {
        String sql = "SELECT sessions.id AS session_id, sessions.expires_at, users.id AS user_id, " +
                     "users.full_name, users.email, users.role " +
                     "FROM sessions JOIN users ON users.id = sessions.user_id " +
                     "WHERE sessions.token = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> session = rowToMap(rs);
                    String expiresAtStr = (String) session.get("expires_at");
                    
                    // Comprobar expiración
                    Instant expiresAt;
                    if (expiresAtStr.contains("Z") || expiresAtStr.contains("+") || (expiresAtStr.indexOf('-', 11) != -1)) {
                        expiresAt = Instant.parse(expiresAtStr);
                    } else {
                        expiresAt = Instant.parse(expiresAtStr + "Z");
                    }
                    
                    Instant now = Instant.now();
                    if (expiresAt.isBefore(now)) {
                        // Eliminar sesión expirada
                        deleteSessionById((Integer) session.get("session_id"));
                        return null;
                    }
                    
                    Map<String, Object> user = new LinkedHashMap<>();
                    user.put("id", session.get("user_id"));
                    user.put("full_name", session.get("full_name"));
                    user.put("email", session.get("email"));
                    user.put("role", session.get("role"));
                    return user;
                }
            }
        } catch (Exception e) {
            // Si hay problemas al parsear la fecha, retornamos null
            e.printStackTrace();
        }
        return null;
    }

    public void deleteSessionByToken(String token) {
        String sql = "DELETE FROM sessions WHERE token = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteSessionById(int sessionId) {
        String sql = "DELETE FROM sessions WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dashboard métricas
     */
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new LinkedHashMap<>();
        try (Connection conn = connect()) {
            // Métricas base
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM metrics WHERE id = 1")) {
                if (rs.next()) {
                    data.put("security_level", rs.getInt("security_level"));
                    data.put("monitored_devices", rs.getInt("monitored_devices"));
                    data.put("blocked_threats", rs.getInt("blocked_threats"));
                    data.put("response_time", rs.getString("response_time"));
                }
            }
            
            // Alertas activas
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM alerts WHERE status = 'Pendiente'")) {
                if (rs.next()) {
                    data.put("active_alerts", rs.getInt("total"));
                }
            }
            
            // Incidentes abiertos
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM incidents WHERE status != 'Resuelto'")) {
                if (rs.next()) {
                    data.put("open_incidents", rs.getInt("total"));
                }
            }
            
            // Alertas recientes (5)
            String sqlRecent = "SELECT id, severity, description, origin, status, created_at FROM alerts ORDER BY created_at DESC LIMIT 5";
            try (PreparedStatement ps = conn.prepareStatement(sqlRecent);
                 ResultSet rs = ps.executeQuery()) {
                data.put("recent_alerts", resultSetToList(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Listado y operaciones de Alertas
     */
    public List<Map<String, Object>> getAlerts(String severity, String textQuery) {
        StringBuilder sql = new StringBuilder("SELECT * FROM alerts WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        
        if (severity != null && (severity.equals("alta") || severity.equals("media") || severity.equals("baja"))) {
            sql.append(" AND severity = ?");
            params.add(severity);
        }
        
        if (textQuery != null && !textQuery.trim().isEmpty()) {
            sql.append(" AND (LOWER(description) LIKE ? OR LOWER(origin) LIKE ? OR LOWER(recommendation) LIKE ?)");
            String likeVal = "%" + textQuery.trim().toLowerCase() + "%";
            params.add(likeVal);
            params.add(likeVal);
            params.add(likeVal);
        }
        
        sql.append(" ORDER BY CASE severity WHEN 'alta' THEN 1 WHEN 'media' THEN 2 ELSE 3 END, created_at DESC");
        
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                return resultSetToList(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Map<String, Object> getAlertById(int id) {
        String sql = "SELECT * FROM alerts WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rowToMap(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> attendAlert(int id) {
        String updateSql = "UPDATE alerts SET status = 'Atendida' WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return getAlertById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> escalateAlertToIncident(int alertId, String userName) {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);
            try {
                // Obtener alerta
                Map<String, Object> alert = null;
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM alerts WHERE id = ?")) {
                    ps.setInt(1, alertId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            alert = rowToMap(rs);
                        }
                    }
                }
                
                if (alert == null) {
                    conn.rollback();
                    return null;
                }
                
                // Insertar incidente
                String insertSql = "INSERT INTO incidents (type, incident_date, severity, description, responsible, status, alert_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
                int incidentId = -1;
                
                String todayDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        .withZone(ZoneOffset.UTC)
                        .format(Instant.now());
                
                try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, "Escalamiento de alerta");
                    ps.setString(2, todayDate);
                    ps.setString(3, (String) alert.get("severity"));
                    ps.setString(4, (String) alert.get("description"));
                    ps.setString(5, userName);
                    ps.setString(6, "Abierto");
                    ps.setInt(7, alertId);
                    ps.executeUpdate();
                    
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            incidentId = keys.getInt(1);
                        }
                    }
                }
                
                // Actualizar alerta
                try (PreparedStatement ps = conn.prepareStatement("UPDATE alerts SET status = 'Atendida' WHERE id = ?")) {
                    ps.setInt(1, alertId);
                    ps.executeUpdate();
                }
                
                conn.commit();
                
                // Retornar el incidente creado
                if (incidentId != -1) {
                    try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM incidents WHERE id = ?")) {
                        ps.setInt(1, incidentId);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                return rowToMap(rs);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Operaciones de Incidentes
     */
    public List<Map<String, Object>> getIncidents() {
        String sql = "SELECT * FROM incidents ORDER BY id DESC";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return resultSetToList(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Map<String, Object> createIncident(String type, String date, String severity, String description, String responsible, Integer alertId) {
        String sql = "INSERT INTO incidents (type, incident_date, severity, description, responsible, status, alert_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, type);
            ps.setString(2, date);
            ps.setString(3, severity);
            ps.setString(4, description);
            ps.setString(5, responsible);
            ps.setString(6, "Abierto");
            if (alertId != null) {
                ps.setInt(7, alertId);
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.executeUpdate();
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    try (PreparedStatement psSelect = conn.prepareStatement("SELECT * FROM incidents WHERE id = ?")) {
                        psSelect.setInt(1, id);
                        try (ResultSet rs = psSelect.executeQuery()) {
                            if (rs.next()) {
                                return rowToMap(rs);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Operaciones de Reportes
     */
    public List<Map<String, Object>> getReports() {
        String sql = "SELECT * FROM reports ORDER BY created_at DESC, id DESC";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return resultSetToList(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Map<String, Object> createReport(String reportType, String dateRange, String fileName) {
        String sql = "INSERT INTO reports (name, report_type, date_range) VALUES (?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fileName);
            ps.setString(2, reportType);
            ps.setString(3, dateRange);
            ps.executeUpdate();
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    try (PreparedStatement psSelect = conn.prepareStatement("SELECT * FROM reports WHERE id = ?")) {
                        psSelect.setInt(1, id);
                        try (ResultSet rs = psSelect.executeQuery()) {
                            if (rs.next()) {
                                return rowToMap(rs);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Operaciones de Validaciones
     */
    public Map<String, Object> createValidation(String module, String result) {
        String sql = "INSERT INTO validations (module, result) VALUES (?, ?)";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, module);
            ps.setString(2, result);
            ps.executeUpdate();
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    try (PreparedStatement psSelect = conn.prepareStatement("SELECT * FROM validations WHERE id = ?")) {
                        psSelect.setInt(1, id);
                        try (ResultSet rs = psSelect.executeQuery()) {
                            if (rs.next()) {
                                return rowToMap(rs);
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
