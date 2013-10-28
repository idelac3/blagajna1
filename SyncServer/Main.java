package SyncServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        
        /**
         * Default SyncServer port.
         */
        int port = 5002;                      
        
        /**
         * Max. number of client connections.
         */
        int maxConnections = 0;
        
        /**
         * Connection counter.
         */
        int i = 0;

        /**
         * Root directory, where uploads and downloads take action.
         */
        String rootDir = System.getProperty("user.dir");

        
        try {
            
            ServerSocket listener = new ServerSocket(port);
            Socket clientSocket;
            
            while((i++ < maxConnections) || (maxConnections == 0)) {
            
                SyncServer syncServer;
                System.out.println("Listening on: "
                        + listener.getInetAddress().getHostAddress() + ":" + listener.getLocalPort());
                clientSocket = listener.accept();
                syncServer = new SyncServer(clientSocket, rootDir);
                Thread t = new Thread(syncServer);
                t.start();
                System.out.println("Connection from: "
                        + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
                
            }
            
            listener.close();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

}
