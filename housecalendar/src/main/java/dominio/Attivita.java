package dominio;


import java.time.LocalDateTime;


public abstract class Attivita {
    
    //attributi essenziali per una attivita PROTECTED
    protected String descrizione;
    protected TipoAttivita tipo;
    protected int id;
    protected LocalDateTime dataInizio;
    protected LocalDateTime dataFine;
    protected LocalDateTime dataNotifica;
    protected int priorita;
    protected boolean completato;
    protected Utente utenteAssegnato;
    protected boolean attivitaPrivata; //visibile solo all'utente assegnato
    

    //costrutorre generale per Attivita
    public Attivita(String descrizione, TipoAttivita tipo, LocalDateTime dataInizio, LocalDateTime dataFine, LocalDateTime dataNotifica, int priorita, Utente utenteAssegnato, boolean attivitaPrivata) {
    	
        
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
        
        if (dataInizio == null || dataFine == null) {
            throw new IllegalArgumentException("Date obbligatorie");
        }

        
        this.descrizione = descrizione;
        this.tipo = tipo;
        this.id=-1; //default prima il salva sulla DB e la generazione di Id
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
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

    public LocalDateTime getDataInizio() {
         return this.dataInizio;
    }
    public LocalDateTime getDataFine() {
         return this.dataFine;
    }

    public TipoAttivita getTipo() {
        return this.tipo;
    }
    public  int getPriorita(){
        return this.priorita;
    };
    
    public boolean isCompletato() {
        return this.completato;
    }

    public Utente getUtenteAssegnato() {
        return this.utenteAssegnato;
    }
    
    public boolean isAttivitaPrivata() {
        return this.attivitaPrivata;
    } 
    
    
    public LocalDateTime getDataNotifica() {
        return this.dataNotifica;
    }
     
    //setter
    public void setId(int id){
        this.id = id;
    }
    public void setDescrizione(String descrizione) {
    	
    	if(descrizione == null || descrizione.isEmpty()) {
            throw new IllegalArgumentException("la descrizione non può essere vuota");
    	}
    	
    	this.descrizione = descrizione;
    }
    
    
    public void setDataFine(LocalDateTime dataFine) {
        if(!dataFine.isAfter(this.dataInizio)) {
            throw new IllegalArgumentException("la data di scadenza non può essere prima della data del'inizio.");
        }
        this.dataFine = dataFine;
    }

    public void setDataInizio(LocalDateTime dataInizio) {
    if (dataInizio == null) {
        throw new IllegalArgumentException("data inizio nulla");
    }

    if (this.dataFine != null && dataInizio.isAfter(this.dataFine)) {
        throw new IllegalArgumentException("la data inizio deve essere prima della data fine.");
    }

    this.dataInizio = dataInizio;
}

    
    public void setDataNotifica(LocalDateTime dataNotifica) {
        if(!dataNotifica.isAfter(LocalDateTime.now()) || dataNotifica.isAfter(this.dataInizio)) {
            throw new IllegalArgumentException("La data di notifica deve essere valida e non dopo la data dell' evento.");
        }
        this.dataNotifica = dataNotifica;
    }

    public void setPriorita(int priorita) {
        if(priorita < 1 || priorita > 3) {
            throw new IllegalArgumentException("La priorità deve essere compresa tra 1 e 3.");
        }
        this.priorita = priorita;
    }
    
    public void setCompletato(boolean completato) {
    	this.completato = completato;
    }
    public void setTipo (TipoAttivita tipo) {
    	this.tipo = tipo;
    }

    public void setUtenteAssegnato(Utente utenteAssegnato) {
        if(utenteAssegnato == null) {
            throw new IllegalArgumentException("L'utente assegnato non può essere nullo.");
        }
        this.utenteAssegnato = utenteAssegnato;
    }

    public void setAttivitaPrivata(boolean attivitaPrivata) {
        this.attivitaPrivata = attivitaPrivata;
    }
    public boolean isBetweenDataOccupata(Attivita nuova){
        //salvata
        LocalDateTime salvataInizia = this.getDataInizio();
        LocalDateTime salvataFine = this.getDataFine();
        //nuova
        LocalDateTime nuovaInizia = nuova.getDataInizio();
        LocalDateTime nuovaFine = nuova.getDataFine();

        return !salvataInizia.isAfter(nuovaFine)&& !salvataFine.isBefore(nuovaInizia);
    }

    public boolean isScaduta() {
        if(LocalDateTime.now().isAfter(this.dataFine)){
            return true;
        }
        return false;
    }

        public abstract String getDettagli();   //ogni attivita ha attributi diversi, obbligando le classi ad implementarlo(polimorfismo)

    @Override
    public String toString() {
        return "Attivita [descrizione=" + descrizione + ", tipo=" + tipo + ", id=" + id + ", dataInizio="
                + dataInizio + ", dataFine="+ dataFine + ", dataNotifica=" + dataNotifica + ", priorita=" + priorita + ", completato="
                + completato + ", utenteAssegnato=" + utenteAssegnato + ", attivitaPrivata=" + attivitaPrivata + "]";
    }


    




  

}
