package mx.plataforma.ciberseguridad;

import java.util.Scanner;

public class DemoIntegracion {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        PasswordService passwordService = new PasswordService();
        JwtService jwtService = new JwtService();
        OtpService otpService = new OtpService();

        String username = "admin";
        String password = "Admin123";
        String role = "ADMINISTRADOR";

        System.out.println("===== PLATAFORMA DE CIBERSEGURIDAD =====");

        String hashedPassword = passwordService.hashPassword(password);

        System.out.print("Usuario: ");
        String inputUser = scanner.nextLine();

        System.out.print("Contraseña: ");
        String inputPassword = scanner.nextLine();

        boolean validPassword = passwordService.verifyPassword(inputPassword, hashedPassword);

        if (!inputUser.equals(username) || !validPassword) {
            System.out.println("Acceso denegado. Credenciales incorrectas.");
            return;
        }

        System.out.println("Credenciales correctas.");

        String generatedOtp = otpService.generateOtp();

        System.out.println("OTP generado: " + generatedOtp);

        System.out.print("Ingrese el OTP: ");
        String enteredOtp = scanner.nextLine();

        boolean validOtp = otpService.validateOtp(generatedOtp, enteredOtp);

        if (!validOtp) {
            System.out.println("OTP inválido.");
            return;
        }

        String token = jwtService.generateToken(username, role);

        System.out.println("\nAutenticación exitosa.");
        System.out.println("JWT generado:\n");
        System.out.println(token);

        boolean tokenValid = jwtService.validateToken(token);

        System.out.println("\nValidación del token: " + tokenValid);
    }
}