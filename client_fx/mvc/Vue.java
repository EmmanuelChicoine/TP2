package mvc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import server.models.Course;

import java.util.List;

public class Vue extends HBox {
    /** Éléments de la moitié gauche de la fenêtre (lister les cours) **/
    private BorderPane gauche = new BorderPane(); // Le conteneur
    private Pane topGauche = new Pane(new Text("Liste des cours")); // Top : nom
    // Centre : liste des cours
    private VBox coursSession = new VBox(); // Conteneur de la liste des cours
    private HBox titreColonnes = new HBox(new Text("Code"), new Text("Cours")); // Noms des colonnes
    private VBox liste  = new VBox(titreColonnes, coursSession); // Conteneur des titres et de la liste
    private Pane centerGauche = new Pane(liste); // Conteneur du panneau
    // Bas : choix de la session
    private ObservableList<String> sessions = FXCollections.observableArrayList("Automne", "Hiver",
                                                                                       "Ete"); // Noms des sessions
    private ListView<String> listeBtnSession = new ListView<String>(sessions); // Liste des boutons pour choisir la session
    private Button btnCharger = new Button("charger"); // Bouton pour charger la liste des cours après avoir
                                                          // choisi une session
    private Pane bottomGauche = new Pane(listeBtnSession, btnCharger); // Conteneur

    /** Eléments de la moitié droite de la fenêtre (pour s'inscrire à un cours) **/
    private BorderPane droite = new BorderPane(); // Conteneur
    private Pane topDroite = new Pane(new Text("Formulaire d'inscription")); // Nom
    // Centre : formulaire
    private TextField textFieldPrenom = new TextField(); // Tous les TextField : prénom
    private TextField textFieldNom = new TextField();    //                      nom
    private TextField textFieldEmail = new TextField();  //                      email
    private TextField textFieldMatricule = new TextField(); //                   matricule
    private TextField textFieldCode = new TextField(); //                        code du cours
    private HBox sectionPrenom = new HBox(new Text("Prénom "), textFieldPrenom); // Sections d'entrée d'info : prénom
    private HBox sectionNom = new HBox(new Text("Nom "), textFieldNom);          //                            nom
    private HBox sectionEmail = new HBox(new Text("Email "), textFieldEmail);    //                            email
    private HBox sectionMatricule = new HBox(new Text("Matricule "), textFieldMatricule); //                 matricule
    private HBox sectionCode = new HBox(new Text("Code "), textFieldCode);//                                   code
    private Button btnEnvoyer = new Button("envoyer"); // Bouton pour envoyer le formulaire rempli
    private VBox formulaire = new VBox(sectionPrenom, sectionNom, sectionEmail, sectionMatricule, // Conteneur du
                                                                   sectionCode, btnEnvoyer);      // formulaire
    private Pane centreDroite = new Pane(formulaire); // Conteneur

    public Vue() {
        // Moitié gauche : afficher la liste des cours
        gauche.setTop(topGauche);       // Setter les panneaux : top -> nom
        gauche.setCenter(centerGauche); //                       centre -> liste des cours
        gauche.setBottom(bottomGauche); //                       bas -> choix de la session
        this.getChildren().add(gauche); // Ajouter ce panneau à la fenêtre

        // Moitié droite : formulaire d'inscription
        droite.setTop(topDroite);       // Setter les panneaux : top -> nom
        droite.setCenter(centreDroite); //                       centre -> formulaire
        this.getChildren().add(droite); // Ajouter ce panneau à la fenêtre
    }

    /** Getters **/
    public String getSession() { return this.listeBtnSession.getSelectionModel().getSelectedItem().toString(); }
    public Button getBtnCharger() { return this.btnCharger; }
    public Button getBtnEnvoyer() { return this.btnEnvoyer; }
    public String getNom() { return textFieldNom.getText(); }
    public String getPrenom() { return textFieldPrenom.getText(); }
    public String getEmail() { return textFieldEmail.getText(); }
    public String getMatricule() { return textFieldMatricule.getText(); }
    public String getCode() { return textFieldCode.getText(); }

    /** Setters **/
    public void setCoursSession(List<Course> coursDonnees) {
        coursSession.getChildren().clear(); // Vider la liste
        for (Course unCours: coursDonnees) { // Pour tous les cours de la session
            Text code = new Text(unCours.getCode());  // Code du cours
            Text nom  = new Text(unCours.getName());  // Nom du cours
            HBox elementVisuel = new HBox(code, nom); // Boîte avec le code et le nom

            coursSession.getChildren().add(elementVisuel); // Ajouter la boîte à la liste à l'écran
        }
    }
}
