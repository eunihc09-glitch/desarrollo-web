INSERT OR IGNORE INTO metrics (id, security_level, monitored_devices, blocked_threats, response_time) VALUES (1, 94, 128, 1284, '2.4 s');
INSERT OR IGNORE INTO alerts (id, severity, description, origin, recommendation, status, created_at) VALUES
(1, 'alta', 'Acceso no autorizado al panel admin', '192.168.1.45', 'Bloquear IP, revisar logs de acceso y forzar cambio de contraseña del administrador.', 'Pendiente', '2026-06-21 08:12:00'),
(2, 'media', 'Tráfico inusual en servidor de archivos', '10.0.0.22', 'Analizar consumo de red, verificar procesos activos y validar integridad de archivos compartidos.', 'Pendiente', '2026-06-21 07:48:00'),
(3, 'media', 'Inicio de sesión fuera de horario laboral', 'Equipo-RH02', 'Confirmar identidad del usuario y revisar bitácora de sesión.', 'Atendida', '2026-06-20 23:10:00'),
(4, 'baja', 'Actualización de firmware pendiente', 'Router-01', 'Programar ventana de mantenimiento y aplicar actualización validada.', 'Atendida', '2026-06-14 14:02:00'),
(5, 'baja', 'Certificado SSL próximo a vencer', 'soporte interno', 'Renovar certificado antes de la fecha de vencimiento.', 'Atendida', '2026-06-18 09:00:00'),
(6, 'alta', 'Múltiples intentos de inicio de sesión fallidos', 'Cuenta: yvalencia', 'Aplicar bloqueo temporal, verificar MFA y revisar origen de intentos.', 'Atendida', '2026-06-17 11:35:00');
INSERT OR IGNORE INTO incidents (id, type, incident_date, severity, description, responsible, status, alert_id) VALUES
(100, 'Malware en estación de trabajo', '2026-06-18', 'alta', 'Se detectó comportamiento anómalo en estación de trabajo.', 'Laura Ramírez', 'Resuelto', NULL),
(101, 'Intento de phishing', '2026-06-20', 'media', 'Usuario reportó correo con enlace sospechoso.', 'Carlos Martínez', 'En proceso', NULL),
(102, 'Acceso no autorizado', '2026-06-21', 'alta', 'Intento de acceso al panel administrativo.', 'Carlos Martínez', 'Abierto', 1);
INSERT OR IGNORE INTO reports (id, name, report_type, date_range, created_at) VALUES
(1, 'Reporte_Ejecutivo_Junio2026.pdf', 'Ejecutivo', '01/06/2026 - 21/06/2026', '2026-06-21 10:30:00'),
(2, 'Reporte_Tecnico_Mayo2026.pdf', 'Técnico', '01/05/2026 - 31/05/2026', '2026-06-01 09:15:00'),
(3, 'Reporte_Cumplimiento_Q2.pdf', 'Cumplimiento', '01/04/2026 - 30/06/2026', '2026-05-15 12:00:00');
