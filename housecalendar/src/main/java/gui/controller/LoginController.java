package gui.controller;

import dataBase.DAO.UtenteDAO;
import dominio.Utente;
import gestione.LoginGestione;
import gestione.SessioneUtente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoPassword;
    @FXML private Label labelErrore;

    private final LoginGestione gestione = new LoginGestione();

    @FXML
    private void azioneLogin() {
        String email = campoEmail != null ? campoEmail.getText() : null;
        String password = campoPassword != null ? campoPassword.getText() : null;

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            mostraErrore("Inserisci email e password.");
            return;
        }

        boolean ok = gestione.login(email.trim(), password);
        if (!ok) {
            mostraErrore("Credenziali non valide.");
            return;
        }

        Utente utente = UtenteDAO.getUtenteByEmail(email.trim());
        if (utente == null) {
            mostraErrore("Utente non trovato.");
            return;
        }

        SessioneUtente.salvaEmail(utente.getEmail());
        mostraLayout(utente);
    }

    @FXML
    private void vaiRegistrazione() {
        try {
            Stage stage = getStage();
            if (stage == null) {
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/registrazione.fxml"));
            Parent root = loader.load();
            double width = stage.getWidth() > 0 ? stage.getWidth() : 1500;
            double height = stage.getHeight() > 0 ? stage.getHeight() : 800;
            Scene scene = new Scene(root, width, height);
            stage.setTitle("Registrazione");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMaximized(false);
            stage.setMinWidth(1500);
            stage.setMinHeight(800);
            stage.show();
            javafx.application.Platform.runLater(stage::centerOnScreen);
        } catch (Exception e) {
            mostraErrore("Errore apertura registrazione.");
        }
    }

    private void mostraLayout(Utente utente) {
        try {
            Stage stage = getStage();
            if (stage == null) {
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/layout.fxml"));
            Parent root = loader.load();
            MainLayoutController controller = loader.getController();
            if (controller != null) {
                controller.impostaUtenteCorrente(utente);
            }
            double width = stage.getWidth() > 0 ? stage.getWidth() : 1500;
            double height = stage.getHeight() > 0 ? stage.getHeight() : 800;
            Scene scene = new Scene(root, width, height);
            stage.setTitle("HouseCalendar");
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(1500);
            stage.setMinHeight(800);
            stage.setMaximized(false);
            stage.show();
        } catch (Exception e) {
            mostraErrore("Errore apertura applicazione.");
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
