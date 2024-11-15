package code.clientScene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import code.ClientApplication;

import java.io.IOException;

/**
 * Gestore della connessione del client.
 * <p>
 * La classe `ClientConnect` è responsabile di caricare l'interfaccia utente per la connessione del client al server.
 * Gestisce anche la logica di connessione al server tramite i metodi definiti in `ClientManager`.
 * </p>
 */
public class ClientConnect {

    /**
     * Avvia la scena di connessione e gestisce l'azione del pulsante di connessione.
     * <p>
     * Questa funzione carica la finestra di connessione dal file FXML e assegna l'azione
     * al pulsante di connessione. Quando il pulsante viene premuto, si tenta di connettersi al server.
     * Se la connessione ha successo, si carica la scena successiva; altrimenti, viene mostrato un messaggio di errore.
     * </p>
     *
     * @param stage la finestra principale dell'applicazione
     * @param width larghezza della finestra
     * @param height altezza della finestra
     * @throws IOException se si verifica un errore nel caricamento dell'interfaccia utente o nella connessione
     */
    public void start(Stage stage, double width, double height) throws IOException {
        // Carica la scena di connessione dal file FXML
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/resources/client.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Imposta la scena nella finestra principale
        ClientManager.setScreen(stage, scene, width, height);

        // Ottieni il riferimento al pulsante di connessione e al testo di errore
        Button connectButton = (Button) fxmlLoader.getNamespace().get("connectButton");
        Text textError = (Text) fxmlLoader.getNamespace().get("textError");

        // Imposta l'azione del pulsante di connessione
        connectButton.setOnAction(event -> {
            try {
                // Prova a inizializzare la connessione al server
                ClientManager.initializeConnection(ClientManager.getIp(), ClientManager.getPort());

                // Se la connessione ha successo, carica la scena successiva
                ClientLoad clientLoad = new ClientLoad();
                clientLoad.start(stage, stage.getWidth(), stage.getHeight());
            } catch (IOException e) {
                // Se c'è un errore nella connessione, mostra il messaggio di errore
                textError.setText("Impossibile connettersi al Server");
            }
        });
    }
}
