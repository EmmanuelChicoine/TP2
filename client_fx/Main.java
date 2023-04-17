import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mvc.Controleur;
import mvc.Modele;
import mvc.Vue;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Modele leModele = new Modele();                             // Variables: le modèle
        Vue laVue = new Vue();                                      //            la vue
        Controleur leControleur = new Controleur(leModele, laVue);  //            le contrôleur

        Scene scene = new Scene(laVue, 500, 400);            //            la scène : vue et dimensions

        stage.setScene(scene);                                      // Mettre la scène sur le stage
        stage.setTitle("Inscription UdeM");                         // Le titre de la fenêtre
        stage.show();                                               // Montrer le stage
    }

    public static void main(String[] args) {
        launch(args);
    }
}