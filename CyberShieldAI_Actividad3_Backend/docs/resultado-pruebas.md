# Resultado de pruebas técnicas

Se ejecutó una prueba smoke sobre el servidor local antes de preparar el ZIP de entrega.

Resultado:

```text
Pruebas smoke completadas correctamente.
```

Casos verificados:

- `GET /api/health` respondió correctamente.
- `POST /api/auth/login` validó las credenciales de prueba.
- `GET /api/dashboard` devolvió métricas desde base de datos.
- `GET /api/alerts?severity=alta` devolvió alertas filtradas.
- `PATCH /api/alerts/{id}/attend` actualizó el estado de una alerta.
- `POST /api/incidents` registró un incidente.
- `POST /api/reports` guardó un reporte.
- `POST /api/validations` guardó una validación.

Después de la prueba, la base de datos de entrega fue reinicializada con los datos semilla para que el proyecto llegue limpio.
