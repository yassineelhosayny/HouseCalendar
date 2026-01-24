package gui.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import dominio.Attivita;
import gestione.AttivitaGestioneImp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;



public class MainLayoutController {

    @FXML private VBox contenitoreSchede;      // deve combaciare con fx:id="contenitoreSchede"
    @FXML private TextField campoRicerca;      // deve combaciare con fx:id="campoRicerca"
    @FXML private Label etichettaMese;         // deve combaciare con fx:id="etichettaMese"
    @FXML private GridPane grigliaCalendario;  // deve combaciare con fx:id="grigliaCalendario"
    @FXML private ToggleGroup gruppoFiltri;    // deve combaciare con fx:id="gruppoFiltri"

    private final AttivitaGestioneImp gestione = new AttivitaGestioneImp();

    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter formatoOra  = DateTimeFormatter.ofPattern("HH:mm");
    private YearMonth meseCorrente = YearMonth.now(); // mese visualizzato
    private LocalDate giornoSelezionato = null;       // giorno cliccato (opzionale)

    private final DateTimeFormatter formatoMese = DateTimeFormatter.ofPattern("MMMM yyyy");


    @FXML
    private void initialize() {
        try {
            gestione.caricaDaDB();
            aggiornaCalendario();
            aggiornaLista();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Errore caricamento attività: " + e.getMessage()).showAndWait();
        }
    }

    private void aggiornaLista() {
        contenitoreSchede.getChildren().clear();

        List<Attivita> lista = gestione.getTutteLeAttivita();
        if (lista.isEmpty()) {
            contenitoreSchede.getChildren().add(new Label("Nessuna attività."));
            return;
        }

        for (Attivita a : lista) {
            contenitoreSchede.getChildren().add(creaRigaAttivita(a));
        }
    }

    private HBox creaRigaAttivita(Attivita a) {
        Label titolo = new Label(a.getDescrizione());
        titolo.setMinWidth(240);

        Label tipo = new Label(a.getTipo().name());
        tipo.setMinWidth(120);

        Label data = new Label(a.getDataInizio().toLocalDate().format(formatoData));
        data.setMinWidth(110);

        Label ora = new Label(a.getDataInizio().toLocalTime().format(formatoOra));
        ora.setMinWidth(80);

        Label assegnato = new Label(a.getUtenteAssegnato().getNome());
        assegnato.setMinWidth(140);

        HBox riga = new HBox(12, titolo, tipo, data, ora, assegnato);
        riga.setStyle("-fx-padding: 10; -fx-background-color: white; -fx-background-radius: 10;");
        return riga;
    }
    

    // Top bar
    @FXML private void azioneHome() {}
    @FXML private void azioneNotifiche() {}
    @FXML private void azioneProfilo() {}
    @FXML private void azioneLogout() {}

    // Filtri
    @FXML private void filtroTutte() { aggiornaLista(); }
    @FXML private void filtroPersonali() { aggiornaLista(); }
    @FXML private void filtroCondivise() { aggiornaLista(); }

    // Calendario
    @FXML
    private void mesePrecedente() {
        meseCorrente = meseCorrente.minusMonths(1);
        aggiornaCalendario();
    }
    @FXML
    private void meseSuccessivo() {
        meseCorrente = meseCorrente.plusMonths(1);
        aggiornaCalendario();
    }

    // Ricerca
    @FXML private void cercaPerData() {}
    @FXML private void cercaPerNome() {}
    @FXML private void cercaPerTipo() {}

    // CRUD
    @FXML private void aggiungiAttivita() {}


    private void aggiornaCalendario() {
        // aggiorna label mese
        etichettaMese.setText(capitalize(formatoMese.format(meseCorrente.atDay(1))));

        // pulisci griglia
        grigliaCalendario.getChildren().clear();
        grigliaCalendario.getColumnConstraints().clear();
        grigliaCalendario.getRowConstraints().clear();

        // 7 colonne uguali
        for (int c = 0; c < 7; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            cc.setPercentWidth(100.0 / 7.0);
            grigliaCalendario.getColumnConstraints().add(cc);
        }

        // 1 riga header + 6 righe settimane
        for (int r = 0; r < 7; r++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            rc.setFillHeight(true);
            grigliaCalendario.getRowConstraints().add(rc);
        }

        // intestazione giorni (L M M G V S D)
        String[] giorni = {"L", "M", "M", "G", "V", "S", "D"};
        for (int c = 0; c < 7; c++) {
            Button header = new Button(giorni[c]);
            header.setDisable(true);
            header.setMaxWidth(Double.MAX_VALUE);
            header.setMaxHeight(Double.MAX_VALUE);
            GridPane.setHgrow(header, Priority.ALWAYS);
            GridPane.setVgrow(header, Priority.ALWAYS);
            header.setStyle("-fx-opacity: 1; -fx-font-weight: bold;");
            grigliaCalendario.add(header, c, 0);
        }

        LocalDate primoGiorno = meseCorrente.atDay(1);
        int giorniNelMese = meseCorrente.lengthOfMonth();

        // offset: in Java DayOfWeek MONDAY=1 ... SUNDAY=7
        int offset = primoGiorno.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        if (offset < 0) offset += 7;

        int riga = 1;
        int colonna = offset;

        for (int giorno = 1; giorno <= giorniNelMese; giorno++) {
            LocalDate data = meseCorrente.atDay(giorno);

            ToggleButton btn = new ToggleButton(String.valueOf(giorno));
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setMaxHeight(Double.MAX_VALUE);
            btn.setMinSize(34, 28);
            btn.setAlignment(Pos.CENTER);

            GridPane.setHgrow(btn, Priority.ALWAYS);
            GridPane.setVgrow(btn, Priority.ALWAYS);

            btn.setStyle(stileGiorno(data));

            btn.setOnAction(e -> {
                giornoSelezionato = data;
                aggiornaCalendario();
            });

            // evidenzia oggi
            if (data.equals(LocalDate.now())) {
                btn.setStyle(btn.getStyle() + " -fx-border-color: white; -fx-border-width: 1;");
            }

            // evidenzia selezione
            if (giornoSelezionato != null && data.equals(giornoSelezionato)) {
                btn.setSelected(true);
                btn.setStyle(btn.getStyle() + " -fx-background-color: rgba(255,255,255,0.35);");
            }

            grigliaCalendario.add(btn, colonna, riga);

            colonna++;
            if (colonna == 7) {
                colonna = 0;
                riga++;
            }
        }
}

private String stileGiorno(LocalDate data) {
    return "-fx-background-color: rgba(255,255,255,0.18);"
         + "-fx-text-fill: white;"
         + "-fx-background-radius: 8;";
}

private static String capitalize(String s) {
    if (s == null || s.isEmpty()) return s;
    return s.substring(0, 1).toUpperCase() + s.substring(1);
}


}
