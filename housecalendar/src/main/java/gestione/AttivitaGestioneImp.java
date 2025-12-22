package gestione;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Observer;

import dominio.Attivita;
import dominio.AttivitaFactory;
import dominio.GestoreAttivita;
import dominio.TipoAttivita;
import dominio.Utente;
import DAO.AttivitaDAO;

public class AttivitaGestioneImp implements AttivitaGestione {
     GestoreAttivita gestoreAttivita = GestoreAttivita.getInstance();

    @Override
    public void aggiungiAttivita( Map<String, Object> parametri) {
       
        String descrizione = (String)parametri.get("descrizione");
        TipoAttivita tipo = (TipoAttivita) parametri.get("tipo");
        LocalDateTime dataInizio =(LocalDateTime) parametri.get("dataInizio");
        LocalDateTime dataFine =(LocalDateTime) parametri.get("dataFine");
        LocalDateTime dataNotifica = (LocalDateTime) parametri.get("dataNotifica");
        Integer priorita = (Integer)parametri.get("priorita"); 
        Utente utenteAssegnato = (Utente) parametri.get("utenteAssegnato");
        Boolean attivitaPrivata =(Boolean) parametri.get("attivitaPrivata");
        String context = (String) parametri.get("context");
        
        //controlli
        if (descrizione == null || tipo == null || dataInizio == null || dataFine == null ||
        dataNotifica == null || priorita == null || utenteAssegnato == null || attivitaPrivata == null) 
           {
        throw new IllegalArgumentException("Parametri mancanti o null.");
           }
        if (dataNotifica.isAfter(dataInizio) || dataNotifica.isBefore(LocalDateTime.now())) {
                 throw new IllegalArgumentException("La data di notifica non può essere nel passato o successiva alla data dell'evento.");
            }

        if(dataInizio.isAfter(dataFine)){
            throw new IllegalArgumentException("La data di Inizio no pò essere successiva alla data Fine del evento.");
        }

        Attivita a = AttivitaFactory.crea(descrizione,tipo, dataInizio,dataFine, dataNotifica, (int)priorita, utenteAssegnato, (boolean) attivitaPrivata,context);
        if(verificaConflitti(a)){
            throw new IllegalArgumentException("tempo occupatoooo");
        }

        gestoreAttivita.aggiungiAttivita(a); //aggiungere l'attivita al la lista in il gestore o RAM
        AttivitaDAO.aggiungiAttivita(a);  // aggiungere L'attivita alla data base " da implimentare dopo"

        
    }
    @Override
    public boolean verificaConflitti(Attivita a) {
        for(Attivita attivita : gestoreAttivita.getTutteLeAttivita()){
            if(a.getUtenteAssegnato().equals(attivita.getUtenteAssegnato()) && attivita.isBetweenDataOccupata(a)){
                    return true;
            }
        }
        return false;
    }

    @Override
    public void modificaAttivita(int id, Map<String, Object> nuoviParametri) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'modificaAttivita'");
    }

    @Override
    public void rimuoviAttivita(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rimuoviAttivita'");
    }

    @Override
    public Attivita getAttivitaById(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAttivitaById'");
    }

    @Override
    public List<Attivita> cercaPerData(LocalDateTime data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cercaPerData'");
    }

    @Override
    public List<Attivita> cercaPerNome(String nome) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cercaPerNome'");
    }

    @Override
    public List<Attivita> cercaPerTipo(String tipo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cercaPerTipo'");
    }

    @Override
    public List<Attivita> filtra(Map<String, Object> criteri) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'filtra'");
    }

    @Override
    public boolean verificaSovrapposizione(Attivita nuova) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'verificaSovrapposizione'");
    }

    @Override
    public boolean esiste(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'esiste'");
    }

    @Override
    public void aggiungiObserver(Observer obs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'aggiungiObserver'");
    }

    @Override
    public void rimuoviObserver(Observer obs) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rimuoviObserver'");
    }

    @Override
    public void notificaObservers(String evento, Attivita attivita) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notificaObservers'");
    }

    @Override
    public List<Attivita> getTutte() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTutte'");
    }

    @Override
    public void caricaDaDB(String path) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'caricaDaDB'");
    }

    @Override
    public void salvaSuDB(String path) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'salvaSuDB'");
    }
    
}
