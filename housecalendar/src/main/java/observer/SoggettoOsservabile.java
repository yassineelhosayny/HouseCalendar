package observer;

//classe per il soggetto osservabile nel pattern Observer
// interfaccia, chi invia notifiche
public interface SoggettoOsservabile {

    void aggiungiOsservatore(Osservatore o);

    void rimuoviOsservatore(Osservatore o);
    
     void notificaOsservatori();
}
