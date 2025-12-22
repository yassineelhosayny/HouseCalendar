package dominio;

import java.time.LocalDateTime;

//classe per AttivitaDomestica che estende Attivita
public class AttivitaDomestica extends Attivita{
	
	private String stanzaCasa;

    public AttivitaDomestica(
    		String descrizione, 
            LocalDateTime dataInizio, 
    		LocalDateTime dataFine, 
    		LocalDateTime dataNotifica,
            int priorita, 
            Utente utenteAssegnato, 
            boolean attivitaPrivata,
            String stanzaCasa) {
    	
        super(descrizione, TipoAttivita.DOMESTICA, dataInizio,dataFine, dataNotifica, priorita, utenteAssegnato, attivitaPrivata);
        this.stanzaCasa = stanzaCasa; 		//cucina, camera da letto, salotto, bagno, ecc...
    }
    //attributi specifici per AttivitaDomestica (se necessari)

    @Override
    public String getDettagli() {	
        return "Attività Domestica\n" +
        "\nDescrizione: " + descrizione +
        "\nStanza: " + stanzaCasa +
        "\ndata Inizio: " + dataInizio +
        "\ndata fine: " + dataFine +
        "\nNotifica: " + dataNotifica +
        "\nPriorità: " + priorita     +
        "\nUtente: "   + utenteAssegnato;
    }

   public String getStanzaCasa() {
	   return stanzaCasa;
   }
    
   public void setStanzaCasa(String stanza) {
	   this.stanzaCasa = stanza;
   }
    
}
