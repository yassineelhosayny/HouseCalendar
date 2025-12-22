package dominio;

import java.time.LocalDateTime;

//classe per AttivitaSpesa che estende Attivita
public class AttivitaSpesa extends Attivita {

	private String negozio;
	
    public AttivitaSpesa(String descrizione,LocalDateTime dataInizio, LocalDateTime dataFine, LocalDateTime dataNotifica,
            int priorita, Utente utenteAssegnato, boolean attivitaPrivata, String negozio) {
        super(descrizione, TipoAttivita.SPESA, dataInizio,dataFine, dataNotifica, priorita, utenteAssegnato, attivitaPrivata);
        
        
        this.negozio = negozio;
    }
    //attributi specifici per AttivitaSpesa (se necessari)

    @Override
    public String getDettagli() {	
        return "Attività Spesa\n" +
        "\nDescrizione: " + descrizione +
        "\nSupermercato: " + negozio +
        "\ndata Inizio: " + dataInizio +
        "\ndata fine: " + dataFine +
        "\nNotifica: " + dataNotifica +
        "\nPriorità: " + priorita     +
        "\nUtente: "   + utenteAssegnato;
    }

   public String getNegozio() {
	   return negozio;
   }
    
   public void setNegozio(String negozio) {
	   this.negozio = negozio;
   }
   
}
