package dominio;

import java.time.LocalDateTime;



//questa classe implementa il pattern factory per creare oggetti di tipo Attivita
//utile per creare oggetti di tipo attivita senza esporre la logica di creazione al client(senza usare "new" nella classe client)
public class AttivitaFactory {
    //ccodice facoty implementato automaticamente da vs, da verificare e modificare dopo
    public static Attivita crea(String descrizione, TipoAttivita tipo,LocalDateTime dataInizio, LocalDateTime dataFine, LocalDateTime dataNotifica, int priorita, Utente utenteAssegnato, boolean attivitaPrivata,String context,boolean notificata )  {
        switch (tipo) {
            case STUDIO: 
                        return new AttivitaStudio(descrizione, dataInizio,dataFine, dataNotifica, priorita, utenteAssegnato, attivitaPrivata, context,notificata);
            case SPESA: 
                        return new AttivitaSpesa(descrizione, dataInizio,dataFine, dataNotifica, priorita, utenteAssegnato, attivitaPrivata, context,notificata);
            case DOMESTICA: 
                        return new AttivitaDomestica(descrizione, dataInizio,dataFine, dataNotifica, priorita, utenteAssegnato, attivitaPrivata, context,notificata);
            default: 
                    throw new IllegalArgumentException("Tipo non valido");
        }
    }

}


