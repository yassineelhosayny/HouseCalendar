package gestione;

import java.util.Observer;

import observer.Osservatore;
import observer.SoggettoOsservabile;

//classe per la gestione del login degli utenti
public class LoginGestione {
    // Gestione autenticazione
        
        public boolean login(String username, String password){
            return false;
        };
        public void logout(){
            
        }
        public boolean isLoggato(){
            return false;
        }
        public String getUtenteAttuale(){
            return null;
        }

        // Observer
        public void aggiungiObserver(Osservatore obs){
            
        }
        public void rimuoviObserver(Osservatore obs){
            
        }
        public void notificaObservers(String evento){

        }

        // Utenti (opzionale)
        public void registraUtente(String username, String password){

        }
        public boolean esisteUtente(String username){
            return false;
        }
}
