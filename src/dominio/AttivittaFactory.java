package dominio;

import java.time.LocalDate;

//questa classe implementa il pattern factory per creare oggetti di tipo Attivita
//utile per creare oggetti di tipo attivita senza esporre la logica di creazione al client(senza usare "new" nella classe client)
public class AttivittaFactory {
    //ccodice facoty implementato automaticamente da vs, da verificare e modificare dopo
    public static Attivita crea(String descrizione, TipoAttivita tipo,LocalDate dataScadenza, LocalDate dataNotifica, int priorita, Utente utenteAssegnato, boolean attivitaPrivata) {
        switch (tipo) {
            case STUDIO: return new AttivitaStudio(descrizione, tipo, dataScadenza, dataNotifica, priorita, utenteAssegnato, attivitaPrivata);
            case SPESA: return new AttivitaSpesa(descrizione, tipo, dataScadenza, dataNotifica, priorita, utenteAssegnato, attivitaPrivata);
            case DOMESTICO: return new AttivitaDomestica(descrizione, tipo, dataScadenza, dataNotifica, priorita, utenteAssegnato, attivitaPrivata);
            default: throw new IllegalArgumentException("Tipo non valido");
        }
    }
}
