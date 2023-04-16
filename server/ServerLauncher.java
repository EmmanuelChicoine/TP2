
package main.java.server;

public class ServerLauncher {
    public final static int PORT = 1337; // localport

    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);                  // Créer le serveur sur le port local
            System.out.println("Server is running..."); // Dire que ça fonctionne
            server.run();                               // Exécuter les requêtes du client
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

