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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/login.fxml"));
            Parent root = loader.load();
            double width = stage.getWidth() > 0 ? stage.getWidth() : 1500;
            double height = stage.getHeight() > 0 ? stage.getHeight() : 800;
            Scene scene = new Scene(root, width, height);
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMaximized(false);
            stage.setMinWidth(1500);
            stage.setMinHeight(800);
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
