package gui.controller;

import dominio.Attivita;
import dominio.AttivitaDomestica;
import dominio.AttivitaSpesa;
import dominio.AttivitaStudio;
import dominio.Utente;

import gestione.AttivitaGestioneImp;
import gestione.SessioneUtente;

import gui.servizi.DialogoAttivita;
import gui.servizi.NotificaToast;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.geometry.Insets;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

public class MainLayoutController {

    private enum ModalitaRicerca {
        NOME,
        TIPO,
        DATA
    }

    private enum StatoAttivita {
        FUTURA,
        IN_CORSO,
        PASSATA,
        COMPLETATA
    }

    @FXML private VBox contenitoreSchede;
    @FXML private TextField campoRicerca;
    @FXML private Label etichettaMese;
    @FXML private GridPane grigliaCalendario;
    @FXML private ToggleGroup gruppoFiltri;
    @FXML private ToggleButton filtroTutte;
    @FXML private ToggleButton filtroPersonali;
    @FXML private ToggleButton filtroCondivise;
    @FXML private MenuButton menuRicerca;
    @FXML private MenuButton menuUtente;
    @FXML private Button btnAggiungi;

    private final AttivitaGestioneImp gestione = new AttivitaGestioneImp();

    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter formatoOra = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter formatoMese = DateTimeFormatter.ofPattern("MMMM yyyy");
    private final List<DateTimeFormatter> formatiDataRicerca = List.of(
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ISO_LOCAL_DATE
    );

    private YearMonth meseCorrente = YearMonth.now();
    private LocalDate giornoSelezionato;
    private ModalitaRicerca modalitaRicerca = ModalitaRicerca.NOME;
    private Timeline timerNotifiche;
    private Timeline timerStato;
    private Utente utenteCorrente;

    @FXML
    private void initialize() {
        try {
            gestione.caricaDaDB();
        } catch (Exception e) {
            mostraErrore("Errore caricamento attivita: " + e.getMessage());
        }

        if (utenteCorrente == null) {
            String email = SessioneUtente.getEmail();
            if (email != null && !email.isBlank()) {
                utenteCorrente = gestione.getUtentiDisponibili().stream()
                        .filter(u -> u.getEmail().equalsIgnoreCase(email))
                        .findFirst()
                        .orElse(null);
            }
        }

        preparaFiltri();
        preparaRicerca();
        preparaAzioniHeader();
        preparaBottoneAggiungi();
        configuraMenuUtente();

        aggiornaCalendario();
        aggiornaLista();

        Platform.runLater(this::avviaTimerNotifiche);
        Platform.runLater(this::avviaTimerStato);
    }

    private void preparaFiltri() {
        if (gruppoFiltri == null) {
            gruppoFiltri = new ToggleGroup();
        }
        if (filtroTutte != null) {
            filtroTutte.setToggleGroup(gruppoFiltri);
            filtroTutte.setSelected(true);
            filtroTutte.setOnAction(evento -> aggiornaLista());
        }
        if (filtroPersonali != null) {
            filtroPersonali.setToggleGroup(gruppoFiltri);
            filtroPersonali.setOnAction(evento -> aggiornaLista());
        }
        if (filtroCondivise != null) {
            filtroCondivise.setToggleGroup(gruppoFiltri);
            filtroCondivise.setOnAction(evento -> aggiornaLista());
        }
    }

    private void preparaRicerca() {
        if (menuRicerca != null) {
            menuRicerca.setText("Nome");
        }
        if (campoRicerca != null) {
            campoRicerca.setOnAction(evento -> applicaRicerca());
        }
    }

    private void preparaAzioniHeader() {
        if (etichettaMese != null && etichettaMese.getParent() instanceof HBox header) {
            if (header.getChildren().size() >= 3) {
                Node prev = header.getChildren().get(0);
                Node next = header.getChildren().get(2);
                if (prev instanceof Button btnPrev) {
                    btnPrev.setOnAction(evento -> mesePrecedente());
                }
                if (next instanceof Button btnNext) {
                    btnNext.setOnAction(evento -> meseSuccessivo());
                }
            }
        }

        Platform.runLater(this::configuraMenuUtente);
    }

