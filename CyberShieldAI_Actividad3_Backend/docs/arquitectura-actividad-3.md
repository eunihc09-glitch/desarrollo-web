# Diseño técnico de la Actividad 3

## 1. Comprensión del problema

La actividad solicita retomar el desarrollo de la Unidad II y agregar funcionamiento del lado del servidor. La entrega anterior ya mostraba interacción del usuario mediante JavaScript, pero las operaciones todavía eran simuladas y no existía conexión real a backend ni base de datos.

## 2. Requerimientos cubiertos

- Mantener las pantallas principales del sistema CyberShield AI.
- Validar el inicio de sesión desde el servidor.
- Guardar y consultar datos persistentes.
- Registrar incidentes desde formulario.
- Consultar, filtrar, atender y escalar alertas.
- Generar reportes como registros persistentes.
- Registrar una validación final del módulo.
- Entregar archivos completos del sitio y de la base de datos.
- Preparar información para el video requerido por la rúbrica.

## 3. Modelo de dominio

Entidades principales:

- Usuario: representa al analista que ingresa al sistema.
- Sesión: token temporal que permite consumir API.
- Alerta: evento detectado por el sistema con severidad, origen, recomendación y estado.
- Incidente: caso de seguridad confirmado o escalado desde una alerta.
- Reporte: registro de un reporte generado desde el módulo de reportes.
- Validación: evidencia interna de la validación funcional de un módulo.
- Métrica: datos del dashboard.

## 4. Arquitectura

Se utiliza una arquitectura simple cliente-servidor:

- Cliente: HTML, CSS y JavaScript en la carpeta `public`.
- Servidor: Escrito en **Java 17** usando la biblioteca estándar (`com.sun.net.httpserver.HttpServer`), responsable de servir archivos estáticos y exponer endpoints JSON.
- Base de datos: SQLite en `database/cybershield.db` (conectado a través de JDBC).

Esta arquitectura se eligió porque el alcance académico requiere demostrar el funcionamiento del lado del servidor sin introducir infraestructura pesada ni frameworks gigantescos como Spring Boot. El servidor estándar de Java 17 permite ejecutar el proyecto localmente con cero dependencias de servidor externo, y SQLite permite entregar la base de datos como un archivo local portable. Esto mantiene coherencia total con las tecnologías declaradas en las entregas de las Unidades 1 y 2 (Java 17 + Maven).

## 5. Diseño de datos

Tablas implementadas:

- `users`
- `sessions`
- `alerts`
- `incidents`
- `reports`
- `validations`
- `metrics`

Se usan llaves primarias, relaciones con llaves foráneas, restricciones `CHECK` para severidades y estados, y consultas parametrizadas desde el backend.

## 6. Diseño de API

La API trabaja con JSON y rutas REST simples:

- Autenticación: `/api/auth/login`, `/api/auth/logout`.
- Dashboard: `/api/dashboard`.
- Alertas: `/api/alerts`, `/api/alerts/{id}`, `/api/alerts/{id}/attend`, `/api/alerts/{id}/escalate`.
- Incidentes: `/api/incidents`.
- Reportes: `/api/reports`.
- Validación: `/api/validations`.

## 7. Estrategia de implementación

1. Crear estructura de base de datos.
2. Insertar datos iniciales del prototipo.
3. Implementar servidor HTTP y conexión SQLite.
4. Crear endpoints protegidos por token.
5. Adaptar JavaScript para consumir API con `fetch`.
6. Probar login, dashboard, alertas, incidentes, reportes y validación.
7. Documentar guion de video y checklist de entrega.

## 8. Alcance real de la actividad

La implementación cumple la parte del servidor necesaria para la primera iteración académica. No implementa monitoreo real de red, inteligencia artificial productiva ni generación binaria de PDF porque esas funciones exceden el alcance de la actividad y requieren infraestructura adicional.
