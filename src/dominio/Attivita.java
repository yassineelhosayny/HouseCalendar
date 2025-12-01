package dominio;

import java.time.LocalDate;


public abstract class Attivita {
    
    //attributi essenziali per una attivita PROTECTED
    protected String descrizione;
    protected TipoAttivita tipo;
    protected int id;
    private static   int contatoreId = 0;
    protected LocalDate dataScadenza;
    protected LocalDate dataNotifica;
    protected int priorita;
    protected boolean completato;
    protected Utente utenteAssegnato;
    protected boolean attivitaPrivata; //visibile solo all'utente assegnato
    

    //blocco statico per gestione id univoci
     {
        this.id = ++contatoreId;
    }

    //costrutorre generale per Attivita
    public Attivita(String descrizione, TipoAttivita tipo, LocalDate dataScadenza, LocalDate dataNotifica, int priorita, Utente utenteAssegnato, boolean attivitaPrivata) {
        if(dataNotifica.isAfter(dataScadenza) || !dataScadenza.isAfter(LocalDate.now()) || !dataNotifica.isAfter(LocalDate.now() )){
            throw new IllegalArgumentException("La data di notifica non può essere successiva alla data di scadenza.");
        }
        if(priorita < 1 || priorita > 3){
            throw new IllegalArgumentException("La priorità deve essere compresa tra 1 e 3.");
        }
        if(descrizione == null || descrizione.isEmpty()){
            throw new IllegalArgumentException("La descrizione non può essere vuota.");
        }
        if(tipo == null){
            throw new IllegalArgumentException("Il tipo di attività non può essere nullo.");
        }
        if(utenteAssegnato == null){
            throw new IllegalArgumentException("L'utente assegnato non può essere nullo.");
        }  
        this.descrizione = descrizione;
        this.tipo = tipo;
        this.dataScadenza = dataScadenza;
        this.dataNotifica = dataNotifica;
        this.priorita = priorita;
        this.completato = false; //inizialmente non completato
        this.utenteAssegnato = utenteAssegnato;
        this.attivitaPrivata = attivitaPrivata;
    }


    //METODI PER ATTIVITA
    //1/ GETER per attributi essenziali, 
    //2/ altri metodi utili per Attivita
     public int getId() { 
        return this.id;
    }

    public String getDescrizione() {
        return this.descrizione;
    }

    public LocalDate getDataScadenza() {
         return this.dataScadenza;
    }

    public TipoAttivita getTipo() {
        return this.tipo;
    }
    public boolean isCompletato() {
        return this.completato;
    }

    public Utente getUtenteAssegnato() {
        return this.utenteAssegnato;
    }
    public boolean isAttivitaPrivata() {
        return this.attivitaPrivata;
    }   
    public LocalDate getDataNotifica() {
        return this.dataNotifica;
    }
     
    //metodi ASTRATTI: ogni attività deve definirlo diversamente *****************************
    public abstract String getDettagli();  // ogni sottoclasse lo implementa


    public boolean isScaduta() {
        if(LocalDate.now().isAfter(this.dataScadenza)){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Attivita [descrizione=" + descrizione + ", tipo=" + tipo + ", id=" + id + ", dataScadenza="
                + dataScadenza + ", dataNotifica=" + dataNotifica + ", priorita=" + priorita + ", completato="
                + completato + ", utenteAssegnato=" + utenteAssegnato + ", attivitaPrivata=" + attivitaPrivata + "]";
    }

}
