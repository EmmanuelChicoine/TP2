import server.Server;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

import javafx.util.Pair;

import static server.Server.PORT;
import static server.Server.LOAD_COMMAND;
import static server.Server.REGISTER_COMMAND;

public class Main {

    final public static String[] SESSIONS = {"automne", "hiver", "ete"}; // Le nom des sessions
    final private static String CHOIX_QUESTION = "> Choix: ";            // Pour demander une réponse à l'étudiant
    final private static String IP = "127.0.0.1";                       // L'adresse IP du client
    private static Socket client;                                        // Le socket du client
    private static ObjectInputStream objectInputStream;                  // Le objectInputStream du client
    private static ObjectOutputStream objectOutputStream;                // Le objectOutputStream du client
    //private static Server server;                                        // Le serveur

    private static String session;  // La session dont l'étudiant veut voir les cours
    private static List<Course> coursSession; // Les cours d'une session
    private static Pair<String, String> pair;
    private static Scanner scan = new Scanner(System.in); // Pour lire les réponses de l'étudiant

    public static void main(String[] args) {
        try {
            client = new Socket(IP,PORT);
            objectOutputStream = new ObjectOutputStream(client.getOutputStream());
            objectInputStream = new ObjectInputStream(client.getInputStream());   // Et les streams du client

            System.out.println("*** Bienvenue au portail d'inscription de l'UDeM ***"); // Message d'accueil

            // Afficher la liste des cours d'une session et demander à l'utilisateur ce qu'il veut faire
            do {
                session = lireSession();                        // Lire la session dont il faut montrer les cours
                coursSession = chargerCoursSession(session);    // Charger la liste de cours
                montrerCours(coursSession, session);            // Consulter la liste
            } while (lireCommande().equals(LOAD_COMMAND)); // Tant que l'utilisateur veut voir la liste des cours
                                                           // d'une session
            // Quand l'utilisateur a fini de consulter les cours
            objectOutputStream.writeBytes(REGISTER_COMMAND);
            objectOutputStream.writeObject(creerFormulaireInscription());   // Lui faire remplir un formulaire

            scan.close();                                       // Fermer le scan
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Charger la liste des cours d'une session donnée.
     *
     * @param session La session en question
     * @return La liste
     */
    private static List<Course> chargerCoursSession(String session) {
        String[] commande = { LOAD_COMMAND, " ", session};
        try {
            objectOutputStream.writeObject(new String(LOAD_COMMAND+ " "+ session));
            List<Course> listeCours = (ArrayList<Course>) objectInputStream.readObject();
            return listeCours; // Enregistrer cette liste
        } catch (Exception e) {
            System.err.println("Les cours de la session " + session + " n'ont pas pu être chargé.");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lister les cours d'une session.
     *
     * @param coursSession Liste des cours d'une session
     * @param session Nom de la session
     */
    private static void montrerCours(List<Course> coursSession, String session) {
        System.out.println("Les cours offerts pendant la session d'" + session + " sont :"); // Message titre
        int c = 1; // Numéro du cours dans la liste
        for (Course unCours : coursSession)                                                  // Lister les cours
            System.out.println(c++ + ". " + unCours.getCode() + "\t" + unCours.getName());   // Numéro, code, nom
    }

    /**
     * Demande la session dont l'étudiant veut voir les cours.
     *
     * @return le nom de la session
     */
    private static String lireSession() {
        System.out.println("Veuillez choisir la session pour laquelle vous souhaiter consulter la liste des cours: \n" +
                "1. Automne \n" +
                "2. Hiver \n" +
                "3. Ete");

        return SESSIONS[lireNbValide(3)]; // Lire le chiffre et retourner la session correspondante
    }

    /**
     * L'utilisateur choisit ce qu'il veut faire : regarder les cours d'une session ou s'inscrire à un cours
     *
     * @return la commande
     */
    private static String lireCommande() {
        String commande;    // Ce que veut faire l'utilisateur (retournée)

        // Question
        System.out.println(CHOIX_QUESTION);                                             // Options:
        System.out.println("1. Consulter les cours offerts pour une autre session");        // Consulter liste
        System.out.println("2. Inscription à un cours");                                    // Inscription
        System.out.print(CHOIX_QUESTION);                                               // Demander réponse

        // Lire la réponse et déterminer la prochaine action
        if (lireNbValide(2) == 1)                  // Si 1
            commande = LOAD_COMMAND;                        // Consulter des cours
        else                                            // Sinon
            commande = REGISTER_COMMAND;                    // S'inscrire à un cours

        return commande;
    }

    /**
     * Demande les informations nécessaires à une inscription et les enregistre dans un object RegistrationForm.
     *
     * @return le formulaire
     */
    private static RegistrationForm creerFormulaireInscription() {
        System.out.print("Veuillez saisir votre prénom: ");         // Prénom
        String prenom = scan.next();
        System.out.print("Veuillez saisir votre nom: ");            // Nom
        String nom = scan.next();
        System.out.print("Veuillez saisir votre email: ");          // Email
        String email = scan.next();
        System.out.print("Veuillez saisir votre matricule: ");      // Matricule
        String matricule = scan.next();
        System.out.print("Veuillez saisir le code du cours: ");     // Code du cours
        Course cours = lireCours();

        return new RegistrationForm(prenom, nom, email, matricule, cours); // Retourner l'objet
    }

    /**
     * Demande à l'usager d'entrer le code d'un cours, repose la question si le code n'existe pas et retourne
     * le cours correspondant
     *
     * @return le cours auquel l'étudiant veut s'inscrire
     */
    private static Course lireCours() {
        Course cours;                                      // Le cours qui sera retourné
        List<Course> tousLesCours = chargerTousLesCours(); // Tous les cours de toutes les sessions

        do {
            String codeDonne = scan.next();                             // Lire le code

            cours = tousLesCours.stream().filter(unCours -> {           // Trouver le cours dans la liste
                        return unCours.getCode().equals(codeDonne);     // Retourner le cours si c'est un bon code
                    }
            ).findAny().orElse(null);                             // Si rien n'est trouvé, retourner null

            if (cours == null)                                          // Si le cours n'existe pas : message d'erreur
                System.out.println("Ce code n'apparaît pas dans la liste des cours disponibles." +
                        "Veuillez saisir un code existant: ");
        } while(cours == null); // Tant qu'un cours n'a pas été trouvé

        return cours;
    }

    /**
     * S'assure que l'entrée de l'utilisateur sur la ligne de commande est un entier entre 1 et un certain maximum
     *
     * @param max le maximum que l'étudiant peut donner
     * @return la réponse valide de l'utilisateur
     */
    private static int lireNbValide(int max) {
        int reponse;            // La réponse de l'utilisateur
        boolean valide = false; // Si la réponse est valide

        do {
            System.out.print(CHOIX_QUESTION);   // Demander d'écrire un choix
            reponse = scan.nextInt();           // Lire la réponse
            if (reponse >= 1 && reponse <= max) // Si la réponse donnée est entre 1 et le max
                valide = true;                      // Elle est valide
            else                                // Sinon: Message d'erreur
                System.out.println("Réponse invalide. Veuillez choisir parmi les options proposées.");
        } while (!valide);                  // Tant que la réponse est invalide

        return reponse;
    }

    /**
     * Charger tous les cours (de toutes les sessions) dans une liste.
     *
     * @return la liste de tous les cours
     */
    private static List<Course> chargerTousLesCours() {
        List<Course> tousLesCours = null; // La liste retournée des cours de toutes les sessions

        try {
            for (int s = 0; s < SESSIONS.length; s++) {                                 // Pour chaque session
                //server.handleEvents(LOAD_COMMAND, SESSIONS[s]);                            // Charger la liste des cours
                List<Course> coursSession = (List<Course>) objectInputStream.readObject(); // L'enregistrer

                for (Course unCours : coursSession)                                        // Pour chaque cours
                    tousLesCours.add(unCours);                                                  // L'ajouter à la
            }                                                                                   // liste principale
        } catch (Exception e) {
            System.err.println("Les cours n'ont pas pu être lus dans le fichier.");
            e.printStackTrace();
        }

        return tousLesCours;
    }
}