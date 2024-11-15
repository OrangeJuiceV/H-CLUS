package code.clientScene;

import code.ClientApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Classe che gestisce l'interfaccia utente per selezionare la profondità
 * e la modalità di distanza, quindi inviare le informazioni al server.
 * <p>
 * La classe `ClientDepth` permette all'utente di scegliere la profondità per l'algoritmo
 * di clustering e di selezionare il tipo di distanza (come "single-link") tramite una
 * combobox e un gruppo di opzioni radio. Le informazioni vengono inviate al server
 * e viene visualizzato un messaggio di errore se qualcosa va storto.
 * </p>
 */
public class ClientDepth {

    /**
     * Avvia la scena in cui l'utente può selezionare la profondità e la modalità di distanza.
     * <p>
     * La scena contiene una combobox per selezionare la profondità e un gruppo di opzioni
     * radio per scegliere la modalità di distanza. Quando l'utente fa clic su "Invia", i dati
     * vengono inviati al server e viene visualizzato un messaggio di errore se qualcosa non va.
     * </p>
     *
     * @param stage la finestra principale dell'applicazione
     * @param width larghezza della finestra
     * @param height altezza della finestra
     * @throws IOException se si verifica un errore nella comunicazione con il server o nel caricamento dell'interfaccia utente
     */
    public void start(Stage stage, double width, double height) throws IOException {
        // Carica la scena clientDepth dal file FXML
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/resources/clientDepth.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Imposta la scena
        ClientManager.setScreen(stage, scene, width, height);

        // Ottieni i componenti dell'interfaccia utente
        ComboBox<Integer> comboBox = (ComboBox<Integer>) fxmlLoader.getNamespace().get("comboBox");
        comboBox.getItems().addAll(1, 2, 3, 4, 5); // Aggiungi le opzioni di profondità alla combobox
        comboBox.setValue(5); // Imposta la profondità predefinita a 5

        ToggleGroup toggleGroup = (ToggleGroup) fxmlLoader.getNamespace().get("multiscelta"); // Gruppo di opzioni radio per la modalità di distanza
        Button buttonInvia = (Button) fxmlLoader.getNamespace().get("buttonInvia"); // Pulsante "Invia"

        // Gestisce l'azione del pulsante "Invia"
        buttonInvia.setOnAction(event -> {
            // Ottieni la profondità selezionata dalla combobox
            Integer selectedDepth = comboBox.getSelectionModel().getSelectedItem();

            // Ottieni il RadioButton selezionato dal ToggleGroup
            RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
            String selectedDistance = selectedRadioButton != null ? selectedRadioButton.getText() : "None";
            Text textError = (Text) fxmlLoader.getNamespace().get("textError"); // Text per visualizzare gli errori
            ObjectOutputStream out = ClientManager.getOutputStream(); // Ottieni lo stream di output per inviare i dati al server

            try {
                // Invia al server la profondità selezionata
                out.writeObject(1); // Indica che si tratta di una richiesta di profondità
                out.writeObject(selectedDepth); // Invia la profondità scelta

                // Invia la modalità di distanza selezionata
                if ("single-link".equals(selectedDistance)) {
                    out.writeObject(1); // 1 per single-link
                } else {
                    out.writeObject(0); // 0 per altra modalità
                }

                // Ricevi la risposta dal server
                if ("OK".equals(ClientManager.getInputStream().readObject())) {
                    // Se la risposta è OK, carica la scena successiva per salvare l'oggetto
                    double currentWidth = stage.getWidth();
                    double currentHeight = stage.getHeight();
                    ClientSaveObject save = new ClientSaveObject();
                    save.start(stage, currentWidth, currentHeight);
                } else {
                    // Se la risposta non è OK, visualizza un messaggio di errore
                    textError.setText("Errore con il server");
                }
            } catch (IOException | ClassNotFoundException e) {
                // Gestione degli errori di comunicazione con il server
                textError.setText("Errore di comunicazione con  il server");
            }
        });
    }
}
