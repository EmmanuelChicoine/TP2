package mvc;

import javafx.util.Pair;
import server.models.Course;

import java.io.IOException;

import java.util.List;

public class Controleur {

    private Modele modele; // Modèle du client
    private Vue vue; // mvc.Vue de l'application

    private Pair<String, String> pair;
    private List<Course> coursSession;
    private String session = null;
    private String nouvelleSession = null;

    public Controleur(Modele modele, Vue vue) {
        this.modele = modele;
        this.vue = vue;

        // Charger la liste des cours d'une session
        this.vue.getBtnCharger().setOnAction((action) -> {
            nouvelleSession = vue.getSession(); // Lire la session demandée
            // Si ce n'est pas la même session qu'à la dernière demande : changer la liste dans la vue
            if (!session.equals(nouvelleSession))
                try {
                    session = nouvelleSession;                          // Mettre à jour 'session'
                    coursSession = Modele.chargerCoursSession(session); // Charger les cours de la nouvelle session
                    vue.setCoursSession(coursSession);                  // Mettre à jour la vue
                } catch (ClassNotFoundException e) {
                    System.err.println("La liste des cours d'une session n'a pas pu être chargée par le modèle.");
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });
    }
}
