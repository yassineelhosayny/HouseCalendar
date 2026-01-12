package gestione;

import dataBase.DAO.UtenteDAO;
import dominio.Utente;

public class LoginGestione {

    // utente attualmente loggato (in memoria)
    private Utente utenteAttuale;

    // LOGIN
    public boolean login(String email, String password) {
        if (email == null || password == null ||
            email.isBlank() || password.isBlank()) {
            return false;
        }

        // controllo credenziali nel DB
        boolean ok = UtenteDAO.checkLogin(email, password);

        if (ok) {
            // carico l'utente dal DB e lo salvo come utente corrente
            utenteAttuale = UtenteDAO.getUtenteByEmail(email);
            return true;
        }

        return false;
    }

    //logout
    public void logout() {
        utenteAttuale = null;
    }

    // è loggato
    public boolean isLoggato() {
        return utenteAttuale != null;
    }

    //utente attuale
    public Utente getUtenteAttuale() {
        return utenteAttuale;
    }

    //REGISTRAZIONE 
    public boolean registraUtente(String nome, String email, String password) {
        if (nome == null || email == null || password == null ||
            nome.isBlank() || email.isBlank() || password.isBlank()) {
            return false;
        }

        //controllo se esiste già
        if (UtenteDAO.getUtenteByEmail(email) != null) {
            return false;
        }
        //altramenti crea utente
        Utente nuovo = new Utente(nome, email, password);
        return UtenteDAO.aggiungiUtente(nuovo);
    }
}
