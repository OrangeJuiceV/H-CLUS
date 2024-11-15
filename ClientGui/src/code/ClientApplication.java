package code;

import code.clientScene.ClientManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import javafx.scene.image.Image;

/**
 * Classe principale dell'applicazione client che estende {@link javafx.application.Application}.
 * <p>
 * Questa classe avvia l'interfaccia grafica del client e stabilisce una connessione
 * alla parte server utilizzando i parametri IP e porta specificati.
 * </p>
 *
 * @see javafx.application.Application
 */
public class ClientApplication extends Application {

    /** Indirizzo IP del server a cui connettersi */
    private static String ip;

    /** Porta del server a cui connettersi */
    private static int port;

    /**
     * Metodo principale di avvio dell'interfaccia grafica dell'applicazione client.
     *
     * @param stage la finestra principale dell'applicazione JavaFX
     * @throws IOException se si verifica un errore di caricamento delle risorse o di connessione
     */
    @Override
    public void start(Stage stage) throws IOException {

    
        // Imposta l'icona della finestra
        Image icon = new Image(ClientApplication.class.getResourceAsStream("/resources/image.png"));
        stage.getIcons().add(icon);
        
        // Crea un'istanza del gestore client e avvia la connessione
        ClientManager client = new ClientManager();
        client.start(stage, this, ip, port);
    }

    /**
     * Metodo principale che avvia l'applicazione.
     * <p>
     * Verifica la presenza dei parametri IP e porta passati tramite argomenti
     * della riga di comando e lancia l'applicazione JavaFX.
     * </p>
     *
     * @param args argomenti della riga di comando, in cui il primo deve essere l'IP e il secondo la porta
     */
    public static void main(String[] args) {
        // Verifica che siano stati passati almeno due argomenti (IP e porta)
        if (args.length < 2) {
            System.err.println("Usage: java ClientApplication <IP> <Port>");
            System.exit(1);
        }

        // Imposta i valori dell'indirizzo IP e della porta in base agli argomenti
        ip = args[0];
        port = Integer.parseInt(args[1]);

        // Avvia l'applicazione JavaFX
        launch(args);
    }

}
