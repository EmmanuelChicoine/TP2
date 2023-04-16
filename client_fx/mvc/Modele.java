package mvc;

import javafx.util.Pair;
import server.models.Course;

import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static server.Server.LOAD_COMMAND;

public class Modele {

    final public static String[] SESSIONS = {"automne", "hiver", "ete"}; // Le nom des sessions
    private static ObjectInputStream objectInputStream;                  // Le objectInputStream du client
    private static ObjectOutputStream objectOutputStream;                // Le objectOutputStream du client


    /**
     * Charger la liste des cours d'une session donnée.
     *
     * @param session La session en question
     * @return La liste
     */
    static List<Course> chargerCoursSession(String session) throws IOException, ClassNotFoundException {
        Pair<String, String> pair = new Pair<String, String>(LOAD_COMMAND, session);
        objectOutputStream.writeObject(pair);
        objectOutputStream.flush();                     // Mettre la liste des cours de cette session dans
        return (List<Course>) objectInputStream.readObject(); // Enregistrer cette liste
    }
}
