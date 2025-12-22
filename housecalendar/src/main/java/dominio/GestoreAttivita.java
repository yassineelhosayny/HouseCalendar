//Not done yet
package dominio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import observer.Osservatore;
import observer.SoggettoOsservabile;
import strategia.StrategiaOrdinamento;

//classe che gestisce attività e notifiche (da implementare Observer se necessario) da uno o più utenti
//usa pattern singleton per garantire una sola istanza
//tre pattern fino ad ora: singleton e observe efactory
//=> contiene la lista attività!! o usiamo Db? + singleton + notify observer
public class GestoreAttivita implements SoggettoOsservabile {

    private static GestoreAttivita instance = new GestoreAttivita();;     // Singleton
     private List<Osservatore> osservatori = new ArrayList<>();
    private List<Attivita> listaAttivita = new ArrayList<>();
    //strategia corrente di ordinamento (Strategy Pattern)
    private StrategiaOrdinamento strategiaOrdinamento;

    //vuoto, non cè nessun attributo da inizializzare
    private GestoreAttivita() {
        // Costruttore privato per il pattern Singleton
    }

    //crea istanza in caso non esiste (lo gia implementato <_>)
    public static GestoreAttivita getInstance() {
        if (instance == null) {
            instance = new GestoreAttivita();
        }
        return instance;
    }

    public void aggiungiAttivita(Attivita a) {
        //aggiungere la attività alla lista+ chiamare notificaOsservatori()
       
    }
    
    public void rimuoviAttivita(Attivita a) {
        //rimuovere attività+ chiamare notificaOsservatori()
    }

    public void modificaAttivita(int id, Attivita nuovaAttivita) {
        // modificare attività esistente +chiamare notificaOsservatori()
        
    }

    public List<Attivita> getTutteLeAttivita() {
        return this.listaAttivita;
    }

    public Attivita getAttivitaById(int id) {
        //cercare attività per ID
        return null;
    }

    public void setStrategiaOrdinamento(StrategiaOrdinamento strategia) {
        //memorizzare strategia scelta
    }

    public void ordinapernome() { 
        //ordinare lista con strategia scelta
    }

    public void ordinaperdata() { 
          //ordinare lista con strategia scelta
    }

    public void ordinaperpriorita() { 
                //ordinare lista con strategia scelta
    }

    public Attivita cercaperid(int id) { 
        //da implementare
        return null;
    }

    public List<Attivita> cercapernome(String nome) {
        //da implementare
        return null;
     }

    public List<Attivita> cercapertipo(String tipo) { 
        //da implementare
        return null;
    }

    public List<Attivita> cercaperdata(LocalDateTime data) { 
        //da implementare
        return null;
    }

    public List<Attivita> cercaperpriorita(int p) {
        //da implementare
        return null;
     }

    public void modificattivita(int id, Attivita nuova) {
        //da implementare
     }


    public void stampatutteleattivita() {

     }

    
    public void salvaSuFile(String percorso) {
        //serializzare su file
    }

    public void caricaDaFile(String percorso) {
        //leggere dati da file+  notificaOsservatori()
     
    }

    //metodi a implementatire per gestire attività e notifiche 
    @Override
    public void aggiungiOsservatore(Osservatore o) {
        //Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'aggiungiOsservatore'");
    }

    @Override
    public void rimuoviOsservatore(Osservatore o) {
        //Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rimuoviOsservatore'");
    }

    @Override
    public void notificaOsservatori() {
        //Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notificaOsservatori'");
    }
}
