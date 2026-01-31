package Application;

import javafx.application.Application;
import javafx.stage.Stage;


/**
 * Classe principale de l'application JavaFX pour la gestion d'un réseau électrique.
 * Elle initialise la fenêtre principale et affiche la page d'accueil de l'interface.
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        AccueilScene accueil = new AccueilScene(primaryStage);
        primaryStage.setScene(accueil.getScene());
        primaryStage.setTitle("Gestion de Réseau Électrique - v1.0");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}