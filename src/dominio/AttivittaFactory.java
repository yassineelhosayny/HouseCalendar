package dominio;

public class AttivittaFactory {
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
