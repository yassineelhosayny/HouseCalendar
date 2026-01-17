//Not done yet
package dominio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dataBase.DAO.AttivitaDAO;
import observer.Osservatore;
import observer.SoggettoOsservabile;
import strategia.StrategiaOrdinamento;
import strategia.StrategiaOrdinamentoPerData;
import strategia.StrategiaOrdinamentoPerNome;
import strategia.StrategiaOrdinamentoPerPriorita;

//classe che gestisce attività e notifiche (da implementare Observer se necessario) da uno o più utenti
//usa pattern singleton per garantire una sola istanza
//tre pattern fino ad ora: singleton e observe efactory
//=> contiene la lista attività!! tipo RAM accesso veloce? + singleton + notify observer
public class GestoreAttivita implements SoggettoOsservabile {

    private static GestoreAttivita instance = new GestoreAttivita();     // Singleton
    private List<Osservatore> osservatori = new ArrayList<>();
    private List<Attivita> listaAttivita = new ArrayList<>();
    //strategia corrente di ordinamento =->strategy Pattern
    private StrategiaOrdinamento strategiaOrdinamento;
    //vuoto
    private GestoreAttivita() {
        // Costruttore privato per il pattern Singleton
    }

    //crea istanza in caso non esiste
    public static GestoreAttivita getInstance() {
        return instance;
    }

    public void aggiungiAttivita(Attivita a) {
                //aggiungere la attività alla lista+ chiamare notificaOsservatori()

            int id =  AttivitaDAO.aggiungiAttivita(a);  // aggiungere L'attivita alla data base 
            if (id <= 0) {
                throw new IllegalStateException("Errore inserimento nel DB");
            }
            a.setId(id); //set del id generato dal database
            listaAttivita.add(a);
            notificaOsservatori();
        }

    public void rimuoviAttivita(Attivita a) {
            if (a == null) {
                throw new IllegalArgumentException("Attività nulla");
            }

            //prova a rimuovere dal DB(controlla connesione con db)
            boolean success = AttivitaDAO.rimuoviAttivitaById(a.getId());
            if (!success) {
                    throw new IllegalStateException("Errore rimozione dal DB");
                }
            // rimuovi dalla lista
            listaAttivita.remove(a);

            notificaOsservatori();
    }

    public void modificaAttivita(int id, Attivita nuovaAttivita) {
        if (nuovaAttivita == null) 
            throw new IllegalArgumentException("Attività nulla");
        
        Attivita esistente = getAttivitaById(id);
        if (esistente == null) {
            throw new IllegalArgumentException("Attività non esistente (RAM)");
        }
        //prova ad aggiornare il DB, ma non bloccare tutto se fallisce (o gestisci l'errore)
        boolean success = AttivitaDAO.updateAttivita(nuovaAttivita);
        if (!success) 
            throw new IllegalStateException("Errore aggiornamento DB");

        // sostituisci nella listaAttivita
        for (int i = 0; i < listaAttivita.size(); i++) {
            if (listaAttivita.get(i).getId() == id) {
                listaAttivita.set(i, nuovaAttivita);
                break;
            }
        }

        notificaOsservatori();
    }


    public List<Attivita> getTutteLeAttivita() {
        return new ArrayList<>(listaAttivita);
    }

    public Attivita getAttivitaById(int id) {
        //cercare attività per ID
        for (Attivita a : listaAttivita) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    public void setStrategiaOrdinamento(StrategiaOrdinamento strategia) {
        //memorizzare strategia scelta
        this.strategiaOrdinamento = strategia;
    }

    public void ordinapernome() { 
        //ordinare lista con strategia scelta
        StrategiaOrdinamento strategia = new StrategiaOrdinamentoPerNome();
        setStrategiaOrdinamento(strategia);
        strategiaOrdinamento.ordina(listaAttivita);
        notificaOsservatori();
    }

    public void ordinaperdata() { 
          //ordinare lista con strategia scelta
        StrategiaOrdinamento strategia = new StrategiaOrdinamentoPerData();
        setStrategiaOrdinamento(strategia);
        strategiaOrdinamento.ordina(listaAttivita);
        notificaOsservatori();
    }

    public void ordinaperpriorita() { 
            //ordinare lista con strategia scelta
        StrategiaOrdinamento strategia = new StrategiaOrdinamentoPerPriorita();
        setStrategiaOrdinamento(strategia);
        strategiaOrdinamento.ordina(listaAttivita);
        notificaOsservatori();
    }

    public List<Attivita> cercapernome(String nome) {
        List<Attivita> risultati = new ArrayList<>();
        for (Attivita a : listaAttivita) {
            if (a.getDescrizione().toLowerCase().contains(nome.toLowerCase())) {
                risultati.add(a);
            }
        }
        return risultati;
     }

    public List<Attivita> cercapertipo(String tipo) { 
        List<Attivita> risultati = new ArrayList<>();
        for (Attivita a : listaAttivita) {
           if (a.getTipo().name().equalsIgnoreCase(tipo)){
                risultati.add(a);
            }
        }
        return risultati;
    }

    public List<Attivita> cercaperdata(LocalDateTime inizio, LocalDateTime fine) { 
    List<Attivita> risultati = new ArrayList<>();
        if (inizio == null || fine == null)
             return risultati;
        if (inizio.isAfter(fine))
             return risultati;

    for (Attivita a : listaAttivita) {
        if (a.getDataInizio() == null || a.getDataFine() == null) continue;

        boolean islogical =
                !a.getDataFine().isBefore(inizio) &&
                !a.getDataInizio().isAfter(fine);

        if (islogical) {
            risultati.add(a);
        }
    }

    return risultati;
    }

    public List<Attivita> cercaperpriorita(int p) {
        List<Attivita> risultati = new ArrayList<>();
        for (Attivita a : listaAttivita) {
            if (a.getPriorita() == p) {
                risultati.add(a);
            }
        }
        return risultati;
     }

    public void caricaDaDB() {
        //leggere dati da db+  notificaOsservatori()
        listaAttivita = AttivitaDAO.getAllAttivita();
        notificaOsservatori();
     
    }

    //metodi a implementatire per gestire attività e notifiche 
    @Override
    public void aggiungiOsservatore(Osservatore o) {
       if (o == null) return;
       if (!osservatori.contains(o)) {
           osservatori.add(o);
       }
    }

    @Override
    public void rimuoviOsservatore(Osservatore o) {
         osservatori.remove(o);
    }

    @Override
    public void notificaOsservatori() {
       for (Osservatore o : osservatori) {
        o.aggiorna();
    }
    }


         //funzione stampa attivita per i test prima del implimentazione del gui
    public void stampatutteleattivita() {
        if (listaAttivita.isEmpty()) {
            System.out.println("Nessuna attività presente.");
            return;
        }
        
        System.out.println("=== LISTA ATTIVITÀ ===");
        for (Attivita a : listaAttivita) {
            System.out.println(a.toString());
        }
        System.out.println("======================");
     }

}