    private void preparaBottoneAggiungi() {
        if (btnAggiungi != null) {
            btnAggiungi.setOnAction(evento -> aggiungiAttivita());
        }
    }

    private void aggiornaLista() {
        if (contenitoreSchede == null) {
            return;
        }
        contenitoreSchede.getChildren().clear();

        List<Attivita> lista = recuperaListaBase();
        lista = applicaFiltri(lista);

        if (lista.isEmpty()) {
            Label vuoto = new Label("Nessuna attivita.");
            vuoto.getStyleClass().add("empty-table-label");
            contenitoreSchede.getChildren().add(vuoto);
            return;
        }

        for (Attivita attivita : lista) {
            contenitoreSchede.getChildren().add(creaSchedaAttivita(attivita));
        }
    }

    private List<Attivita> recuperaListaBase() {
        String testo = campoRicerca != null ? campoRicerca.getText() : null;
        boolean testoVuoto = testo == null || testo.isBlank();

        try {
            return switch (modalitaRicerca) {
                case NOME -> testoVuoto ? gestione.getTutteLeAttivita() : gestione.cercaPerNome(testo.trim());
                case TIPO -> testoVuoto ? gestione.getTutteLeAttivita() : gestione.cercaPerTipo(testo.trim());
                case DATA -> recuperaPerData(testo);
            };
        } catch (IllegalArgumentException ex) {
            mostraErrore(ex.getMessage());
            return gestione.getTutteLeAttivita();
        }
    }

    private List<Attivita> recuperaPerData(String testo) {
        boolean testoVuoto = testo == null || testo.isBlank();
        if (testoVuoto && giornoSelezionato != null) {
            LocalDateTime inizio = giornoSelezionato.atStartOfDay();
            LocalDateTime fine = giornoSelezionato.atTime(LocalTime.MAX);
            return gestione.cercaPerData(inizio, fine);
        }
        if (testoVuoto) {
            return gestione.getTutteLeAttivita();
        }
        IntervalloDate intervallo = leggiIntervallo(testo.trim());
        return gestione.cercaPerData(intervallo.inizio, intervallo.fine);
    }

    private List<Attivita> applicaFiltri(List<Attivita> lista) {
        List<Attivita> filtrata = new ArrayList<>(lista);

        if (filtroPersonali != null && filtroPersonali.isSelected()) {
            filtrata.removeIf(attivita -> !attivita.isAttivitaPrivata());
        } else if (filtroCondivise != null && filtroCondivise.isSelected()) {
            filtrata.removeIf(Attivita::isAttivitaPrivata);
        }

        if (giornoSelezionato != null) {
            filtrata.removeIf(attivita -> !attivitaInGiorno(attivita, giornoSelezionato));
        }

        filtraPerPrivacy(filtrata);
        return filtrata;
    }

    private void filtraPerPrivacy(List<Attivita> lista) {
        if (utenteCorrente == null) {
            lista.clear();
            return;
        }
        lista.removeIf(attivita -> attivita.isAttivitaPrivata() && !isProprietario(attivita));
    }

    private boolean isProprietario(Attivita attivita) {
        if (utenteCorrente == null || attivita == null || attivita.getUtenteAssegnato() == null) {
            return false;
        }
        return utenteCorrente.getEmail().equalsIgnoreCase(attivita.getUtenteAssegnato().getEmail());
    }

    private boolean attivitaInGiorno(Attivita attivita, LocalDate giorno) {
        LocalDateTime inizio = attivita.getDataInizio();
        LocalDateTime fine = attivita.getDataFine();
        LocalDateTime start = giorno.atStartOfDay();
        LocalDateTime end = giorno.atTime(LocalTime.MAX);
        return !fine.isBefore(start) && !inizio.isAfter(end);
    }

