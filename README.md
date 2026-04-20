# desarrollo-web
desarrollo de proyecto de ciberseguridad
# Plataforma de Ciberseguridad para PYMEs

Implementación del diagrama de clases en **Java**.  
Proyecto: Plataforma integral de soluciones de ciberseguridad para pequeñas y medianas empresas.

**Autora:** Eunice Hernández Cruz

---

## Estructura del proyecto

```
pymes/
└── 
    ├── Permiso.java                  ← Enum de permisos del sistema
    ├── Rol.java                      ← Roles con conjuntos de permisos
    ├── Usuario.java                  ← Usuarios con autenticación MFA
    ├── Alerta.java                   ← Alertas de seguridad
    ├── Incidente.java                ← Incidentes con bitácora completa
    ├── Reporte.java                  ← Reportes ejecutivos exportables a PDF
    ├── MotorIA.java                  ← Motor de IA para detección de anomalías
    ├── ModuloMonitoreo.java           ← Monitoreo continuo de red y endpoints
    ├── ModuloSeguridad.java           ← Autenticación, cifrado y análisis IA
    └── PlataformaSeguridad.java       ← Clase central coordinadora
```

---

## Requisitos

- **JDK 17** o superior
- **IntelliJ IDEA Community Edition** (recomendado) o cualquier IDE con soporte Java

---

## Cómo ejecutar

### Opción 1: IntelliJ IDEA
1. Abrir IntelliJ IDEA → `File > Open` → seleccionar la carpeta `pymes`
2. Esperar que IntelliJ detecte la estructura del proyecto
3. Abrir `Main.java`
4. Clic derecho → `Run 'Main.main()'`

### Opción 2: Línea de comandos
```bash
# Desde la raíz del proyecto
javac -d out -sourcepath src/main/java $(find src -name "*.java")
java -cp out mx.plataforma.ciberseguridad.Main
```

---

## Clases implementadas (del diagrama de clases)

| Clase del diagrama    | Archivo Java              | Descripción                                      |
|-----------------------|---------------------------|--------------------------------------------------|
| USUARIO               | `Usuario.java`            | Gestión de sesión, MFA, control de acceso        |
| ROL                   | `Rol.java`                | Agrupación de permisos (RBAC)                    |
| PERMISO               | `Permiso.java`            | Enumeración de permisos del sistema              |
| INCIDENTE             | `Incidente.java`          | Registro, trazabilidad y flujo de resolución     |
| REPORTE               | `Reporte.java`            | Generación y exportación PDF                     |
| MODULO MONITOREO      | `ModuloMonitoreo.java`    | Captura de logs, detección de anomalías, alertas |
| MODULO SEGURIDA       | `ModuloSeguridad.java`    | Autenticación, cifrado AES-256, predicción IA    |
| MOTORIA               | `MotorIA.java`            | Análisis de patrones y predicción de riesgo      |
| ALERTA                | `Alerta.java`             | Notificaciones en tiempo real vía WebSocket      |
| PLATAFORMA SEGURIDAD  | `PlataformaSeguridad.java`| Clase central coordinadora de todos los módulos  |

---

## Casos de prueba cubiertos en Main.java

| ID         | Descripción                                               |
|------------|-----------------------------------------------------------|
| CP-F-001   | Login exitoso con credenciales válidas y código MFA       |
| CP-F-002   | Login con contraseña incorrecta → bloqueo tras 5 intentos |
| CP-F-003   | Monitoreo en tiempo real con latencia < 5 segundos        |
| CP-F-004   | Generación de reporte ejecutivo PDF                       |
| CP-F-007   | Control de acceso por rol → HTTP 403 para Operador        |
| CP-F-008   | Análisis predictivo de IA con confianza ≥ 70%             |
| CP-F-010   | Validación de correo duplicado al registrar usuario       |

---

## Lenguaje e IDE

**Lenguaje:** Java 17  
**IDE:** IntelliJ IDEA Community Edition  

**Justificación:** Java fue seleccionado por su sólido soporte al paradigma orientado a objetos, tipado estático estricto, y amplio ecosistema de bibliotecas de seguridad (Spring Security, JWT, Bouncy Castle). IntelliJ IDEA facilita la refactorización, generación de código y depuración, lo que acelera el desarrollo de sistemas complejos como esta plataforma.
