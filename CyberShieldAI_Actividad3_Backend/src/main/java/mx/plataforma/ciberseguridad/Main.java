package mx.plataforma.ciberseguridad;

import mx.plataforma.ciberseguridad.web.DatabaseManager;
import mx.plataforma.ciberseguridad.web.WebServer;

public class Main {

    public static void main(String[] args) {
        System.out.println("Iniciando servidor de ciberseguridad CyberShield AI...");

        // 1. Inicializar el manejador de base de datos y correr scripts schema/seed si es necesario
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.initDatabase();

        // 2. Levantar el servidor HTTP en el puerto 8000
        int port = 8000;
        WebServer webServer = new WebServer(port, dbManager);
        webServer.start();

        // Registrar hook para apagar limpiamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Apagando servidor web...");
            webServer.stop();
            System.out.println("Servidor apagado.");
        }));
    }
}
