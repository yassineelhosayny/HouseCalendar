package gestione;

import java.util.prefs.Preferences;

public final class SessioneUtente {

    private static final String KEY_EMAIL = "utente_corrente_email";
    private static final Preferences PREFS = Preferences.userNodeForPackage(SessioneUtente.class);

    private SessioneUtente() {
    }

    public static void salvaEmail(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        PREFS.put(KEY_EMAIL, email);
    }

    public static String getEmail() {
        return PREFS.get(KEY_EMAIL, null);
    }

    public static void pulisci() {
        PREFS.remove(KEY_EMAIL);
    }
}
