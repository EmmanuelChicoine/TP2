package main.java.server;

import javafx.util.Pair;
import main.java.server.models.Course;
import main.java.server.models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * TODO: documentation à ce niveau?
 */
public class Server {

    public final static String REGISTER_COMMAND = "INSCRIRE"; // Le mot à utiliser pour inscrire un étudiant à un cours
    public final static String LOAD_COMMAND = "CHARGER"; // Le mot à utiliser pour charger la liste des cours d'une session
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private BufferedReader reader;
    private final ArrayList<EventHandler> handlers;
    // Le chemin relatif vers le fichier de la liste des cours
    private final String PATH_LISTE_COURS = "data/cours.txt";
    // Le chemin relatif vers le fichier de la liste des inscriptions
    private final String PATH_LISTE_INSCRIPTION = "data/inscription.txt";


    /**
     * Définit le serveur avec le port fourni en paramètre et une file d'attente de 1.
     * Crée une liste vide pour les 'handlers'.
     * TODO : this::handleEvents?
     *
     * @param port // Numéro du port du client
     * @throws IOException
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1); // Création du serveur
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents); // TODO: qcq ça veut dire 'this::handleEvents'
    }

    /**
     * Ajoute le 'EventHandler' spécifié aux 'handlers'.
     *
     * @param h nouveau handler
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     * Chercher dans tous les 'handler's existant
     *
     * @param cmd commande
     * @param arg session
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Définit 'client', écoute ses commandes puis le déconnecte.
     */
    public void run() {
        while (true) {
            try {
                client = server.accept(); // Attendre la connexion d'un client et enregistrer ce client
                System.out.println("Connecté au client: " + client); // Indiquer le client sur la console
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen(); // Attendre la prochaine commande
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prend l'information donnée par le client et la reformate avant de la passer à alertHandlers
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Prend l'information de la ligne de commande et la met dans un objet compréhensible pour le reste des méthodes
     *
     * @param line ce qui a été écrit sur la ligne de commande
     * @return l'objet qui contient la commande et ses arguments
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Ferme le output stream et le input stream du serveur et ferme le 'client'.
     *
     * @throws IOException
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Inscrit le client à un cours ou lui montre les cours d'une session précisée, selon ce que demande le client.
     *
     * @param cmd commande
     * @param arg arguments (session)
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) { // Si le client demande de s'inscrire à un cours
            handleRegistration(); // L'inscrire
        } else if (cmd.equals(LOAD_COMMAND)) { // Si le client demande de montrer les cours d'une session
            handleLoadCourses(arg); // Les montrer
        }
    }

    /**
     * Lire un fichier texte contenant des informations sur les cours et les transformer en liste d'objets 'Course'.
     * La méthode filtre les cours par la session spécifiée en argument.
     * Ensuite, elle renvoie la liste des cours pour une session au client en utilisant l'objet 'objectOutputStream'.
     * La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     *
     * @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) {
        try {
            this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(PATH_LISTE_COURS)));
            List<Course> coursDeLaSession = new ArrayList<>();

            String ligne;
            while ((ligne = this.reader.readLine()) != null) {
                String[] ligneSplitee = ligne.split("\t");
                String nom = ligneSplitee[1];
                String code = ligneSplitee[0];
                String session = ligneSplitee[2];

                coursDeLaSession.add(new Course(nom, code, session));
            }

            objectOutputStream.writeObject(coursDeLaSession); // Donner la liste au client
        } catch (FileNotFoundException exception) {
            System.out.println("Le fichier de la liste des cours n'a pas été trouvé à l'adresse: " + PATH_LISTE_COURS);
            exception.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
            System.out.println(exception.getMessage());
        }

    }

    /**
     * Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     * et renvoyer un message de confirmation au client.
     * La méthode gère les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {
        try {
            // Le formulaire d'inscription envoyé par le client
            RegistrationForm form = (RegistrationForm) objectInputStream.readObject();
            // Les streams pour le fichier de la liste des inscriptions
            FileOutputStream fileOutputStream = new FileOutputStream(new File(PATH_LISTE_INSCRIPTION));
            DataOutputStream output = new DataOutputStream(fileOutputStream);

            // Écrire les infos, séparées par des tabulations, et un retour à la ligne
            output.writeBytes (form.getCourse().getSession() + '\t' +            // session
                    form.getCourse().getCode() + '\t' +                             // code du cours
                    form.getMatricule() + '\t' +                                    // matricule
                    form.getPrenom() + '\t' +                                       // prénom
                    form.getNom() + '\t' +                                          // nom
                    form.getEmail() + '\n');                                        // email
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur lors de la lecture du formulaire");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
