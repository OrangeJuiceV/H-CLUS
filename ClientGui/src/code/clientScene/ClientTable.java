package code.clientScene;

import code.ClientApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.ObjectOutputStream;
import java.util.List;
import java.io.IOException;

/**
 * Gestore della selezione e interazione con la tabella del client.
 * <p>
 * La classe `ClientTable` gestisce la selezione di una tabella dal server da parte dell'utente.
 * Permette di scegliere una tabella da una combo box, inviarla al server per la validazione
 * e, in base alla risposta, avviare ulteriori operazioni.
 * </p>
 */
public class ClientTable {

    /**
     * Avvia la scena in cui l'utente seleziona una tabella dal server.
     * <p>
     * Carica le informazioni relative alle tabelle disponibili dal server e le mostra in una
     * combo box. Una volta che l'utente seleziona una tabella e preme il pulsante di invio,
     * l'applicazione invia la selezione al server per la validazione. Se il server risponde
     * positivamente, l'applicazione procede con l'operazione richiesta. In caso contrario,
     * mostra un messaggio di errore.
     * </p>
     *
     * @param stage la finestra principale dell'applicazione
     * @param width larghezza della finestra
     * @param height altezza della finestra
     * @param choice un valore che determina quale operazione eseguire dopo la selezione della tabella
     * @throws IOException se si verifica un errore nel caricamento dell'interfaccia utente o nella comunicazione con il server
     */
    public void start(Stage stage, double width, double height, int choice) throws IOException {
        // Carica la scena clientTable dal file FXML
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/resources/clientTable.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ClientManager.setScreen(stage, scene, width, height);

        // Ottieni la combo box, il pulsante di invio e il campo di testo per gli errori
        ComboBox comboBox = (ComboBox) fxmlLoader.getNamespace().get("comboBox"); // Combobox contenente i nomi delle tabelle
        Button submitButton = (Button) fxmlLoader.getNamespace().get("submitButton");
        Text textError = (Text) fxmlLoader.getNamespace().get("textError");

        try {
            // Legge la lista dei nomi delle tabelle dal server
            List<String> tablesNames = (List<String>) ClientManager.getInputStream().readObject();
            // Aggiunge i nomi delle tabelle alla combo box
            comboBox.getItems().addAll(tablesNames);

            // Imposta l'azione del pulsante di invio
            submitButton.setOnAction(event -> {
                try {
                    // Ottiene le dimensioni correnti della finestra prima di navigare alla scena successiva
                    double currentWidth = stage.getWidth();
                    double currentHeight = stage.getHeight();

                    // Ottiene la tabella selezionata dall'utente
                    String selectedTable = (String) comboBox.getSelectionModel().getSelectedItem();
                    if (selectedTable != null) {
                        // Invia la tabella selezionata al server
                        ObjectOutputStream out = ClientManager.getOutputStream();
                        out.writeObject(selectedTable);

                        // Riceve la risposta del server
                        String answer = (String) ClientManager.getInputStream().readObject();
                        if ("OK".equals(answer)) {
                            // Se la risposta del server è "OK", avvia l'operazione successiva
                            if (choice == 1) {
                                // Avvia l'operazione per il tipo di scelta 1
                                ClientDepth depth = new ClientDepth();
                                depth.start(stage, currentWidth, currentHeight);
                            } else {
                                // Avvia l'operazione per il tipo di scelta 2
                                ClientLoadObject loadObject = new ClientLoadObject();
                                loadObject.start(stage, currentWidth, currentHeight);
                            }
                        } else {
                            // Se la risposta del server non è "OK", mostra un messaggio di errore
                            textError.setText(answer);
                        }
                    } else {
                        // Se l'utente non seleziona una tabella, mostra un errore
                        textError.setText("Seleziona una tabella");
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    // Gestisce eventuali errori durante la comunicazione o il caricamento dei dati
                    textError.setText(ex.getMessage());
                }
            });
        } catch (IOException | ClassNotFoundException ex) {
            // Gestisce errori durante la comunicazione iniziale con il server
            textError.setText("Errore nella comunicazione con il server");
        }
    }
}
