package gui;

import dataBase.DAO.UtenteDAO;
import dominio.Utente;
import gestione.SessioneUtente;
import gui.controller.MainLayoutController;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static final double NORMAL_WIDTH = 1800;
    private static final double NORMAL_HEIGHT = 1000;

    private double normalX = Double.NaN;
    private double normalY = Double.NaN;
    private boolean blockingMaximize = false;

    @Override
    public void start(Stage finestra) throws Exception {
        Utente utente = null;
        String email = SessioneUtente.getEmail();
        if (email != null && !email.isBlank()) {
            utente = UtenteDAO.getUtenteByEmail(email);
        }

        if (utente != null) {
            mostraLayout(finestra, utente);
        } else {
            mostraLogin(finestra);
        }
    }

    private void mostraLogin(Stage finestra) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/loginForm.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, NORMAL_WIDTH, NORMAL_HEIGHT);
        finestra.setTitle("Accedi");
        finestra.setScene(scene);
        configuraFinestraBase(finestra);
        finestra.show();
    }

    private void mostraLayout(Stage finestra, Utente utente) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/layout.fxml"));
        Parent root = loader.load();
        MainLayoutController controller = loader.getController();
        if (controller != null) {
            controller.impostaUtenteCorrente(utente);
        }
        Scene scene = new Scene(root, NORMAL_WIDTH, NORMAL_HEIGHT);
        finestra.setTitle("HouseCalendar");
        finestra.setScene(scene);
        configuraFinestraBase(finestra);
        finestra.show();
    }

    private void configuraFinestraBase(Stage finestra) {
        if (finestra == null) {
            return;
        }
        finestra.setResizable(false);
        finestra.setFullScreen(false);
        finestra.setFullScreenExitHint("");
        finestra.setFullScreenExitKeyCombination(null);
        applicaDimensioniNormali(finestra);
        centraFinestra(finestra);
        salvaPosizioneNormale(finestra);
        javafx.application.Platform.runLater(() -> {
            applicaDimensioniNormali(finestra);
            centraFinestra(finestra);
            salvaPosizioneNormale(finestra);
        });

        // blocca massimizza senza innescare loop ricorsivi
        if (!Boolean.TRUE.equals(finestra.getProperties().get("maxBlocker"))) {
            finestra.getProperties().put("maxBlocker", true);
            finestra.maximizedProperty().addListener((obs, wasMax, isMax) -> {
                if (!isMax) {
                    return;
                }
                if (blockingMaximize) {
                    return;
                }
                blockingMaximize = true;
                finestra.setMaximized(false);
                applicaDimensioniNormali(finestra);
                centraFinestra(finestra);
                blockingMaximize = false;
            });
        }
    }

    private void applicaDimensioniNormali(Stage finestra) {
        finestra.setWidth(NORMAL_WIDTH);
        finestra.setHeight(NORMAL_HEIGHT);
        finestra.setMinWidth(NORMAL_WIDTH);
        finestra.setMinHeight(NORMAL_HEIGHT);
        finestra.setMaxWidth(NORMAL_WIDTH);
        finestra.setMaxHeight(NORMAL_HEIGHT);
    }

    private void salvaPosizioneNormale(Stage finestra) {
        normalX = finestra.getX();
        normalY = finestra.getY();
    }
    private void centraFinestra(Stage finestra) {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        double x = bounds.getMinX() + (bounds.getWidth() -NORMAL_WIDTH) / 2.0;
        double y = bounds.getMinY() + (bounds.getHeight() - NORMAL_HEIGHT)/ 2.0;
        finestra.setX(x);
        finestra.setY(y);
    }
    public static void main(String[] args) {
        launch(args);
    }

}
