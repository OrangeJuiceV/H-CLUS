package code.server;

import code.clustering.InvalidClustersNumberException;
import code.clustering.InvalidDepthException;
import code.data.Data;
import code.data.InvalidSizeException;
import code.data.NoDataException;
import code.clustering.HierachicalClusterMiner;
import code.database.DatabaseConnectionException;
import code.database.DbAccess;
import code.distance.ClusterDistance;
import code.distance.SingleLinkDistance;
import code.distance.AverageLinkDistance;
import java.io.IOException;               
import java.io.ObjectOutputStream;     
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;            
import java.sql.DatabaseMetaData;      
import java.sql.ResultSet;                
import java.sql.SQLException;           
import java.util.ArrayList;            
import java.util.List;
import java.net.Socket;

/**
* Classe che gestisce le connessioni con i client.
*/
class ServerOneClient extends Thread {
    /** Il socket del client */
    private final Socket clientSocket;

    /** Stream di output per il client */
    private final ObjectOutputStream out;

    /** Stream di input per il client */
    private final ObjectInputStream in;

    /** Oggetto Data che memorizza i dati caricati */
    private Data data;

    /**
     * Costruttore per il gestore client.
     *
     * @param socket il socket del client
     * @throws IOException se si verifica un errore di I/O
     */
     public ServerOneClient(Socket socket) throws IOException {
            this.clientSocket = socket;
            this.out = new ObjectOutputStream(clientSocket.getOutputStream());
            this.in = new ObjectInputStream(clientSocket.getInputStream());
            this.start();
     }

     /**
      * Metodo che gestisce le richieste del client.
      */
     @Override
     public void run() {
          try {
              while (true) {
                  DbAccess db = new DbAccess();
                  int requestType = (int) in.readObject();
                  switch (requestType) {
                      case 0:
                          // Carica dati dal database
                          try {
                              sendTableNames(db.getConnection());
                          } catch (DatabaseConnectionException e) {
                              e.printStackTrace();
                          }
                          handleLoadData();
                          break;
                      case 1:
                          // Esegui clustering
                          handleClustering();
                          break;
                      case 2:
                          // Carica dendrogramma da file
                          handleLoadDendrogramFromFile();
                          break;
                      default:
                          out.writeObject("Tipo di richiesta non valido");
                          break;
                  }
              }
          } catch (IOException e) {
                System.out.println("Disconnessione client: " + clientSocket);
          } catch (ClassNotFoundException e) {
                e.printStackTrace();
          } finally {
              try {
                  clientSocket.close();
                  out.close();
                  in.close();
              } catch (IOException e) {
                  System.err.println("Errore nella chiusura del socket o degli ObjectStream");
              }
          }
     }


    private void sendTableNames(Connection connection) throws IOException {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            List<String> tableNames = new ArrayList<>();
            while (tables.next()) {
                tableNames.add(tables.getString("TABLE_NAME"));
            }

            out.writeObject(tableNames); // Send table names list

        } catch (SQLException e) {
            out.writeObject("Errore durante l'ottenimento dei nomi delle tabelle: " + e.getMessage());
        }
    }

    /**
     * Gestisce il caricamento dei dati dal database.
     *
     * @throws IOException se si verifica un errore di I/O
     * @throws ClassNotFoundException se la classe non è trovata
     */
    private void handleLoadData() throws IOException, ClassNotFoundException {
        String tableName = (String) in.readObject();
        try {
            this.data = new Data(tableName);
            out.writeObject("OK");
        } catch (NoDataException e) {
            out.writeObject(e.getMessage());
        }
    }

    /**
     * Gestisce l'operazione di clustering.
     *
     * @throws IOException se si verifica un errore di I/O
     * @throws ClassNotFoundException se la classe non è trovata
     */
    private void handleClustering() throws IOException, ClassNotFoundException {
        if (data == null) {
            out.writeObject("Dati non caricati");
            return;
        }

        int depth = (int) in.readObject();
        int distanceType = (int) in.readObject();

        try {
            HierachicalClusterMiner clustering = new HierachicalClusterMiner(depth);
            ClusterDistance distance = distanceType == 1 ? new SingleLinkDistance() : new AverageLinkDistance();

            clustering.mine(data, distance);

            out.writeObject("OK");
            out.writeObject(clustering.toString(data));
            String fileName = (String) in.readObject();
            clustering.salva(fileName);
        } catch (InvalidSizeException | InvalidClustersNumberException | IOException |
                 InvalidDepthException e) {
            out.writeObject(e.getMessage());
        }
    }

    /**
     * Gestisce il caricamento del dendrogramma da un file.
     *
     * @throws IOException se si verifica un errore di I/O
     * @throws ClassNotFoundException se la classe non è trovata
     */
    private void handleLoadDendrogramFromFile() throws IOException, ClassNotFoundException {
        String fileName = (String) in.readObject();
        try {
            HierachicalClusterMiner clustering = HierachicalClusterMiner.loadHierachicalClusterMiner(fileName);
            if (data == null) {
                out.writeObject("Dati non caricati");
                return;
            }

            if (clustering.getDepth() > data.getNumberOfExample()) {
                out.writeObject("Numero di esempi maggiore della profondità del dendrogramma!");
            } else {
                out.writeObject("OK");
                out.writeObject(clustering.toString(data));
            }
        } catch (FileNotFoundException e) {
            out.writeObject("File non trovato: " + e.getMessage());
        } catch (IOException | ClassNotFoundException | InvalidDepthException e) {
            out.writeObject(e.getMessage());
        }
    }
}