    private HBox creaSchedaAttivita(Attivita attivita) {
        HBox riga = new HBox(15);
        riga.setAlignment(Pos.CENTER_LEFT);
        riga.getStyleClass().add("activity-row-example");
        riga.setMinHeight(70);
        riga.setPrefHeight(70);
        riga.setPadding(new Insets(0, 20, 0, 20));

        HBox boxCheck = new HBox();
        boxCheck.setAlignment(Pos.CENTER_LEFT);
        boxCheck.setPrefWidth(90);
        boxCheck.setMinWidth(90);
        CheckBox checkCompletata = new CheckBox();
        checkCompletata.setSelected(attivita.isCompletato());
        boolean owner = isProprietario(attivita);
        checkCompletata.setDisable(!owner);
        checkCompletata.setOnAction(evento -> {
            if (!owner) {
                checkCompletata.setSelected(attivita.isCompletato());
                mostraErrore("Non puoi modificare lo stato di questa attività.");
                return;
            }
            boolean selezionata = checkCompletata.isSelected();
            if (selezionata) {
                if (!attivitaIniziata(attivita)) {
                    checkCompletata.setSelected(false);
                    mostraErrore("L\u2019attivit\u00e0 non \u00e8 cominciata ancora");
                    return;
                }
            }
            attivita.setCompletato(selezionata);
            if (attivita.getId() > 0) {
                try {
                    gestione.segnaCompletata(attivita.getId(), selezionata);
                } catch (IllegalArgumentException ex) {
                    mostraErrore(ex.getMessage());
                }
            }
            aggiornaLista();
        });
        boxCheck.getChildren().add(checkCompletata);

        VBox boxTitolo = new VBox();
        boxTitolo.setAlignment(Pos.CENTER_LEFT);
        boxTitolo.setPrefWidth(230);
        boxTitolo.setMinWidth(230);
        Label titolo = new Label(attivita.getDescrizione());
        titolo.getStyleClass().add("activity-title");
        if (attivita.isCompletato()) {
            titolo.getStyleClass().add("activity-completed");
        }
        boxTitolo.getChildren().add(titolo);

        HBox boxBadge = new HBox();
        boxBadge.setAlignment(Pos.CENTER_LEFT);
        boxBadge.setPrefWidth(130);
        boxBadge.setMinWidth(130);
        Label badge = new Label(attivita.isAttivitaPrivata() ? "Personale" : "Condivisa");
        badge.getStyleClass().add(attivita.isAttivitaPrivata() ? "badge-personale" : "badge-condivisa");
        boxBadge.getChildren().add(badge);

        VBox boxData = new VBox();
        boxData.setAlignment(Pos.CENTER_LEFT);
        boxData.setPrefWidth(120);
        boxData.setMinWidth(120);
        Label data = new Label(attivita.getDataInizio().toLocalDate().format(formatoData));
        data.getStyleClass().add("activity-date");
        boxData.getChildren().add(data);

        VBox boxOra = new VBox();
        boxOra.setAlignment(Pos.CENTER_LEFT);
        boxOra.setPrefWidth(110);
        boxOra.setMinWidth(110);
        String orario = attivita.getDataInizio().toLocalTime().format(formatoOra) + " - "
                + attivita.getDataFine().toLocalTime().format(formatoOra);
        Label ora = new Label(orario);
        ora.getStyleClass().add("activity-time");
        boxOra.getChildren().add(ora);

        HBox boxStato = new HBox();
        boxStato.setAlignment(Pos.CENTER_LEFT);
        boxStato.setPrefWidth(110);
        boxStato.setMinWidth(110);
        Label statoLabel = creaBadgeStato(attivita);
        boxStato.getChildren().add(statoLabel);

        VBox boxUtente = new VBox();
        boxUtente.setAlignment(Pos.CENTER_LEFT);
        boxUtente.setPrefWidth(140);
        boxUtente.setMinWidth(140);
        Label utente = new Label(attivita.getUtenteAssegnato().getNome());
        utente.getStyleClass().add("activity-assigned");
        boxUtente.getChildren().add(utente);

        Region spazio = new Region();
        HBox.setHgrow(spazio, Priority.ALWAYS);

        Button menu = new Button("...");
        menu.getStyleClass().add("btn-actions");
        ContextMenu menuAzioni = new ContextMenu();
        MenuItem dettagli = new MenuItem("Dettagli");
        dettagli.setOnAction(evento -> mostraDettagli(attivita));
        MenuItem modifica = new MenuItem("Modifica");
        modifica.setOnAction(evento -> modificaAttivita(attivita));
        MenuItem elimina = new MenuItem("Elimina");
        elimina.setOnAction(evento -> eliminaAttivita(attivita));
        menuAzioni.getItems().addAll(dettagli, modifica, elimina);
        menu.setOnAction(evento -> {
            if (menuAzioni.isShowing()) {
                menuAzioni.hide();
            } else {
                menuAzioni.show(menu, Side.BOTTOM, 0, 0);
            }
        });

        HBox boxAzioni = new HBox(menu);
        boxAzioni.setAlignment(Pos.CENTER_RIGHT);

        riga.getChildren().addAll(boxCheck, boxTitolo, boxBadge, boxData, boxOra, boxStato, boxUtente, spazio, boxAzioni);
        return riga;
    }

