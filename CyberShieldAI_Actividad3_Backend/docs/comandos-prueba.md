# Comandos de prueba rápida

Iniciar el servidor:

```bash
mvn clean compile exec:java
```

Verificar salud del servidor:

```bash
curl http://localhost:8000/api/health
```

Login:

```bash
curl -X POST http://localhost:8000/api/auth/login -H "Content-Type: application/json" -d "{\"email\":\"demo@cybershield.ai\",\"password\":\"demo1234\"}"
```

Después del login, copiar el token recibido y usarlo en las demás pruebas:

```bash
curl http://localhost:8000/api/dashboard -H "Authorization: Bearer TOKEN_AQUI"
```

```bash
curl http://localhost:8000/api/alerts -H "Authorization: Bearer TOKEN_AQUI"
```

```bash
curl -X POST http://localhost:8000/api/incidents -H "Content-Type: application/json" -H "Authorization: Bearer TOKEN_AQUI" -d "{\"type\":\"Phishing\",\"incident_date\":\"2026-06-21\",\"severity\":\"media\",\"description\":\"Correo sospechoso reportado por usuario interno.\",\"responsible\":\"Carlos Martínez\"}"
```
