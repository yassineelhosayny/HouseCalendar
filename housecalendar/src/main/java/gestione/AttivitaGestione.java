package gestione;
import dominio.Attivita;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Observer;

//classe per la gestione delle attività
public interface AttivitaGestione {
    //dobbiamo definire i metodi per la gestione delle attività

        public static List<Attivita> lista = null;

            // CRUD
        public void aggiungiAttivita( Map<String, Object> parametri);
        public boolean verificaConflitti(Attivita a) ;
        public void modificaAttivita(int id, Map<String, Object> nuoviParametri);
        public void rimuoviAttivita(int id);
        public Attivita getAttivitaById(int id);

        // Filtri e ricerche
        public List<Attivita> cercaPerData(LocalDateTime data);
        public List<Attivita> cercaPerNome(String nome);
        public List<Attivita> cercaPerTipo(String tipo);
        public List<Attivita> filtra(Map<String, Object> criteri);

        // Controlli applicativi
        public boolean verificaSovrapposizione(Attivita nuova);
        public boolean esiste(int id);

        // Observer
        public void aggiungiObserver(Observer obs);
        public void rimuoviObserver(Observer obs);
        public void notificaObservers(String evento, Attivita attivita);

        // Utilità
        public List<Attivita> getTutte();
        public void caricaDaDB(String path);
        public void salvaSuDB(String path);
}
