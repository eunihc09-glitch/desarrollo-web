package mx.plataforma.ciberseguridad;

import java.util.Random;

public class OtpService {

    public String generateOtp() {

        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);

        return String.valueOf(otp);
    }

    public boolean validateOtp(String generatedOtp, String enteredOtp) {
        return generatedOtp.equals(enteredOtp);
    }
}