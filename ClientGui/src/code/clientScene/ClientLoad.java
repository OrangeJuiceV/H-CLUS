package code.clientScene;

import code.ClientApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Gestore della fase di caricamento del client.
 * <p>
 * La classe `ClientLoad` si occupa di gestire l'interfaccia utente durante la fase di caricamento del client,
 * permettendo all'utente di scegliere una modalità tramite una radio button e di inviare la selezione al server.
 * </p>
 */
public class ClientLoad {

    /**
     * Avvia la scena di caricamento del client e gestisce l'azione del pulsante di invio.
     * <p>
     * In questa fase, l'utente può scegliere una delle opzioni fornite tramite le radio button.
     * Quando il pulsante di invio viene premuto, l'opzione selezionata viene inviata al server e si passa alla scena successiva
     * in base alla selezione dell'utente.
     * </p>
     *
     * @param stage la finestra principale dell'applicazione
     * @param width larghezza della finestra
     * @param height altezza della finestra
     * @throws IOException se si verifica un errore nel caricamento dell'interfaccia utente o nella comunicazione con il server
     */
    public void start(Stage stage, double width, double height) throws IOException {
        // Carica la scena di caricamento dal file FXML
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/resources/clientLoad.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Imposta la scena nella finestra principale
        ClientManager.setScreen(stage, scene, width, height);

        // Ottieni il flusso di output per la comunicazione con il server
        ObjectOutputStream out = ClientManager.getOutputStream();

        // Ottieni il pulsante di invio e il gruppo di radio button
        Button submitButton = (Button) fxmlLoader.getNamespace().get("submitButton");
        ToggleGroup multiscelta = (ToggleGroup) fxmlLoader.getNamespace().get("multiscelta");

        // Imposta l'azione del pulsante di invio
        submitButton.setOnAction(event -> {
            // Ottieni le dimensioni correnti della finestra prima di navigare alla prossima scena
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();

            // Ottieni l'opzione selezionata tra le radio button
            RadioButton selectedRadioButton = (RadioButton) multiscelta.getSelectedToggle();
            if (selectedRadioButton != null) {
                String selectedOption = selectedRadioButton.getText();

                // Se l'opzione selezionata è "Apprendi dendrogramma da database", invia 0 e carica la scena successiva
                if (selectedOption.equals("Apprendi dendrogramma da database")) {
                    try {
                        out.writeObject(0); // Invia il valore 0 per l'opzione selezionata
                        ClientTable clientTable = new ClientTable();
                        clientTable.start(stage, currentWidth, currentHeight, 1); // Avvia la scena successiva
                    } catch (IOException e) {
                        e.printStackTrace(); // Gestisce eventuali errori
                    }
                } else {
                    // Se l'opzione selezionata è diversa, invia 0 e carica un'altra scena
                    try {
                        out.writeObject(0); // Invia il valore 0 per l'opzione selezionata
                        ClientTable clientTable = new ClientTable();
                        clientTable.start(stage, currentWidth, currentHeight, 2); // Avvia la scena successiva
                    } catch (IOException e) {
                        e.printStackTrace(); // Gestisce eventuali errori
                    }
                }
            }
        });
    }
}
