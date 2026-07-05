# Checklist de cumplimiento de la Actividad 3

## Criterio 1: Retoma el desarrollo de la Unidad 2 agregando funcionamiento del lado del servidor

Cumplido.

Evidencia:

- Se conservan las pantallas del FrontEnd: `index.html`, `login.html`, `dashboard.html`, `alertas.html`, `incidentes.html`, `reportes.html` y `validacion.html`.
- Se agrega la estructura del backend en **Java 17** y **Maven** (carpeta `src/` y archivo `pom.xml`).
- Se agrega base de datos SQLite en `database/cybershield.db`.
- Se reemplazan las simulaciones locales por peticiones reales a endpoints del servidor.

## Criterio 2: Video con lenguaje, gestor de base de datos, fundamentación y funcionamiento del sitio web

Cumplido con material preparado.

Evidencia:

- `docs/guion-video.md` contiene la explicación del lenguaje usado.
- El mismo documento explica el gestor de base de datos seleccionado.
- Incluye fundamentación de la elección tecnológica.
- Incluye orden de demostración de pantallas y funciones.

## Criterio 3: Funcionamiento completo, correcto y necesario del lado del servidor

Cumplido para el alcance de la primera iteración.

Funciones verificables:

- Login validado en servidor.
- Sesión con token.
- Dashboard alimentado desde base de datos.
- Consulta y búsqueda de alertas.
- Cambio de estado de alerta.
- Escalamiento de alerta a incidente.
- Registro de incidentes desde formulario.
- Generación de historial de reportes.
- Registro de validación de módulos.

## Archivos que deben subirse a la nube

- Archivo `pom.xml`
- Carpeta `src/` (código fuente Java del backend)
- `run.bat`
- `run.sh`
- Carpeta `public/`
- Carpeta `database/`
- Carpeta `docs/`
- Carpeta `tests/`
- `README.md`

## Revisión antes de entregar

- Ejecutar `./run.bat` o `mvn compile exec:java`.
- Abrir `http://localhost:8000`.
- Iniciar sesión con las credenciales de prueba.
- Registrar un incidente.
- Marcar una alerta como atendida.
- Escalar una alerta a incidente.
- Generar un reporte.
- Registrar una validación.
- Confirmar que `database/cybershield.db` existe.
