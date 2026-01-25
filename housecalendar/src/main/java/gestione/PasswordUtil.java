package gestione;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {

    private static final int ITERAZIONI = 65536;
    private static final int LUNGHEZZA_CHIAVE = 256;

    private PasswordUtil() {
    }

    public static String generaSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String saltBase64) {
        if (password == null || saltBase64 == null) {
            return null;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERAZIONI, LUNGHEZZA_CHIAVE);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean verificaPassword(String password, String saltBase64, String hashBase64) {
        if (password == null || saltBase64 == null || hashBase64 == null) {
            return false;
        }
        String calcolato = hashPassword(password, saltBase64);
        return hashBase64.equals(calcolato);
    }
}
