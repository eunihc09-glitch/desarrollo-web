# CyberShield AI - Actividad 3: Funcionamiento del lado del servidor

Proyecto web completo para la tercera actividad de Desarrollo Web. Retoma el FrontEnd de la Unidad II y agrega funcionamiento real del lado del servidor mediante una API conectada a una base de datos SQLite.

## Tecnologías utilizadas

- FrontEnd: HTML5, CSS3 y JavaScript puro.
- BackEnd: Java 17 con Maven (usando el servidor HTTP incorporado `com.sun.net.httpserver.HttpServer`).
- Gestor de Base de Datos: SQLite (conectado mediante JDBC).
- Persistencia: archivo `database/cybershield.db`.

## Funciones implementadas del lado del servidor

1. Autenticación con validación de correo y contraseña contra la base de datos.
2. Creación de sesión temporal con token.
3. Dashboard con métricas calculadas desde datos persistentes.
4. Consulta, búsqueda y filtrado de alertas desde API.
5. Cambio de estado de alerta a atendida mediante actualización en base de datos.
6. Escalamiento de alerta a incidente con inserción real en la tabla de incidentes.
7. Registro de incidentes desde formulario con validación en servidor.
8. Generación de reportes con guardado de historial.
9. Registro de validaciones de módulos en base de datos.
10. Servicio de archivos HTML, CSS y JavaScript desde el mismo servidor.

## Credenciales de prueba

- Correo: `demo@cybershield.ai`
- Contraseña: `demo1234`

## Cómo ejecutar

### Windows

1. Instalar JDK 17 y Maven.
2. Abrir la carpeta del proyecto.
3. Ejecutar `run.bat` (compila y arranca el servidor).
4. Abrir `http://localhost:8000` en el navegador.

### Linux o macOS

```bash
chmod +x run.sh
./run.sh
```

También se puede ejecutar directamente:

```bash
mvn clean compile exec:java
```

## Archivos principales

- `server.py`: servidor y API.
- `public/`: sitio web completo.
- `database/schema.sql`: estructura de la base de datos.
- `database/seed.sql`: datos iniciales.
- `database/cybershield.db`: base de datos SQLite generada.
- `docs/guion-video.md`: información lista para grabar el video.
- `docs/checklist-entrega.md`: validación contra criterios de evaluación.

## Endpoints principales

- `GET /api/health`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/dashboard`
- `GET /api/alerts`
- `GET /api/alerts/{id}`
- `PATCH /api/alerts/{id}/attend`
- `POST /api/alerts/{id}/escalate`
- `GET /api/incidents`
- `POST /api/incidents`
- `GET /api/reports`
- `POST /api/reports`
- `POST /api/validations`