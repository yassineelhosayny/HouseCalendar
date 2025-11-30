package dominio;

import java.time.LocalDate;

//classe per AttivitaStudio che estende Attivita
public class AttivitaStudio extends Attivita{

    public AttivitaStudio(String descrizione, TipoAttivita tipo,int id, LocalDate dataScadenza, LocalDate dataNotifica,
            int priorita, Utente utenteAssegnato, boolean attivitaPrivata) {
                //super chiama il costruttore madre
        super(descrizione, tipo, id, dataScadenza, dataNotifica, priorita, utenteAssegnato, attivitaPrivata);
        
    }
    //attributi specifici per AttivitaStudio (se necessari)

    @Override
    public String getDettagli() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDettagli'");
    }

    //costruttore 
    //il suo costrottore chiama super() della classe Attivita

    //metodi specifici per AttivitaStudio (se necessari)

}
