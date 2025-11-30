package dominio;

import java.time.LocalDate;

//classe per AttivitaSpesa che estende Attivita
public class AttivitaSpesa extends Attivita {

    public AttivitaSpesa(String descrizione, TipoAttivita tipo,int id, LocalDate dataScadenza, LocalDate dataNotifica,
            int priorita, Utente utenteAssegnato, boolean attivitaPrivata) {
        super(descrizione, tipo, id, dataScadenza, dataNotifica, priorita, utenteAssegnato, attivitaPrivata);
        //TODO Auto-generated constructor stub
    }
    //attributi specifici per AttivitaSpesa (se necessari)

    @Override
    public String getDettagli() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDettagli'");
    }

    //costruttore 
    //il suo costrottore chiama super() della classe Attivita

    //metodi specifici per AttivitaSpesa (se necessari)
}
