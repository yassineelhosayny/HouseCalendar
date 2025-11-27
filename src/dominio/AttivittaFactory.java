package dominio;

//questa classe implementa il pattern factory per creare oggetti di tipo Attivita
//utile per creare oggetti di tipo attivita senza esporre la logica di creazione al client(senza usare "new" nella classe client)
public class AttivittaFactory {
    //ccodice facoty implementato automaticamente da vs, da verificare e modificare dopo
    public static Attivita createAttivita(String tipo) { 
        switch (tipo) {
            case "Spesa":
                return new AttivitaSpesa();
            case "Studio":
                return new AttivitaStudio();
            case "Domestica":
                return new AttivitaDomestica();
            default:
                throw new IllegalArgumentException("Tipo di attività non riconosciuto: " + tipo);
        }
    }
}
