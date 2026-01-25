package gui.servizi;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;

public final class NotificaToast {

    private static final int MARGINE = 20;
    private static final int DURATA_SECONDI = 10;
    private NotificaToast() {
    }

    public static void mostra(Node ancora, String titolo, String messaggio, String orario) {
        if (ancora == null || ancora.getScene() == null) {
            return;
        }
        Window owner = ancora.getScene().getWindow();
        if (owner == null) {
            return;
        }
        try {
            Parent root = FXMLLoader.load(NotificaToast.class.getResource("/gui/view/notifica.fxml"));
            Label titoloLabel = (Label) root.lookup("#notificationTitle");
            Label messaggioLabel = (Label) root.lookup("#notificationMessage");
            Label orarioLabel = (Label) root.lookup("#notificationTime");
            Button btnClose = (Button) root.lookup("#btnCloseNotif");
            Button btnDismiss = (Button) root.lookup("#btnDismiss");
            Button btnAction = (Button) root.lookup("#btnAction");

            if (titoloLabel != null) {
                titoloLabel.setText(titolo);
            }
            if (messaggioLabel != null) {
                messaggioLabel.setText(messaggio);
            }
            if (orarioLabel != null) {
                orarioLabel.setText(orario);
            }
            if (btnDismiss != null) {
                btnDismiss.setVisible(true);
                btnDismiss.setManaged(true);
                btnDismiss.setText("OK");
            }
            if (btnAction != null) {
                btnAction.setVisible(false);
                btnAction.setManaged(false);
            }

            Popup popup = new Popup();
            popup.getContent().add(root);

            popup.show(owner);
            posizionaPopup(owner, popup, ancora);

            owner.xProperty().addListener((obs, oldVal, newVal) -> posizionaPopup(owner, popup, ancora));
            owner.yProperty().addListener((obs, oldVal, newVal) -> posizionaPopup(owner, popup, ancora));
            owner.widthProperty().addListener((obs, oldVal, newVal) -> posizionaPopup(owner, popup, ancora));
            owner.heightProperty().addListener((obs, oldVal, newVal) -> posizionaPopup(owner, popup, ancora));

            if (btnClose != null) {
                btnClose.setOnAction(evento -> popup.hide());
            }
            if (btnDismiss != null) {
                btnDismiss.setOnAction(evento -> popup.hide());
            }

            PauseTransition pausa = new PauseTransition(Duration.seconds(DURATA_SECONDI));
            pausa.setOnFinished(evento -> popup.hide());
            pausa.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void posizionaPopup(Window owner, Popup popup, Node ancora) {
        if (!popup.isShowing()) {
            return;
        }
        Platform.runLater(() -> {
            Bounds bounds = ancora.localToScreen(ancora.getBoundsInLocal());
            double x = bounds.getMaxX() - popup.getWidth();
            double y = bounds.getMinY() - popup.getHeight() - MARGINE;
            if (x < owner.getX() + MARGINE) {
                x = owner.getX() + MARGINE;
            }
            if (y < owner.getY() + MARGINE) {
                y = owner.getY() + MARGINE;
            }
            popup.setX(x);
            popup.setY(y);
        });
    }
}
