package gui;

import dataBase.DAO.UtenteDAO;
import dominio.Utente;
import gestione.SessioneUtente;
import gui.controller.MainLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static final double APP_WIDTH = 1500;
    private static final double APP_HEIGHT = 800;
    private boolean ripristinoInCorso = false;
    private double lastWidth = APP_WIDTH;
    private double lastHeight = APP_HEIGHT;

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, dimensioneCorrente(finestra, APP_WIDTH), dimensioneCorrente(finestra, APP_HEIGHT, false));
        finestra.setTitle("Login");
        finestra.setScene(scene);
        configuraFinestraBase(finestra);
        finestra.show();
        javafx.application.Platform.runLater(finestra::centerOnScreen);
    }

    private void mostraLayout(Stage finestra, Utente utente) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/layout.fxml"));
        Parent root = loader.load();
        MainLayoutController controller = loader.getController();
        if (controller != null) {
            controller.impostaUtenteCorrente(utente);
        }
        Scene scene = new Scene(root, dimensioneCorrente(finestra, APP_WIDTH), dimensioneCorrente(finestra, APP_HEIGHT, false));
        finestra.setTitle("HouseCalendar");
        finestra.setScene(scene);
        configuraFinestraBase(finestra);
        finestra.show();
        javafx.application.Platform.runLater(finestra::centerOnScreen);
    }

    private void configuraFinestraBase(Stage finestra) {
        if (finestra == null) {
            return;
        }
        finestra.setResizable(true);
        finestra.setFullScreen(false);
        if (finestra.getWidth() <= 0) {
            finestra.setWidth(APP_WIDTH);
        }
        if (finestra.getHeight() <= 0) {
            finestra.setHeight(APP_HEIGHT);
        }
        finestra.setMinWidth(APP_WIDTH);
        finestra.setMinHeight(APP_HEIGHT);
        finestra.setMaximized(false);
        lastWidth = Math.max(finestra.getWidth(), APP_WIDTH);
        lastHeight = Math.max(finestra.getHeight(), APP_HEIGHT);
        finestra.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!finestra.isMaximized() && !ripristinoInCorso) {
                lastWidth = Math.max(newVal.doubleValue(), APP_WIDTH);
            }
        });
        finestra.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (!finestra.isMaximized() && !ripristinoInCorso) {
                lastHeight = Math.max(newVal.doubleValue(), APP_HEIGHT);
            }
        });
        finestra.maximizedProperty().addListener((obs, eraMax, isMax) -> {
            if (Boolean.TRUE.equals(isMax)) {
                ripristinoInCorso = true;
                javafx.application.Platform.runLater(() -> {
                    finestra.setMaximized(false);
                    finestra.setWidth(Math.max(lastWidth, APP_WIDTH));
                    finestra.setHeight(Math.max(lastHeight, APP_HEIGHT));
                    finestra.centerOnScreen();
                    ripristinoInCorso = false;
                });
            }
        });
    }

    private double dimensioneCorrente(Stage finestra, double fallback) {
        return dimensioneCorrente(finestra, fallback, true);
    }

    private double dimensioneCorrente(Stage finestra, double fallback, boolean isWidth) {
        if (finestra == null) {
            return fallback;
        }
        double valore = isWidth ? finestra.getWidth() : finestra.getHeight();
        return valore > 0 ? valore : fallback;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
