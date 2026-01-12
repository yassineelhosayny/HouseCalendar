package dataBase.pojo;
import java.time.LocalDateTime;
public class AttivitaPojo {
    private int id;
    private String descrizione;
    private String tipo;            //TipoAttivita salvato come String
    private LocalDateTime dataInizio;
    private LocalDateTime dataFine;
    private LocalDateTime dataNotifica;
    private int priorita;
    private boolean attivitaPrivata;
    private String context;          
    private String utenteEmail;      // FK per identificare utnete

    // costruttore vuoto (OBBLIGATORIO per JDBC)
    public AttivitaPojo() {}

    // costruttore completo (opzionale ma comodo)
    public AttivitaPojo(String descrizione, String tipo,LocalDateTime dataInizio, LocalDateTime dataFine,LocalDateTime dataNotifica, int priorita,boolean attivitaPrivata, String context,String utenteEmail){
        this.descrizione = descrizione;
        this.tipo = tipo;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.dataNotifica = dataNotifica;
        this.priorita = priorita;
        this.attivitaPrivata = attivitaPrivata;
        this.context = context;
        this.utenteEmail = utenteEmail;
    }

    //getter e setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDateTime dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDateTime getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDateTime dataFine) {
        this.dataFine = dataFine;
    }

    public LocalDateTime getDataNotifica() {
        return dataNotifica;
    }

    public void setDataNotifica(LocalDateTime dataNotifica) {
        this.dataNotifica = dataNotifica;
    }

    public int getPriorita() {
        return priorita;
    }

    public void setPriorita(int priorita) {
        this.priorita = priorita;
    }

    public boolean isAttivitaPrivata() {
        return attivitaPrivata;
    }

    public void setAttivitaPrivata(boolean attivitaPrivata) {
        this.attivitaPrivata = attivitaPrivata;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getUtenteEmail() {
        return utenteEmail;
    }

    public void setUtenteEmail(String utenteEmail) {
        this.utenteEmail = utenteEmail;
    }
}
