package gui.controller;

import dominio.Attivita;
import dominio.AttivitaDomestica;
import dominio.AttivitaSpesa;
import dominio.AttivitaStudio;
import dominio.TipoAttivita;

import dominio.Utente;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import javafx.scene.control.Label;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

public class FormAttivitaController {

    @FXML private Label titoloDialogo;
    @FXML private TextField campoDescrizione;
    @FXML private ComboBox<TipoAttivita> comboTipo;
    @FXML private DatePicker dataInizio;
    @FXML private TextField oraInizio;
    @FXML private DatePicker dataFine;
    @FXML private TextField oraFine;
    @FXML private Spinner<Integer> spinnerPriorita;
    @FXML private ComboBox<Utente> comboUtente;
    @FXML private CheckBox checkPrivata;
    @FXML private TextField campoContext;
    @FXML private DatePicker dataNotifica;
    @FXML private TextField oraNotifica;

    private final DateTimeFormatter formatoOra = DateTimeFormatter.ofPattern("HH:mm");
    private Stage stage;
    private Map<String, Object> risultato;

    @FXML
    private void initialize() {
        if (comboTipo != null) {
            comboTipo.setItems(FXCollections.observableArrayList(TipoAttivita.values()));
        }
        if (spinnerPriorita != null) {
            spinnerPriorita.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 3, 1));
        }
        if (comboUtente != null) {
            comboUtente.setConverter(new StringConverter<>() {
                @Override
                public String toString(Utente utente) {
                    if (utente == null) {
                        return "";
                    }
                    return utente.getNome() + " (" + utente.getEmail() + ")";
                }

                @Override
                public Utente fromString(String string) {
                    return null;
                }
            });
        }
        if (oraInizio != null) {
            oraInizio.setPromptText("HH:mm");
        }
        if (oraFine != null) {
            oraFine.setPromptText("HH:mm");
        }
        if (oraNotifica != null) {
            oraNotifica.setPromptText("HH:mm (opzionale)"); }
        if (campoContext != null)
        {
            campoContext.setPromptText("Contesto/nota (opzionale)");
        }
    }

    public void impostaStage(Stage stage) {
        this.stage = stage;
    }
    public void impostaDati(String titolo, Attivita attivita, List<Utente> utenti) {
        if (titoloDialogo != null && titolo != null) {
            titoloDialogo.setText(titolo);
        }
        if (comboUtente != null) {
            comboUtente.setItems(FXCollections.observableArrayList(utenti));
        }
        if (attivita != null) {
            campoDescrizione.setText(attivita.getDescrizione());
            comboTipo.setValue(attivita.getTipo());
            dataInizio.setValue(attivita.getDataInizio().toLocalDate());
             oraInizio.setText(attivita.getDataInizio().toLocalTime().format(formatoOra));
            dataFine.setValue(attivita.getDataFine().toLocalDate());
            oraFine.setText(attivita.getDataFine().toLocalTime().format(formatoOra));
            spinnerPriorita.getValueFactory().setValue(attivita.getPriorita());
            checkPrivata.setSelected(attivita.isAttivitaPrivata());
            campoContext.setText(estraiContext(attivita));
            if (attivita.getDataNotifica() != null) {
                dataNotifica.setValue(attivita.getDataNotifica().toLocalDate());
                oraNotifica.setText(attivita.getDataNotifica().toLocalTime().format(formatoOra));
            }
            comboUtente.setValue(attivita.getUtenteAssegnato());
        } else if(utenti != null && !utenti.isEmpty()){
            comboUtente.setValue(utenti.get(0));
        }
    }

    public void impostaUtenteCorrente(Utente utente) {
        if (comboUtente != null && utente != null) {
            comboUtente.setValue(utente);
            comboUtente.setDisable(true);
        }
    }
    public Map<String, Object> getRisultato() {
        return risultato;
    }

    @FXML
    private void salva() {
        try {
            risultato = creaParametri();
            chiudi();
        } catch (IllegalArgumentException ex) {
            mostraErrore(ex.getMessage());
        }
    }

    @FXML
    private void annulla() {
        risultato = null;
        chiudi();
    }

    private void chiudi() {
        if (stage != null) {
            stage.close();
        }
    }

    private Map<String, Object> creaParametri() {
        String descrizione = campoDescrizione.getText();
        if (descrizione == null || descrizione.isBlank()) {
            throw new IllegalArgumentException("La descrizione e obbligatoria.");
        }
        if (comboTipo.getValue() == null) {
            throw new IllegalArgumentException("Seleziona un tipo di attivita.");
        }
        LocalDateTime inizio = combinaDataOra(dataInizio.getValue(), oraInizio.getText(), "inizio");
        LocalDateTime fine = combinaDataOra(dataFine.getValue(), oraFine.getText(), "fine");
        LocalDateTime notifica = combinaDataOraOpzionale(dataNotifica.getValue(), oraNotifica.getText());
        if (comboUtente.getValue() == null) {
            throw new IllegalArgumentException("Seleziona un utente assegnato.");
        }

        Map<String, Object> parametri = new HashMap<>();
        parametri.put("descrizione", descrizione.trim());
        parametri.put("tipo", comboTipo.getValue());
        parametri.put("dataInizio", inizio);
        parametri.put("dataFine", fine);
        parametri.put("dataNotifica", notifica);
        parametri.put("priorita", spinnerPriorita.getValue());
        parametri.put("utenteAssegnato", comboUtente.getValue());
        parametri.put("attivitaPrivata", checkPrivata.isSelected());
        String valoreContext = campoContext.getText();
        parametri.put("context", valoreContext == null || valoreContext.isBlank() ? null : valoreContext.trim());
        return parametri;
    }

    private LocalDateTime combinaDataOra(LocalDate data, String ora, String etichetta) {
        if (data == null) {
            throw new IllegalArgumentException("La data " + etichetta + " e obbligatoria.");
        }
        if (ora == null || ora.isBlank()) {
            throw new IllegalArgumentException("L'orario di " + etichetta + " e obbligatorio.");
        }
        try {
            LocalTime tempo = LocalTime.parse(ora.trim(), formatoOra);
            return LocalDateTime.of(data, tempo);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Formato orario " + etichetta + " non valido. Usa HH:mm.");
        }
    }

    private LocalDateTime combinaDataOraOpzionale(LocalDate data, String ora) {
        if (data == null && (ora == null || ora.isBlank())) {
            return null;
        }
        if (data == null) {
            throw new IllegalArgumentException("Se inserisci l'orario notifica, serve anche la data.");
        }
        if (ora == null || ora.isBlank()) {
            throw new IllegalArgumentException("Se inserisci la data notifica, serve anche l'orario.");
        }
        try {
            LocalTime tempo = LocalTime.parse(ora.trim(), formatoOra);
            return LocalDateTime.of(data, tempo);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Formato orario notifica non valido. usa HH:mm.");
        }
    }

    private String estraiContext(Attivita attivita) {
        if (attivita instanceof AttivitaStudio studio) {
            return studio.getMateria();
        }
        if (attivita instanceof AttivitaSpesa spesa) {
            return spesa.getNegozio();
        }
        if (attivita instanceof AttivitaDomestica domestica) {
            return domestica.getStanzaCasa();
        }
        return null;
    }

    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}
