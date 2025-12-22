package dominio;


import java.time.LocalDateTime;

//classe per AttivitaStudio che estende Attivita
public class AttivitaStudio extends Attivita{
	
	private String materia;
	
    public AttivitaStudio(String descrizione,LocalDateTime dataInizio, LocalDateTime dataFine, LocalDateTime dataNotifica,
            int priorita, Utente utenteAssegnato, boolean attivitaPrivata, String materia) {
                //super chiama il costruttore madre
    	
    	
        super(descrizione, TipoAttivita.STUDIO, dataInizio,dataFine, dataNotifica, priorita, utenteAssegnato, attivitaPrivata);
        
        this.materia = materia;
    }
    //attributi specifici per AttivitaStudio (se necessari)


    @Override
    public String getDettagli() {	
        return "Attività Studio\n" +
        "\nDescrizione: " + descrizione +
        "\nMateria: " + materia +
        "\ndata Inizio: " + dataInizio +
        "\ndata fine: " + dataFine +
        "\nNotifica: " + dataNotifica +
        "\nPriorità: " + priorita     +
        "\nUtente: "   + utenteAssegnato;
    }

   public String getMateria() {
	   return materia;
   }
    
   public void setMateria(String materia) {
	   this.materia = materia;
   }
    
}
