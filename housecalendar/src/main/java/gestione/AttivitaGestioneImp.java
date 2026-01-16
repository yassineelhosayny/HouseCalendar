package gestione;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dominio.Attivita;

import dominio.AttivitaFactory;

import dominio.GestoreAttivita;
import dominio.TipoAttivita;
import dominio.Utente;


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

    private boolean verificaConflittiModifica(Attivita a, int idDaEscludere) {
        for (Attivita att : gestoreAttivita.getTutteLeAttivita()) {
            if (att.getId() == idDaEscludere) continue;
            if (a.getUtenteAssegnato().equals(att.getUtenteAssegnato()) && att.isBetweenDataOccupata(a)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void modificaAttivita(int id, Map<String, Object> nuoviParametri) {
        String descrizione = (String) nuoviParametri.get("descrizione");
        TipoAttivita tipo = (TipoAttivita)nuoviParametri.get("tipo");
        LocalDateTime dataInizio = (LocalDateTime) nuoviParametri.get("dataInizio");
        LocalDateTime dataFine = (LocalDateTime) nuoviParametri.get("dataFine");
        LocalDateTime dataNotifica = (LocalDateTime)nuoviParametri.get("dataNotifica");
        Integer priorita = (Integer) nuoviParametri.get("priorita");
        Utente utenteAssegnato = (Utente) nuoviParametri.get("utenteAssegnato");
        Boolean attivitaPrivata = (Boolean)nuoviParametri.get("attivitaPrivata");
        String context = (String) nuoviParametri.get("context");

        // controlli
        if (descrizione == null || tipo == null || dataInizio == null || dataFine == null ||
            dataNotifica == null || priorita == null || utenteAssegnato == null || attivitaPrivata == null) {
            throw new IllegalArgumentException("Errore: Parametri mancanti o null.");
        }
        if (dataInizio.isAfter(dataFine)) {
            throw new IllegalArgumentException("Errore: data inizio dopo data fine");
        }
        if (dataNotifica.isBefore(LocalDateTime.now()) || dataNotifica.isAfter(dataInizio)) {
            throw new IllegalArgumentException("Errore: data notifica non valida");
        }

        // verifica esistenza
        Attivita vecchia = gestoreAttivita.getAttivitaById(id);
        if (vecchia == null) {
            throw new IllegalArgumentException("Errore: Attività non esiste");
        }

        // CREA una nuova attività completa "incluso context
        Attivita nuova = AttivitaFactory.crea(descrizione, tipo, dataInizio, dataFine, dataNotifica,priorita, utenteAssegnato, attivitaPrivata, context);

        nuova.setId(id);

        //ignora se stessa (stesso id)
        if (verificaConflittiModifica(nuova, id)) {
            throw new IllegalArgumentException("Errore: tempo occupato");
        }

        // passa al dominio (DB + RAM+ notifica)
        gestoreAttivita.modificaAttivita(id, nuova);
    }


    @Override
    public void rimuoviAttivita(int id) {
        Attivita a = gestoreAttivita.getAttivitaById(id);
        if( a == null)
             throw new IllegalArgumentException("Errore: Attività non esiste");
        
        gestoreAttivita.rimuoviAttivita(a);
    }

    @Override
    public Attivita getAttivitaById(int id) {
         Attivita a = gestoreAttivita.getAttivitaById(id);
        if( a== null)
             throw new IllegalArgumentException("Errore: Attività non esiste");

        return a;
    }

    @Override
    public List<Attivita> cercaPerData(LocalDateTime datainizio, LocalDateTime datafine) {
        List<Attivita> listaRes= new ArrayList<>();
    if (datainizio == null || datafine == null) {
        throw new IllegalArgumentException("Errore: data/ora mancanti per la ricerca.");
    }
    if (datainizio.isAfter(datafine)) {
        throw new IllegalArgumentException("Errore: l'inizio non può essere dopo la fine.");
    }
        listaRes = gestoreAttivita.cercaperdata(datainizio,datafine);
        return listaRes;
    }

    @Override
    public List<Attivita> cercaPerNome(String descrizione) {
       List<Attivita> listaRes= new ArrayList<>();
        if(descrizione == null || descrizione.isBlank()){
           throw new IllegalArgumentException("Errore: manca il nome o il descrizone del Attivita.!");
        }
        listaRes = gestoreAttivita.cercapernome(descrizione);
        return listaRes;
    }
    @Override
    public TipoAttivita getTipoByString(String strTipo){
        if (strTipo == null || strTipo.isBlank()) {
        throw new IllegalArgumentException("Errore: tipo di attività mancante");
    }
         TipoAttivita tipovalido;
        try {
            tipovalido = TipoAttivita.valueOf(strTipo.toUpperCase());
            return tipovalido;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Errore: tipo di attività non valido");
        }
    }
    @Override
    public List<Attivita> cercaPerTipo(String tipo) {
        List<Attivita> listaRes= new ArrayList<>();
       
        TipoAttivita tipovalido = getTipoByString(tipo);

        listaRes = gestoreAttivita.cercapertipo(tipovalido.name());
        return listaRes;
    }
    
    @Override
    public Attivita cercaperid(int id) {
        if(id <= 0){
            throw new IllegalArgumentException("Errore:id non valido.!");
        }
        return gestoreAttivita.getAttivitaById(id);
    }
    @Override
    public List<Attivita> cercaperpriorita(int p) {
        if(p <= 0 || p > 3){
            throw new IllegalArgumentException("Errore: priorita non valida!");
        }
        return gestoreAttivita.cercaperpriorita(p);
    }

    @Override
    public List<Attivita> filtra(Map<String, Object> criteri) {
        List<Attivita> result = new ArrayList<>(gestoreAttivita.getTutteLeAttivita());
        //accumula filtri descrizione +id + tipo+ data + priorita
        String descrizione = (String)criteri.get("descrizione");
        Integer id = (Integer) criteri.get("id");
        String tipo = (String) criteri.get("tipo");
        LocalDateTime data = (LocalDateTime) criteri.get("data");
        Integer p = (Integer) criteri.get("priorita");

        if(descrizione != null && !descrizione.isBlank()){
            result.removeIf(a -> !a.getDescrizione().toLowerCase().contains(descrizione.toLowerCase()));
        }
        if (id != null) {
            result.removeIf(a -> a.getId() != id);
        }
        if (tipo != null) {
            result.removeIf(a -> a.getTipo() != getTipoByString(tipo));
        }

        if (data != null) {
            result.removeIf(a -> a.getDataInizio().isAfter(data) || a.getDataFine().isBefore(data)
            );
        }
        if (p != null) {
            result.removeIf(a -> a.getPriorita() != p);
        }
        return result;
    }


    @Override
    public List<Attivita> getTutteLeAttivita() {
        return gestoreAttivita.getTutteLeAttivita();
    }

    @Override
    public void caricaDaDB() {
        gestoreAttivita.caricaDaDB();
    }
    
}
