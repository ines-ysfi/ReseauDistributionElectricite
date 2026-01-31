package Application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Représente la scène d'accueil principale de l'application.
 * 
 * Cette scène permet à l'utilisateur de :
 * - construire un réseau électrique manuellement,
 * - charger un réseau depuis un fichier,
 * - quitter l'application.
 * 
 * Elle sert de point d'entrée graphique à l'application.
 */
public class AccueilScene {

    private Scene scene;
    private Stage primaryStage;

    /**
     * Crée la scène d'accueil associée à la fenêtre principale.
     * 
     * @param primaryStage la fenêtre principale de l'application
     */
    public AccueilScene(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createScene();
    }

    /**
     * Construit et initialise tous les éléments graphiques de la scène
     * (labels, boutons, layouts et actions).
     */
    private void createScene() {
        Label titre = new Label("Bienvenue dans l'application de gestion de réseau électrique");
        titre.setFont(Font.font("Arial", 24));
        titre.setTextFill(Color.DARKBLUE);
        titre.setWrapText(true);

        Label sousTitre = new Label("Choisissez une option pour commencer");
        sousTitre.setFont(Font.font("Arial", 16));
        sousTitre.setTextFill(Color.DARKSLATEGRAY);

        Button btnManuel = createStyledButton("Construire Réseau Manuellement", "#4CAF50");
        Button btnFichier = createStyledButton("Charger Réseau depuis Fichier", "#2196F3");
        Button btnQuitter = createStyledButton("Quitter", "#F44336");

        Label descManuel = new Label(
            "Créez votre réseau en ajoutant des générateurs, maisons et connexions manuellement"
        );
        descManuel.setFont(Font.font("Arial", 12));
        descManuel.setTextFill(Color.GRAY);
        descManuel.setWrapText(true);

        Label descFichier = new Label(
            "Chargez un réseau existant depuis un fichier texte et optimisez-le"
        );
        descFichier.setFont(Font.font("Arial", 12));
        descFichier.setTextFill(Color.GRAY);
        descFichier.setWrapText(true);

        btnManuel.setOnAction(e -> {
            ReseauManuelScene manuelScene = new ReseauManuelScene(primaryStage);
            primaryStage.setScene(manuelScene.getScene());
        });

        btnFichier.setOnAction(e -> {
            ChargerFichierScene fichierScene = new ChargerFichierScene(primaryStage);
            primaryStage.setScene(fichierScene.getScene());
        });

        btnQuitter.setOnAction(e -> primaryStage.close());

        VBox optionManuel = new VBox(10, btnManuel, descManuel);
        optionManuel.setAlignment(Pos.CENTER);
        optionManuel.setPadding(new Insets(20));
        optionManuel.setStyle(
            "-fx-background-color: #E8F5E8; " +
            "-fx-border-color: #4CAF50; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5;"
        );

        VBox optionFichier = new VBox(10, btnFichier, descFichier);
        optionFichier.setAlignment(Pos.CENTER);
        optionFichier.setPadding(new Insets(20));
        optionFichier.setStyle(
            "-fx-background-color: #E3F2FD; " +
            "-fx-border-color: #2196F3; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 5;"
        );

        HBox optionsBox = new HBox(30, optionManuel, optionFichier);
        optionsBox.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(30, titre, sousTitre, optionsBox, btnQuitter);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(40));
        mainLayout.setBackground(
            new Background(
                new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)
            )
        );

        scene = new Scene(mainLayout, 1000, 600);
    }

    /**
     * Crée un bouton avec une couleur donnée et un effet visuel
     * lors du survol de la souris.
     * 
     * @param text le texte affiché sur le bouton
     * @param color la couleur principale du bouton (code hexadécimal)
     * @return le bouton configuré
     */
    private Button createStyledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", 14));
        btn.setPrefWidth(300);
        btn.setPrefHeight(50);
        btn.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-border-radius: 5; " +
            "-fx-font-weight: bold;"
        );

        btn.setOnMouseEntered(e ->
            btn.setStyle(
                "-fx-background-color: derive(" + color + ", -20%); " +
                "-fx-text-fill: white; " +
                "-fx-border-radius: 5; " +
                "-fx-font-weight: bold;"
            )
        );

        btn.setOnMouseExited(e ->
            btn.setStyle(
                "-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-border-radius: 5; " +
                "-fx-font-weight: bold;"
            )
        );

        return btn;
    }

    /**
     * Retourne la scène d'accueil.
     * 
     * @return la scène associée à cet écran
     */
    public Scene getScene() {
        return scene;
    }
}
