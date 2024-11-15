package code;

import code.server.MultiServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Classe principale responsabile della gestione del server.
 * <p>
 * Questa classe estende {@link javafx.application.Application} per fornire
 * un'interfaccia JavaFX che permette di monitorare e controllare le attività del server.
 * </p>
 *
 * <p>Si occupa di configurare l'interfaccia utente del server e inizializzare le risorse correlate.</p>
 *
 * @see javafx.application.Application
 */
public class Server extends Application {

    /** TextArea per visualizzare l'output della console. */
    private TextArea consoleOutput;

    /**
     * Inizializza e avvia l'interfaccia grafica dell'applicazione.
     *
     * @param stage la finestra principale dell'applicazione JavaFX
     * @throws IOException se il caricamento dell'interfaccia grafica fallisce
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Carica il file FXML per creare la scena dell'interfaccia grafica
        FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource("/resources/server.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Imposta l'icona della finestra
        Image icon = new Image(Server.class.getResourceAsStream("/resources/image2.png"));
        stage.getIcons().add(icon);

        // Recupera il TextArea e il pulsante dal file FXML
        consoleOutput = (TextArea) fxmlLoader.getNamespace().get("consoleOutput");
        Button startServerButton = (Button) fxmlLoader.getNamespace().get("startServerButton");

        // Impostazioni della finestra
        stage.setTitle("H-CLUS Server");
        stage.setMinWidth(400);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();

        // Reindirizza l'output della console
        redirectConsoleOutput();

        // Configura l'azione del pulsante per avviare il server
        startServerButton.setOnAction(event -> {
            startServerButton.setVisible(false); // Rimuove il pulsante dopo il click
            new Thread(() -> {
                try {
                    MultiServer.instanceMultiServer();
                    Platform.runLater(() -> consoleOutput.appendText("Server avviato con successo!\n"));
                } catch (Exception e) {
                    Platform.runLater(() -> consoleOutput.appendText("Errore: " + e.getMessage() + "\n"));
                }
            }).start();
        });

        // Permette di entrare in modalità schermo intero premendo F11
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case F11 -> stage.setFullScreen(!stage.isFullScreen());
            }
        });

        // Configura l'evento di chiusura per terminare l'applicazione
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Reindirizza l'output della console alla finestra di TextArea nell'interfaccia.
     * Questo metodo crea un flusso di byte e lo aggiorna continuamente per
     * mostrare i messaggi più recenti nella finestra.
     */
    private void redirectConsoleOutput() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        final int[] messageCount = {0};
        StringBuilder messageBuilder = new StringBuilder();

        // Thread che controlla periodicamente l'output della console
        Thread consoleReaderThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100);
                    String output = outputStream.toString();
                    if (!output.isEmpty()) {
                        messageBuilder.append(output);
                        outputStream.reset();

                        Platform.runLater(() -> {
                            consoleOutput.appendText(messageBuilder.toString());
                            messageCount[0]++;

                        });
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        consoleReaderThread.setDaemon(true);
        consoleReaderThread.start();
    }

    /**
     * Metodo principale per l'avvio dell'applicazione.
     *
     * @param args argomenti della riga di comando
     */
    public static void main(String[] args) {
        launch(args);
    }
}
