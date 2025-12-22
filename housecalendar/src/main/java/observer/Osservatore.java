package observer;

//classe per l'osservatore nel pattern Observer
// riceve eventi GUI => chiama GestoreAttivita
//interfaccia, chi riceve notifiche
public interface Osservatore {
    void aggiorna();   // riceve la notifica e reagisce
}
