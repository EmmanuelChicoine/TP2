import models.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

import javafx.util.Pair;

public class Main {
    final static int PORT = 1337;
    private final static String REGISTER_COMMAND = "INSCRIRE"; // Le mot à utiliser pour inscrire un étudiant à un cours
    private final static String LOAD_COMMAND = "CHARGER"; // Le mot à utiliser pour charger la liste des cours d'une session

    final private static String[] SESSIONS = {"Automne", "Hiver", "Ete"}; // Le nom des sessions
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
    // Variables nécessaire pour charger les cours de cours.txt sans passer par le serveur (je n'arrive pas à
    // transmettre la liste
    // Le chemin relatif vers le fichier de la liste des cours
    private static final String PATH_LISTE_COURS = "data/cours.txt";
    // Le chemin relatif vers le fichier de la liste des inscriptions
    private static final String PATH_LISTE_INSCRIPTION = "data/inscription.txt";
    private static BufferedReader reader;
    // Le client et le serveur ne communiquent pas bien et la connexion est inutilisable
    final private static boolean CONNEXION_FONCTIONNE = false;

    public static void main(String[] args) {
        try {
            // Ces trois variables ne sont pas nécessaires quand la liste des cours et des inscriptions n'est pas
            // demandée au serveur.
            /*client = new Socket(IP,PORT);
            objectOutputStream = new ObjectOutputStream(client.getOutputStream());
            objectInputStream = new ObjectInputStream(client.getInputStream());*/
            // Fin des varibales inutilisées

            System.out.println("*** Bienvenue au portail d'inscription de l'UDeM ***"); // Message d'accueil

            // Afficher la liste des cours d'une session et demander à l'utilisateur ce qu'il veut faire
            do {
                session = lireSession();                        // Lire la session dont il faut montrer les cours
                coursSession = chargerCoursSession(session);    // Charger la liste de cours
                montrerCours(coursSession, session);            // Consulter la liste
            } while (lireCommande().equals(LOAD_COMMAND)); // Tant que l'utilisateur veut voir la liste des cours
                                                           // d'une session
            // Quand l'utilisateur a fini de consulter les cours
            inscrire();

            scan.close();                                  // Fermer le scan et les streams
            /*objectOutputStream.close();
            objectInputStream.close();*/
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charger la liste des cours d'une session donnée.
     *
     * @param session La session en question
     * @return La liste
     */
    private static List<Course> chargerCoursSession(String session) throws ClassNotFoundException, IOException {
        List<Course> listeCours = new ArrayList<>();

        if (CONNEXION_FONCTIONNE) { // Partie inutilisée
            listeCours = chargerSessionAvecServeur(session);
        } else { // Partie utilisée
            listeCours = chargerSessionSansServeur(session);
        }

        return listeCours;
    }

    /**
     * (Cette fonction bogue et n'est présentement pas utilisée. Voir chargerSessionSansServeur pour ce qui
     * roule réellement.)
     *
     * Si la connexion avec le serveur fonctionne (ce qui n'est pas le cas), demander et recevoir la liste
     * des cours d'une session donnée.
     *
     * @param session la session voulue
     * @return la liste des cours
     */
    private static List<Course> chargerSessionAvecServeur(String session) throws IOException, ClassNotFoundException {
        List<Course> listeCours = new ArrayList<>();

        objectOutputStream.writeObject(new String(LOAD_COMMAND + " " + session)); // Faire la demande
        listeCours = (List<Course>) objectInputStream.readObject(); // (Ne fonctionne pas)

        return listeCours;
    }

    /**
     * (La fonction réellement utilisée pour charger les cours d'une session.)
     *
     * Charger la liste des cours d'une session donnée à partir du document dans ce projet, donc sans passer par
     * le serveur et objectInputStream/objectOutputStream.
     *
     * @param session la session voulue
     * @return la liste des cours
     */
    private static List<Course> chargerSessionSansServeur(String session) throws FileNotFoundException, IOException {
        List<Course> listeCours = new ArrayList<>();

        // reader du fichier texte avec l'info des cours
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(PATH_LISTE_COURS)));

        // Lire les cours dans le fichier et les mettre dans la liste coursDeLaSession
        String ligne;
        while ((ligne = reader.readLine()) != null) {
            // Enregistrer les données dans des variables séparées
            String[] ligneSplitee = ligne.split("\t");      // Séparer la ligne
            String sessionDuCours = ligneSplitee[2];                // session
            if (!sessionDuCours.equalsIgnoreCase(session))          // si le cours n'est pas de la session
                continue;                                           // demandée, passer au prochain
            String nom = ligneSplitee[1];                           // nom du cours
            String code = ligneSplitee[0];                          // code du cours

            listeCours.add(new Course(nom, code, session));     // Créer le cours et l'ajouter à la liste
        }

        return listeCours;
    }

    /**
     * Lister les cours d'une session sur la console.
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

        System.out.print(CHOIX_QUESTION);
        int idx = lireNbValide(3) - 1;
        return SESSIONS[idx]; // Lire le chiffre et retourner la session correspondante
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
        System.out.print(CHOIX_QUESTION);                                             // Demander la réponse

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
    private static RegistrationForm creerFormulaireInscription() throws ClassNotFoundException, IOException {
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
    private static Course lireCours() throws ClassNotFoundException, IOException {
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
    private static List<Course> chargerTousLesCours() throws ClassNotFoundException, IOException {
        List<Course> tousLesCours = new ArrayList<>(); // La liste retournée des cours de toutes les sessions

        for (int s = 0; s < SESSIONS.length; s++) {                                 // Pour chaque session
            List<Course> coursSession = chargerCoursSession(SESSIONS[s]); // L'enregistrer

            for (Course unCours : coursSession)                                        // Pour chaque cours
                tousLesCours.add(unCours);                                                  // L'ajouter à la
        }                                                                                   // liste principale

        return tousLesCours;
    }

    /**
     * Écrit les informations de l'étudiant et du cours dans le fichier inscription.txt, dans le dossier de
     * client_simple si la connexion avec le serveur ne fonctionne pas, et dans le dossier du serveur si elle
     * fonctionne.
     */
    private static void inscrire() throws ClassNotFoundException, IOException {
        RegistrationForm form = creerFormulaireInscription();
        if (CONNEXION_FONCTIONNE) { // Partie inutilisée car le client et le serveur ne communiquent pas bien
            try {
                objectOutputStream.writeObject(new String(REGISTER_COMMAND));
                objectOutputStream.writeObject(form);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        } else { // Partie utilisée: l'information est modifié dans le fichier dans le dossier du client
            handleRegistration(form);
        }
    }

    /**
     * Inscrit l'étudiant au cours souhaité (ajoute ses informations dans le fichier inscription.txt)
     *
     * @param form le formulaire d'inscription rempli
     */
    private static void handleRegistration(RegistrationForm form) {
        try {
            // Les streams pour le fichier de la liste des inscriptions
            BufferedWriter writer = new BufferedWriter(new FileWriter(PATH_LISTE_INSCRIPTION));
            writer.append(form.getCourse().getSession() + '\t' +         // session
                    form.getCourse().getCode() + '\t' +                             // code du cours
                    form.getMatricule() + '\t' +                                    // matricule
                    form.getPrenom() + '\t' +                                       // prénom
                    form.getNom() + '\t' +                                          // nom
                    form.getEmail());                                               // email
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}