    private void mostraDettagli(Attivita attivita) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dettagli attivita");
        alert.setHeaderText(attivita.getDescrizione());
        alert.setContentText(formattaDettagli(attivita));
        alert.showAndWait();
    }

    private Label creaBadgeStato(Attivita attivita) {
        StatoAttivita stato = calcolaStatoAttivita(attivita);
        String testo = switch (stato) {
            case FUTURA -> "FUTURA";
            case IN_CORSO -> "IN CORSO";
            case PASSATA -> "PASSATA";
            case COMPLETATA -> "COMPLETATA";
        };
        Label label = new Label(testo);
        label.getStyleClass().add("stato-badge");
        switch (stato) {
            case FUTURA -> label.getStyleClass().add("stato-futura");
            case IN_CORSO -> label.getStyleClass().add("stato-in-corso");
            case PASSATA -> label.getStyleClass().add("stato-passata");
            case COMPLETATA -> label.getStyleClass().add("stato-completata");
        }
        return label;
    }

    private StatoAttivita calcolaStatoAttivita(Attivita attivita) {
        if (attivita.isCompletato()) {
            return StatoAttivita.COMPLETATA;
        }
        LocalDateTime adesso = LocalDateTime.now();
        if (adesso.isBefore(attivita.getDataInizio())) {
            return StatoAttivita.FUTURA;
        }
        if (!adesso.isAfter(attivita.getDataFine())) {
            return StatoAttivita.IN_CORSO;
        }
        return StatoAttivita.PASSATA;
    }

    private boolean attivitaIniziata(Attivita attivita) {
        return !LocalDateTime.now().isBefore(attivita.getDataInizio());
    }

    private String formattaDettagli(Attivita attivita) {
        StringBuilder builder = new StringBuilder();
        builder.append("Tipo: ").append(attivita.getTipo()).append("\n");
        builder.append("Inizio: ").append(formattaDataOra(attivita.getDataInizio())).append("\n");
        builder.append("Fine: ").append(formattaDataOra(attivita.getDataFine())).append("\n");
        builder.append("Notifica: ").append(formattaDataOra(attivita.getDataNotifica())).append("\n");
        builder.append("Priorita: ").append(attivita.getPriorita()).append("\n");
        builder.append("Utente: ").append(attivita.getUtenteAssegnato().getNome()).append("\n");
        builder.append("Visibilita: ").append(attivita.isAttivitaPrivata() ? "Personale" : "Condivisa").append("\n");
        String context = estraiContext(attivita);
        if (context != null && !context.isBlank()) {
            builder.append("Contesto: ").append(context).append("\n");
        }
        return builder.toString();
    }

    private String formattaDataOra(LocalDateTime dataOra) {
        if (dataOra == null) {
            return "-";
        }
        return dataOra.toLocalDate().format(formatoData) + " " + dataOra.toLocalTime().format(formatoOra);
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

    // Top bar
    @FXML
    private void azioneHome() {
        modalitaRicerca = ModalitaRicerca.NOME;
        if (menuRicerca != null) {
            menuRicerca.setText("Nome");
        }
        if (campoRicerca != null) {
            campoRicerca.clear();
        }
        if (filtroTutte != null) {
            filtroTutte.setSelected(true);
        }
        giornoSelezionato = null;
        aggiornaCalendario();
        aggiornaLista();
    }

    @FXML
    private void azioneLogout() {
        SessioneUtente.pulisci();
        utenteCorrente = null;
        try {
            if (contenitoreSchede != null && contenitoreSchede.getScene() != null) {
                Stage stage = (Stage) contenitoreSchede.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/loginForm.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root, 1600, 900));
                stage.setTitle("Accedi");
                stage.setResizable(false);
                stage.setMaximized(false);
                stage.setFullScreen(false);
                stage.setMinWidth(1600);
                stage.setMinHeight(900);
                stage.setMaxWidth(1600);
                stage.setMaxHeight(900);
                stage.setWidth(1600);
                stage.setHeight(900);
                stage.show();
                javafx.application.Platform.runLater(stage::centerOnScreen);
            }
        } catch (Exception ex) {
            mostraErrore("Errore logout.");
        }
    }

    // Filtri
    @FXML
    private void filtroTutte() {
        aggiornaLista();
    }

    @FXML
    private void filtroPersonali() {
        aggiornaLista();
    }

    @FXML
    private void filtroCondivise() {
        aggiornaLista();
    }

    // Calendario
    @FXML
    private void mesePrecedente() {
        meseCorrente = meseCorrente.minusMonths(1);
        if (giornoSelezionato != null && !YearMonth.from(giornoSelezionato).equals(meseCorrente)) {
            giornoSelezionato = null;
        }
        aggiornaCalendario();
        aggiornaLista();
    }

    @FXML
    private void meseSuccessivo() {
        meseCorrente = meseCorrente.plusMonths(1);
        if (giornoSelezionato != null && !YearMonth.from(giornoSelezionato).equals(meseCorrente)) {
            giornoSelezionato = null;
        }
        aggiornaCalendario();
        aggiornaLista();
    }

    // Ricerca
    @FXML
    private void cercaPerData() {
        impostaModalitaRicerca(ModalitaRicerca.DATA, "Data", "Data (gg/MM/aaaa o gg/MM/aaaa - gg/MM/aaaa)");
    }

    @FXML
    private void cercaPerNome() {
        impostaModalitaRicerca(ModalitaRicerca.NOME, "Nome", "Cerca per nome");
    }

    @FXML
    private void cercaPerTipo() {
        impostaModalitaRicerca(ModalitaRicerca.TIPO, "Tipo", "Cerca per tipo (STUDIO, SPESA, DOMESTICA)");
    }

    @FXML
    private void applicaRicerca() {
        aggiornaLista();
    }

    private void impostaModalitaRicerca(ModalitaRicerca modalita, String testoMenu, String prompt) {
        modalitaRicerca = modalita;
        if (menuRicerca != null) {
            menuRicerca.setText(testoMenu);
        }
        if (campoRicerca != null) {
            campoRicerca.clear();
            campoRicerca.setPromptText(prompt);
        }
        aggiornaLista();
    }

    // CRUD
    @FXML
    private void aggiungiAttivita() {
        if (utenteCorrente == null) {
            mostraErrore("Devi effettuare il login per aggiungere attivita.");
            return;
        }
        Stage owner = getStage();
        List<Utente> utenti = gestione.getUtentiDisponibili();
        Optional<java.util.Map<String, Object>> risultato = DialogoAttivita.mostra(owner, "Nuova attivita", null, utenti, utenteCorrente);
        risultato.ifPresent(parametri -> {
            try {
                if (utenteCorrente != null) {
                    parametri.put("utenteAssegnato", utenteCorrente);
                }
                gestione.aggiungiAttivita(parametri);
                aggiornaLista();
                controllaNotifiche();
            } catch (IllegalArgumentException ex) {
                mostraErrore(ex.getMessage());
            }
        });
    }

    private void modificaAttivita(Attivita attivita) {
        if (utenteCorrente == null) {
            mostraErrore("Devi effettuare il login per modificare attivita.");
            return;
        }
        if (!isProprietario(attivita)) {
            mostraErrore("Puoi modificare solo le tue attivita.");
            return;
        }
        Stage owner = getStage();
        List<Utente> utenti = gestione.getUtentiDisponibili();
        Utente utenteDialogo = utenteCorrente;
        Optional<java.util.Map<String, Object>> risultato = DialogoAttivita.mostra(owner, "Modifica attivita", attivita, utenti, utenteDialogo);
        risultato.ifPresent(parametri -> {
            try {
                if (utenteCorrente != null) {
                    parametri.put("utenteAssegnato", utenteCorrente);
                }
                gestione.modificaAttivita(attivita.getId(), parametri);
                aggiornaLista();
                controllaNotifiche();
            } catch (IllegalArgumentException ex) {
                mostraErrore(ex.getMessage());
            }
        });
    }

    private void eliminaAttivita(Attivita attivita) {
        if (utenteCorrente == null) {
            mostraErrore("Devi effettuare il login per eliminare attivita.");
            return;
        }
        if (!isProprietario(attivita)) {
            mostraErrore("Puoi eliminare solo le tue attivita.");
            return;
        }
        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
        conferma.setTitle("Conferma eliminazione");
        conferma.setHeaderText("Eliminare l'attivita selezionata?");
        conferma.setContentText(attivita.getDescrizione());
        Optional<ButtonType> risposta = conferma.showAndWait();
        if (risposta.isPresent() && risposta.get() == ButtonType.OK) {
            try {
                gestione.rimuoviAttivita(attivita.getId());
                aggiornaLista();
            } catch (IllegalArgumentException ex) {
                mostraErrore(ex.getMessage());
            }
        }
    }

    private void aggiornaCalendario() {
        if (etichettaMese != null) {
            etichettaMese.setText(capitalize(formatoMese.format(meseCorrente.atDay(1))));
        }

        if (grigliaCalendario == null) {
            return;
        }
        grigliaCalendario.getChildren().clear();
        grigliaCalendario.getColumnConstraints().clear();
        grigliaCalendario.getRowConstraints().clear();

        for (int c = 0; c < 7; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            cc.setPercentWidth(100.0 / 7.0);
            grigliaCalendario.getColumnConstraints().add(cc);
        }

        for (int r = 0; r < 7; r++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            rc.setFillHeight(true);
            grigliaCalendario.getRowConstraints().add(rc);
        }

        String[] giorni = {"L", "M", "M", "G", "V", "S", "D"};
        for (int c = 0; c < 7; c++) {
            Label header = new Label(giorni[c]);
            header.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            header.getStyleClass().add("calendar-day-header");
            GridPane.setHgrow(header, Priority.ALWAYS);
            GridPane.setVgrow(header, Priority.ALWAYS);
            grigliaCalendario.add(header, c, 0);
        }

        LocalDate primoGiorno = meseCorrente.atDay(1);
        int giorniNelMese = meseCorrente.lengthOfMonth();
        int offset = primoGiorno.getDayOfWeek().getValue() - 1;

        int riga = 1;
        int colonna = offset;

        for (int giorno = 1; giorno <= giorniNelMese; giorno++) {
            LocalDate data = meseCorrente.atDay(giorno);

            ToggleButton btn = new ToggleButton(String.valueOf(giorno));
            btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            btn.setMinSize(32, 28);
            btn.setPrefSize(34, 28);
            btn.setPadding(Insets.EMPTY);
            btn.setAlignment(Pos.CENTER);
            btn.getStyleClass().add("calendar-day");
            btn.setTextOverrun(OverrunStyle.CLIP);

            if (giornoSelezionato != null && data.equals(giornoSelezionato)) {
                btn.getStyleClass().add("calendar-day-selected");
                btn.setSelected(true);
            }

            btn.setOnAction(evento -> {
                if (data.equals(giornoSelezionato)) {
                    giornoSelezionato = null;
                } else {
                    giornoSelezionato = data;
                }
                aggiornaCalendario();
                aggiornaLista();
            });

            grigliaCalendario.add(btn, colonna, riga);
            colonna++;
            if (colonna == 7) {
                colonna = 0;
                riga++;
            }
        }
    }

    private void avviaTimerNotifiche() {
        if (timerNotifiche != null) {
            timerNotifiche.stop();
        }
        timerNotifiche = new Timeline(new KeyFrame(Duration.seconds(45), evento -> controllaNotifiche()));
        timerNotifiche.setCycleCount(Timeline.INDEFINITE);
        timerNotifiche.play();
        controllaNotifiche();
    }

    private void controllaNotifiche() {
        try {
            List<Attivita> daNotificare = gestione.getAttivitaDaNotificare(LocalDateTime.now());
            if (daNotificare.isEmpty()) {
                return;
            }
            for (Attivita attivita : daNotificare) {
                if (utenteCorrente == null) {
                    continue;
                }
                if (attivita.isAttivitaPrivata() && !isProprietario(attivita)) {
                    continue;
                }
                String orario = formattaDataOra(attivita.getDataInizio());
                String messaggio = "L'attivita (" + attivita.getDescrizione() + "), creata da "
                        + attivita.getUtenteAssegnato().getNome()
                        + ", iniziera il " + attivita.getDataInizio().toLocalDate().format(formatoData)
                        + " alle ore " + attivita.getDataInizio().toLocalTime().format(formatoOra);
                Node ancora = btnAggiungi != null ? btnAggiungi : contenitoreSchede;
                if (ancora != null) {
                    NotificaToast.mostra(ancora, "Notifica Attivita", messaggio, orario);
                }
                gestione.segnaNotificata(attivita.getId(), true);
            }
        } catch (Exception ex) {
            // Evita di bloccare il timer per errori sporadici
        }
    }

    private void avviaTimerStato() {
        if (timerStato != null) {
            timerStato.stop();
        }
        timerStato = new Timeline(new KeyFrame(Duration.seconds(30), evento -> aggiornaLista()));
        timerStato.setCycleCount(Timeline.INDEFINITE);
        timerStato.play();
    }

    private Stage getStage() {
        if (contenitoreSchede == null || contenitoreSchede.getScene() == null) {
            return null;
        }
        return (Stage) contenitoreSchede.getScene().getWindow();
    }

    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        Stage owner = getStage();
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.show();
    }

    private IntervalloDate leggiIntervallo(String testo) {
        String[] parti;
        if (testo.contains(";")) {
            parti = testo.split("\\s*;\\s*");
        } else if (testo.contains(" - ")) {
            parti = testo.split("\\s-\\s");
        } else {
            parti = new String[]{testo};
        }

        if (parti.length == 1) {
            LocalDate data = parseData(parti[0]);
            return new IntervalloDate(data.atStartOfDay(), data.atTime(LocalTime.MAX));
        }
        if (parti.length == 2) {
            LocalDate inizio = parseData(parti[0]);
            LocalDate fine = parseData(parti[1]);
            if (inizio.isAfter(fine)) {
                throw new IllegalArgumentException("Intervallo data non valido.");
            }
            return new IntervalloDate(inizio.atStartOfDay(), fine.atTime(LocalTime.MAX));
        }
        throw new IllegalArgumentException("Formato data non valido.");
    }

    private LocalDate parseData(String valore) {
        for (DateTimeFormatter formatter : formatiDataRicerca) {
            try {
                return LocalDate.parse(valore.trim(), formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IllegalArgumentException("Formato data non valido. Usa gg/MM/aaaa.");
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }


    public void impostaUtenteCorrente(Utente utente) {
        this.utenteCorrente = utente;
        configuraMenuUtente();
    }

    private void aggiornaMenuUtente() {
        if (menuUtente == null) {
            return;
        }
        String nome = (utenteCorrente != null && utenteCorrente.getNome() != null && !utenteCorrente.getNome().isBlank())
                ? utenteCorrente.getNome()
                : "Utente sconosciuto";
        menuUtente.setText(nome + " ▼");
    }

    private void configuraMenuUtente() {
        if (menuUtente == null) return;
        if (menuUtente.getItems().isEmpty()) {
            MenuItem logout = new MenuItem("Logout");
            logout.setOnAction(evento -> azioneLogout());
            menuUtente.getItems().add(logout);
        }
        aggiornaMenuUtente();
        menuUtente.setDisable(false);
    }

    private record IntervalloDate(LocalDateTime inizio, LocalDateTime fine) {}

 
}

