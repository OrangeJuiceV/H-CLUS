package code.clientScene;

import code.ClientApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Classe che gestisce l'interfaccia utente per il salvataggio di oggetti.
 * <p>
 * La classe `ClientSaveObject` permette all'utente di visualizzare un oggetto deserializzato,
 * quindi di inserire un nome per il file in cui desidera salvare l'oggetto. Il nome del file
 * deve avere una delle estensioni supportate (.bin, .ser, .dat). Dopo il salvataggio, la connessione
 * con il server viene reimpostata per accettare ulteriori richieste.
 * </p>
 */
public class ClientSaveObject {

    /**
     * Avvia la scena che permette all'utente di inserire il nome del file per salvare l'oggetto.
     * <p>
     * La scena contiene un campo di testo per inserire il nome del file, un'area di testo per
     * visualizzare l'oggetto da salvare e un pulsante per confermare il salvataggio. Se il nome
     * del file è valido (con estensione .bin, .ser o .dat), l'oggetto viene serializzato e inviato
     * al server per il salvataggio. Se il nome del file non è valido o si verifica un errore, viene
     * visualizzato un messaggio di errore.
     * </p>
     *
     * @param stage la finestra principale dell'applicazione
     * @param width larghezza della finestra
     * @param height altezza della finestra
     * @throws IOException se si verifica un errore nella comunicazione con il server o nel caricamento dell'interfaccia utente
     */
    public void start(Stage stage, double width, double height) throws IOException {
        // Carica la scena clientSaveObject dal file FXML
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/resources/clientSaveObject.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Imposta la scena
        ClientManager.setScreen(stage, scene, width, height);

        // Ottieni gli stream di input e output per la comunicazione con il server
        ObjectOutputStream out = ClientManager.getOutputStream();
        ObjectInputStream in = ClientManager.getInputStream();

        // Ottieni i componenti dell'interfaccia utente
        TextField text = (TextField) fxmlLoader.getNamespace().get("text"); // Campo di testo per il nome del file
        Button saveButton = (Button) fxmlLoader.getNamespace().get("saveButton"); // Pulsante per salvare l'oggetto
        Text textError = (Text) fxmlLoader.getNamespace().get("textError"); // Text per visualizzare gli errori
        TextArea textArea = (TextArea) fxmlLoader.getNamespace().get("textArea"); // Area di testo per visualizzare l'oggetto
        textArea.setEditable(false); // Impedisce la modifica dell'area di testo

        try {
            // Leggi l'oggetto inviato dal server e visualizzalo nell'area di testo
            String reader = (String) in.readObject();
            textArea.setText(reader);

            // Gestisci l'azione del pulsante "Salva"
            saveButton.setOnAction(event -> {
                try {
                    String fileName = text.getText(); // Ottieni il nome del file inserito dall'utente
                    // Controlla se il nome del file ha una delle estensioni supportate
                    if (fileName.endsWith(".bin") || fileName.endsWith(".ser") || fileName.endsWith(".dat")) {
                        // Se il nome è valido, invia il nome del file al server per il salvataggio
                        out.writeObject(fileName);
                        ClientManager.resetConnection(stage); // Resetta la connessione per permettere altre richieste
                    } else {
                        // Se il nome del file non è valido, mostra un errore
                        textError.setText("Il nome del file deve terminare con .bin | .ser | .dat");
                    }
                } catch (IOException e) {
                    // Gestione degli errori di comunicazione con il server
                    textError.setText("Errore nella comunicazione con il server");
                }
            });
        } catch (ClassNotFoundException | IOException e) {
            // Gestione degli errori nella lettura dell'oggetto dal server
            textError.setText("Errore nella comunicazione con il server");
        }
    }
}
