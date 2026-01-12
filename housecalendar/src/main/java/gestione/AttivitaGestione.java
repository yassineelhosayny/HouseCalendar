package gestione;
import dominio.Attivita;
import dominio.TipoAttivita;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


//classe per la gestione delle attività
public interface AttivitaGestione {
    //dobbiamo definire i metodi per la gestione delle attività
            // CRUD
        public void aggiungiAttivita( Map<String, Object> parametri);
        public void modificaAttivita(int id, Map<String, Object> nuoviParametri);
        public void rimuoviAttivita(int id);
    

        // Filtri e ricerche
        public List<Attivita> cercaPerData(LocalDateTime data);
        public List<Attivita> cercaPerNome(String nome);
        public List<Attivita> cercaPerTipo(String tipo);
        public Attivita cercaperid(int id) ;
         public List<Attivita> cercaperpriorita(int p);
        public List<Attivita> filtra(Map<String, Object> criteri);

        // Controlli applicativi
        public boolean verificaConflitti(Attivita a) ;
        public Attivita getAttivitaById(int id);  // a || null ;exception

        // Utilità
        public List<Attivita> getTutteLeAttivita();
        public void caricaDaDB();
        TipoAttivita getTipoByString(String strTipo);
}
