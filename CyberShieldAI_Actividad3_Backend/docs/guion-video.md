# Guion sugerido para el video de la Actividad 3

## Duración sugerida

Entre 4 y 6 minutos.

## 1. Presentación inicial

Buenos días/tardes. En este video se presenta la Actividad 3 del proyecto CyberShield AI, correspondiente a una plataforma integral de ciberseguridad para pequeñas y medianas empresas. En esta entrega se retoma el desarrollo de la Unidad II y se agrega funcionamiento del lado del servidor para cubrir los requerimientos de la primera iteración.

## 2. Lenguaje utilizado

Para el FrontEnd se utilizaron HTML5, CSS3 y JavaScript puro. HTML5 permite estructurar las pantallas, CSS3 permite definir el diseño visual responsivo y JavaScript permite consumir los servicios del servidor mediante peticiones `fetch`.

Para el lado del servidor se utilizó **Java 17** gestionado con **Maven**. Se eligió Java 17 porque es un lenguaje robusto, fuertemente tipado y alineado con los requerimientos técnicos y de diseño definidos en las entregas de las Unidades 1 y 2. En esta implementación se utiliza el servidor HTTP incorporado en el JDK (`com.sun.net.httpserver.HttpServer`) para mantener el proyecto ligero, evitando frameworks pesados y facilitando su ejecución.

## 3. Gestor de Base de Datos seleccionado

El gestor de base de datos seleccionado fue SQLite. Se eligió porque es una base de datos relacional ligera, se guarda en un solo archivo y facilita la entrega en la nube junto con el proyecto. También permite trabajar con tablas, llaves primarias, llaves foráneas, restricciones y consultas SQL reales sin requerir instalar un servidor de base de datos externo.

## 4. Fundamentación técnica

La elección de **Java 17** y **SQLite** responde al alcance de la primera iteración y a la coherencia del diseño inicial del proyecto. El objetivo no es desplegar una plataforma compleja en producción, sino demostrar que el frontend de ciberseguridad se comunica mediante llamadas REST con un servidor real de Java y persiste información de manera persistente en SQLite usando JDBC. Usar Maven nos permite estructurar dependencias como Gson para el manejo de JSON sin complicar la compilación y ejecución.

## 5. Explicación de la arquitectura

El proyecto funciona con una arquitectura cliente-servidor. Las pantallas se encuentran en la carpeta `public`. El backend en **Java** sirve esas pantallas y expone endpoints de una API REST en formato JSON usando la librería Gson. La información se almacena en la base de datos `database/cybershield.db`, cuya estructura está documentada en `schema.sql` y cuyos datos iniciales están en `seed.sql`.

## 6. Demostración del funcionamiento

Primero se ejecuta el servidor con el comando:

```bash
mvn clean compile exec:java
# O bien, haciendo doble clic en el archivo run.bat
```

Después se abre el navegador en:

```text
http://localhost:8000
```

En la página principal se puede verificar que el servidor responde correctamente con el botón de verificación.

Luego se entra al formulario de inicio de sesión. Las credenciales de prueba son:

```text
Correo: demo@cybershield.ai
Contraseña: demo1234
```

Al iniciar sesión, el servidor valida el correo y la contraseña contra la tabla de usuarios y genera una sesión temporal.

En el dashboard se muestran métricas obtenidas desde la base de datos, como nivel de seguridad, alertas activas, incidentes abiertos y dispositivos monitoreados.

En la pantalla de alertas se consultan datos reales desde la API. Se puede filtrar por severidad, buscar por texto, abrir el detalle de una alerta, marcarla como atendida y escalarla a incidente. Cuando se escala una alerta, el servidor crea un registro nuevo en la tabla de incidentes.

En la pantalla de incidentes se muestra el historial guardado en la base de datos. También se puede abrir el formulario de registro, capturar tipo, fecha, severidad, descripción y responsable. Al guardar, el servidor valida los datos y crea el incidente en SQLite.

En la pantalla de reportes se puede generar un reporte. En esta primera iteración se registra el reporte en la base de datos como evidencia de generación, mostrando nombre, tipo, rango y fecha.

En la pantalla de validación se selecciona un módulo y se registra el resultado de validación en el servidor, lo que demuestra que el formulario ya no solo muestra mensajes locales, sino que también persiste la información.

## 7. Cierre del video

Con esta entrega se cumple el funcionamiento necesario del lado del servidor para la primera iteración, ya que el sistema incluye autenticación, consultas, registros, actualizaciones y persistencia en base de datos. La entrega contiene los archivos completos del sitio web, el servidor, los scripts SQL y la base de datos SQLite, listos para compartirse mediante GitHub, Drive u otro enlace en la nube.
