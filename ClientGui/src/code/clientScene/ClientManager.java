package code.clientScene;

import code.ClientApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Gestore delle operazioni di connessione del client.
 * <p>
 * La classe `ClientManager` si occupa di avviare l'interfaccia utente del client,
 * gestire la connessione al server e fornire metodi statici per l'accesso ai flussi di input/output.
 * </p>
 */
public class ClientManager {

    /** Flusso di output per la comunicazione con il server */
    private static ObjectOutputStream out;

    /** Flusso di input per la ricezione di dati dal server */
    private static ObjectInputStream in;

    /** Socket per la connessione al server */
    private static Socket socket;

    /** Indirizzo IP del server */
    private static String ip;

    /** Porta del server */
    private static int port;

    /**
     * Inizializza e visualizza l'interfaccia grafica del client.
     *
     * @param stage la finestra principale dell'applicazione JavaFX
     * @param app riferimento all'istanza di `ClientApplication`
     * @param ip l'indirizzo IP del server
     * @param port la porta del server
     * @throws IOException se si verifica un errore durante il caricamento dell'interfaccia o la connessione
     */
    public void start(Stage stage, ClientApplication app, String ip, int port) throws IOException {
        // Imposta l'IP e la porta per la connessione al server
        ClientManager.ip = ip;
        ClientManager.port = port;

        // Carica l'interfaccia utente dal file FXML
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("/resources/client.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Configura la finestra principale
        stage.setTitle("H-CLUS Client");
        stage.setMinWidth(400);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();

        // Inizializza il client
        ClientConnect client = new ClientConnect();
        client.start(stage, stage.getWidth(), stage.getHeight());
    }

    /**
     * Configura e visualizza una scena specifica nella finestra.
     *
     * @param stage la finestra in cui mostrare la scena
     * @param scene la scena da visualizzare
     * @param width larghezza della finestra
     * @param height altezza della finestra
     */
    public static void setScreen(Stage stage, Scene scene, double width, double height) {
        stage.setTitle("H-CLUS Client");
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Restituisce il flusso di output per inviare dati al server.
     *
     * @return l'oggetto {@link ObjectOutputStream} per l'output
     * @throws IllegalStateException se la connessione non è stata inizializzata
     */
    public static ObjectOutputStream getOutputStream() {
        if (out == null) {
            throw new IllegalStateException("Connection has not been initialized.");
        }
        return out;
    }

    /**
     * Restituisce il flusso di input per ricevere dati dal server.
     *
     * @return l'oggetto {@link ObjectInputStream} per l'input
     * @throws IllegalStateException se la connessione non è stata inizializzata
     */
    public static ObjectInputStream getInputStream() {
        if (in == null) {
            throw new IllegalStateException("Connection has not been initialized.");
        }
        return in;
    }

    /**
     * Reimposta la connessione chiudendo il socket e riavviando l'applicazione client.
     *
     * @param stage la finestra principale dell'applicazione
     * @throws IOException se si verifica un errore durante la chiusura o la riapertura della connessione
     */
    public static void resetConnection(Stage stage) throws IOException {
        if (socket != null) {
            out.close();
            in.close();
            socket.close();
            ClientApplication client = new ClientApplication();
            client.start(stage);
        }
    }

    /**
     * Inizializza la connessione al server specificando l'IP e la porta.
     *
     * @param ip l'indirizzo IP del server
     * @param port la porta del server
     * @throws IOException se si verifica un errore durante la connessione
     */
    public static void initializeConnection(String ip, int port) throws IOException {
        if (socket == null || socket.isClosed()) {
            InetAddress addr = InetAddress.getByName(ip);
            System.out.println("Connecting to: " + addr);
            socket = new Socket(addr, port);
            System.out.println("Connected to: " + socket);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        }
    }

    /**
     * Restituisce l'indirizzo IP del server a cui il client è connesso.
     *
     * @return l'indirizzo IP del server
     */
    public static String getIp() {
        return ip;
    }

    /**
     * Restituisce la porta del server a cui il client è connesso.
     *
     * @return la porta del server
     */
    public static int getPort() {
        return port;
    }

}
