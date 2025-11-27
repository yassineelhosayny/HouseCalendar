package dominio;

import java.time.LocalDate;

//classe per AttivitaStudio che estende Attivita
public class AttivitaStudio extends Attivita{

    public AttivitaStudio(String descrizione, TipoAttivita tipo, LocalDate dataScadenza, LocalDate dataNotifica,
            int priorita, Utente utenteAssegnato, boolean attivitaPrivata) {
        super(descrizione, tipo, dataScadenza, dataNotifica, priorita, utenteAssegnato, attivitaPrivata);
        //TODO Auto-generated constructor stub
    }
    //attributi specifici per AttivitaStudio (se necessari)

    //costruttore 
    //il suo costrottore chiama super() della classe Attivita

    //metodi specifici per AttivitaStudio (se necessari)

}
