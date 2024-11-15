package code.clientScene;

import code.ClientApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Classe che gestisce il caricamento di un oggetto dal client.
 * <p>
 * La classe `ClientLoadObject` gestisce l'interazione con l'utente per selezionare un file
 * contenente un oggetto serializzato, inviarlo al server per la deserializzazione,
 * e visualizzare l'oggetto deserializzato nella finestra.
 * </p>
 */
public class ClientLoadObject {

    /**
     * Avvia la scena in cui l'utente può caricare un oggetto dal client.
     * <p>
     * Carica la scena `clientLoadObject.fxml`, inizializza i pulsanti per selezionare il file
     * e per riavviare la connessione con il server, e gestisce l'invio del file selezionato al server.
     * Quando l'oggetto è deserializzato dal server, viene visualizzato nell'area di testo.
     * </p>
     *
     * @param stage la finestra principale dell'applicazione
     * @param width larghezza della finestra
     * @param height altezza della finestra
     * @throws IOException se si verifica un errore nella comunicazione con il server o nel caricamento dell'interfaccia utente
     */
    public void start(Stage stage, double width, double height) throws IOException {
        // Carica la scena clientLoadObject dal file FXML
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/resources/clientLoadObject.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Imposta la scena
        ClientManager.setScreen(stage, scene, width, height);

        // Ottieni gli stream di output e input per la comunicazione con il server
        ObjectOutputStream out = ClientManager.getOutputStream();
        ObjectInputStream in = ClientManager.getInputStream();

        // Ottieni i riferimenti ai pulsanti e all'area di testo
        Button button = (Button) fxmlLoader.getNamespace().get("selectButton");
        Button restartButton = (Button) fxmlLoader.getNamespace().get("restartButton");
        TextArea textArea = (TextArea) fxmlLoader.getNamespace().get("textArea");
        textArea.setEditable(false); // Imposta l'area di testo come non modificabile

        // Imposta l'azione del pulsante "selectButton" per selezionare il file
        button.setOnAction(event -> {
            String path = openFileChooser(stage); // Apre il file chooser per selezionare il file
            if (path != null) {
                try {
                    // Invia al server il codice 2 per indicare il tipo di richiesta e il path del file
                    out.writeObject(2);
                    out.writeObject(path);

                    // Riceve la risposta dal server: un messaggio di conferma e l'oggetto deserializzato
                    String ok = (String) in.readObject();
                    String object = (String) in.readObject();
                    textArea.setText(object); // Visualizza l'oggetto deserializzato nell'area di testo
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace(); // Gestisce eventuali eccezioni
                }
            }
        });

        // Imposta l'azione del pulsante "restartButton" per riavviare la connessione con il server
        restartButton.setOnAction(e -> {
            try {
                ClientManager.resetConnection(stage); // Riavvia la connessione al server
            } catch (IOException ex) {
                throw new RuntimeException(ex); // Gestisce l'errore durante il riavvio della connessione
            }
        });
    }

    /**
     * Apre una finestra di selezione del file per permettere all'utente di scegliere un file.
     * <p>
     * Utilizza il `FileChooser` per permettere all'utente di selezionare un file
     * contenente un oggetto serializzato. Filtra i file per estensione e ritorna
     * il percorso del file selezionato, oppure `null` se non viene selezionato alcun file.
     * </p>
     *
     * @param stage la finestra principale dell'applicazione
     * @return il percorso assoluto del file selezionato, oppure `null` se non è stato selezionato alcun file
     */
    private String openFileChooser(Stage stage) {
        // Crea un'istanza di FileChooser
        FileChooser fileChooser = new FileChooser();

        // Imposta il titolo della finestra del file chooser
        fileChooser.setTitle("Scegli l'oggetto da deserializzare");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));  // Cartella iniziale

        // Aggiungi filtri per le estensioni dei file
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Supported Files", "*.ser", "*.bin", "*.dat"),
                new FileChooser.ExtensionFilter("Serialized Files", "*.ser"),
                new FileChooser.ExtensionFilter("Binary Files", "*.bin"),
                new FileChooser.ExtensionFilter("Data Files", "*.dat")
        );

        // Mostra la finestra di dialogo per la selezione del file
        File selectedFile = fileChooser.showOpenDialog(stage);

        // Ritorna il percorso assoluto del file selezionato, oppure null se non è stato selezionato nessun file
        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        } else {
            return null;
        }
    }
}
