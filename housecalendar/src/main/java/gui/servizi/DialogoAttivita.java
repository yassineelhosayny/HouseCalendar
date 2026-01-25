package gui.servizi;

import dominio.Attivita;
import dominio.Utente;
import gui.controller.FormAttivitaController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DialogoAttivita {

    private DialogoAttivita() {
    }

    public static Optional<Map<String, Object>> mostra(Stage owner, String titolo, Attivita attivita, List<Utente> utenti, Utente utenteCorrente) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogoAttivita.class.getResource("/gui/view/attivita_form.fxml"));
            Parent root = loader.load();
            FormAttivitaController controller = loader.getController();

            Stage dialogo = new Stage();
            if (owner != null) {
                dialogo.initOwner(owner);
                dialogo.initModality(Modality.WINDOW_MODAL);
            }
            dialogo.setTitle(titolo);
            dialogo.setScene(new Scene(root));
            dialogo.setResizable(false);

            controller.impostaStage(dialogo);
            controller.impostaDati(titolo, attivita, utenti);
            if (utenteCorrente != null) {
                controller.impostaUtenteCorrente(utenteCorrente);
            }

            dialogo.showAndWait();
            return Optional.ofNullable(controller.getRisultato());
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
