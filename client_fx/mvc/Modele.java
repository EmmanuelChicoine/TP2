package mvc;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.lang.ClassNotFoundException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static server.Server.LOAD_COMMAND;
import static server.Server.PORT;

public class Modele {
    private final static String REGISTER_COMMAND = "INSCRIRE"; // Le mot à utiliser pour inscrire un étudiant à un cours
    final public static String[] SESSIONS = {"Automne", "Hiver", "Ete"}; // Le nom des sessions
    final private static String IP = "127.0.0.1";                       // L'adresse IP du client
    private static Socket client;                                        // Le socket du client
    private static ObjectInputStream objectInputStream;                  // Le objectInputStream du client
    private static ObjectOutputStream objectOutputStream;                // Le objectOutputStream du client
    // Variables nécessaire pour charger les cours de cours.txt sans passer par le serveur (je n'arrive pas à
    // transmettre la liste
    // Le chemin relatif vers le fichier de la liste des cours
    private static final String PATH_LISTE_COURS = "data/cours.txt";
    // Le chemin relatif vers le fichier de la liste des inscriptions
    private static final String PATH_LISTE_INSCRIPTION = "data/inscription.txt";
    private static BufferedReader reader;
    // Le client et le serveur ne communiquent pas bien et la connexion est inutilisable
    final private static boolean CONNEXION_FONCTIONNE = false;

    public Modele() {
        try {
            client = new Socket(IP, PORT);
            objectOutputStream = new ObjectOutputStream(client.getOutputStream());
            objectInputStream = new ObjectInputStream(client.getInputStream());   // Et les streams du client
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
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
    private List<Course> chargerSessionAvecServeur(String session) throws IOException, ClassNotFoundException {
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
    private List<Course> chargerSessionSansServeur(String session) throws FileNotFoundException, IOException {
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
     * Écrit les informations de l'étudiant et du cours dans le fichier inscription.txt, dans le dossier de
     * client_simple si la connexion avec le serveur ne fonctionne pas, et dans le dossier du serveur si elle
     * fonctionne.
     */
    public void inscrire(RegistrationForm form) throws ClassNotFoundException, IOException {
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
    private void handleRegistration(RegistrationForm form) {
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

    /**
     * Charger tous les cours (de toutes les sessions) dans une liste.
     *
     * @return la liste de tous les cours
     */
    public List<Course> chargerTousLesCours() throws ClassNotFoundException, IOException {
        List<Course> tousLesCours = new ArrayList<>(); // La liste retournée des cours de toutes les sessions

        for (int s = 0; s < SESSIONS.length; s++) {                                 // Pour chaque session
            List<Course> coursSession = chargerCoursSession(SESSIONS[s]); // L'enregistrer

            for (Course unCours : coursSession)                                        // Pour chaque cours
                tousLesCours.add(unCours);                                                  // L'ajouter à la
        }                                                                                   // liste principale

        return tousLesCours;
    }

    /**
     * Charger la liste des cours d'une session donnée.
     *
     * @param session La session en question
     * @return La liste
     */
    public List<Course> chargerCoursSession(String session) throws ClassNotFoundException, IOException {
        List<Course> listeCours = new ArrayList<>();

        if (CONNEXION_FONCTIONNE) { // Partie inutilisée
            listeCours = chargerSessionAvecServeur(session);
        } else { // Partie utilisée
            listeCours = chargerSessionSansServeur(session);
        }

        return listeCours;
    }

    /**
     * Cherche parmi la liste des cours et retourne l'objet qui correspond au code donné.
     *
     * @param code le code donné
     * @return le cours
     */
    public Course trouverCours(String code) {
        try {
            List<Course> tousLesCours = chargerTousLesCours();
            Course cours;

            cours = tousLesCours.stream().filter(unCours -> {           // Trouver le cours dans la liste
                        return unCours.getCode().equals(code);          // Retourner le cours si c'est un bon code
                    }
            ).findAny().orElse(null);                             // Si rien n'est trouvé, retourner null
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
