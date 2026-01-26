package gui.controller;

import dominio.Utente;
import gestione.LoginGestione;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrazioneController {

    @FXML private TextField campoNome;
    @FXML private TextField campoEmail;
    @FXML private PasswordField campoPassword;
    @FXML private PasswordField campoConferma;
    @FXML private Label labelErrore;

    private final LoginGestione gestione = new LoginGestione();

    @FXML
    private void azioneRegistrazione() {
        String nome = campoNome != null ? campoNome.getText() : null;
        String email = campoEmail != null ? campoEmail.getText() : null;
        String password = campoPassword != null ? campoPassword.getText() : null;
        String conferma = campoConferma != null ? campoConferma.getText() : null;

        if (nome == null || nome.isBlank() || email == null || email.isBlank() ||
                password == null || password.isBlank() || conferma == null || conferma.isBlank()) {
            mostraErrore("Compila tutti i campi.");
            return;
        }
        if (!password.equals(conferma)) {
            mostraErrore("Le password non coincidono.");
            return;
        }
        if (password.length() < 6) {
            mostraErrore("La password deve avere almeno 6 caratteri.");
            return;
        }

        try {
            Utente nuovo = new Utente(nome.trim(), email.trim(), password);
            boolean ok = gestione.registraUtente(nuovo.getNome(), nuovo.getEmail(), nuovo.getPassword());
            if (!ok) {
                mostraErrore("Registrazione fallita. Email gia usata?");
                return;
            }
            vaiLogin();
        } catch (IllegalArgumentException ex) {
            mostraErrore(ex.getMessage());
        }
    }

    @FXML
    private void vaiLogin() {
        try {
            Stage stage = getStage();
            if (stage == null) {
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/loginForm.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1600, 900);
            stage.setTitle("Accedi");
            stage.setScene(scene);
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
        } catch (Exception e) {
            mostraErrore("Errore apertura login.");
        }
    }

    private void mostraErrore(String messaggio) {
        if (labelErrore != null) {
            labelErrore.setText(messaggio);
        }
    }

    private Stage getStage() {
        if (campoEmail != null && campoEmail.getScene() != null) {
            return (Stage) campoEmail.getScene().getWindow();
        }
        return null;
    }

}
