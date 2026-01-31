package Application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import reseau.*;
import io.ReseauReader;
import io.ReseauWriter;
import optimisation.OptimisateurReseau;
import java.io.File;
import java.io.IOException;

/**
 * Page permettant de charger un réseau électrique depuis un fichier,
 * d'afficher ses caractéristiques, de calculer son coût, de l'optimiser
 * et de le sauvegarder.
 *
 * Cette scène permet également de définir dynamiquement le paramètre λ (lambda)
 * utilisé dans le calcul du coût du réseau.
 */
public class ChargerFichierScene {

    private Scene scene;
    private Stage primaryStage;
    private Reseau reseau;
    private TextArea displayArea;
    private Label infoLabel;
    private Button btnAfficherCout;
    private Button btnOptimiser;
    private Button btnSauvegarder;
    private TextField lambdaField;

    /**
     * Construit la page de chargement de fichier.
     *
     * @param primaryStage La fenêtre principale de l'application.
     */
    public ChargerFichierScene(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.reseau = null;
        createScene();
    }

    /**
     * Crée et initialise tous les composants graphiques de la page.
     */
    private void createScene() {

        Label titre = new Label("Chargement de Réseau depuis Fichier");
        titre.setFont(Font.font("Arial", 18));
        titre.setTextFill(Color.DARKBLUE);

        HBox lambdaPanel = new HBox(10);
        lambdaPanel.setAlignment(Pos.CENTER);
        lambdaPanel.setPadding(new Insets(10, 0, 10, 0));

        Label lambdaLabel = new Label("Paramètre λ (lambda):");
        lambdaLabel.setFont(Font.font("Arial", 12));
        lambdaLabel.setTextFill(Color.DARKRED);

        lambdaField = new TextField("10.0");
        lambdaField.setPrefWidth(80);
        lambdaField.setPromptText("ex: 10.0");
        lambdaField.setDisable(true);

        Button btnSetLambda = new Button("Définir");
        btnSetLambda.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");
        btnSetLambda.setOnAction(e -> definirLambda());
        btnSetLambda.setDisable(true);

        lambdaPanel.getChildren().addAll(lambdaLabel, lambdaField, btnSetLambda);

        infoLabel = new Label("Aucun fichier chargé");
        infoLabel.setFont(Font.font("Arial", 12));
        infoLabel.setTextFill(Color.DARKRED);

        displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setPrefHeight(400);
        displayArea.setPrefWidth(500);
        displayArea.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 12px;");

        Button btnCharger = createActionButton("Charger un fichier");
        btnAfficherCout = createActionButton("Afficher Coût");
        btnOptimiser = createActionButton("Optimiser Automatiquement");
        btnSauvegarder = createActionButton("Sauvegarder sous...");
        Button btnRetour = createActionButton("Retour à l'accueil");
        Button btnQuitter = createActionButton("Quitter l'application");

        enableButtons(false);

        btnCharger.setOnAction(e -> chargerFichier());

        btnAfficherCout.setOnAction(e -> {
            if (reseau != null) {
                afficherCout();
            }
        });

        btnOptimiser.setOnAction(e -> {
            if (reseau != null) {
                optimiserReseau();
            }
        });

        btnSauvegarder.setOnAction(e -> {
            if (reseau != null) {
                sauvegarderReseau();
            }
        });

        btnRetour.setOnAction(e -> {
            AccueilScene accueil = new AccueilScene(primaryStage);
            primaryStage.setScene(accueil.getScene());
        });

        btnQuitter.setOnAction(e -> primaryStage.close());

        VBox buttonBox = new VBox(10,
                btnCharger, btnAfficherCout, btnOptimiser,
                btnSauvegarder, btnRetour, btnQuitter);

        HBox mainLayout = new HBox(20);
        mainLayout.getChildren().addAll(buttonBox, displayArea);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        VBox root = new VBox(10, titre, lambdaPanel, infoLabel, mainLayout);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        scene = new Scene(root, 1100, 700);
    }

    /**
     * Définit la valeur du paramètre λ pour le réseau chargé.
     */
    private void definirLambda() {
        if (reseau == null) {
            showAlert("Erreur", "Chargez d'abord un réseau!");
            return;
        }

        try {
            double lambda = Double.parseDouble(lambdaField.getText());
            if (lambda < 0) {
                showAlert("Erreur", "λ doit être positif ou nul!");
                return;
            }
            reseau.setLambda(lambda);
            displayArea.appendText("✓ Paramètre λ défini à: " + lambda + "\n");
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Valeur de λ invalide! Utilisez un nombre (ex: 10.0)");
        }
    }

    /**
     * Crée un bouton utilisé dans l'interface.
     *
     * @param text Le texte affiché sur le bouton.
     * @return Le bouton configuré.
     */
    private Button createActionButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(250);
        btn.setPrefHeight(40);
        btn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #F57C00; -fx-text-fill: white;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;"));
        return btn;
    }

    /**
     * Ouvre un explorateur de fichiers et charge un réseau depuis un fichier texte.
     */
    private void chargerFichier() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger un réseau");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                ReseauReader reader = new ReseauReader();
                reseau = reader.lireFichier(file.getAbsolutePath());
                reseau.setLambda(10.0);

                infoLabel.setText("Fichier chargé: " + file.getName());
                infoLabel.setTextFill(Color.DARKGREEN);

                enableButtons(true);
                lambdaField.setDisable(false);
                lambdaField.setText("10.0");

                for (javafx.scene.Node node : ((HBox) lambdaField.getParent()).getChildren()) {
                    if (node instanceof Button) {
                        node.setDisable(false);
                    }
                }

                displayArea.clear();
                displayArea.appendText("✓ Fichier chargé avec succès!\n\n");
                displayArea.appendText("Paramètre λ défini à 10.0 par défaut\n");
                displayArea.appendText("Vous pouvez modifier λ avec le champ ci-dessus.\n\n");
                displayArea.appendText(reseau.toString() + "\n");

                afficherCout();

            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors du chargement: " + e.getMessage());
                infoLabel.setText("Erreur de chargement");
                infoLabel.setTextFill(Color.DARKRED);
                enableButtons(false);
            }
        }
    }

    /**
     * Calcule et affiche le coût du réseau ainsi que ses indicateurs.
     */
    private void afficherCout() {
        if (reseau == null) return;

        try {
            double cout = reseau.calculerCout();
            double dispersion = reseau.calculerDispersion();
            double surcharge = reseau.calculerSurcharge();
            double lambda = reseau.getLambda();

            displayArea.appendText("\n=== ANALYSE DU COÛT ===\n");
            displayArea.appendText(String.format("Paramètre λ: %.2f\n", lambda));
            displayArea.appendText(String.format("Coût total: %.4f\n", cout));
            displayArea.appendText(String.format("Dispersion: %.4f\n", dispersion));
            displayArea.appendText(String.format("Surcharge: %.4f\n", surcharge));
            displayArea.appendText(String.format("λ × Surcharge: %.4f\n", lambda * surcharge));

            var erreurs = reseau.verifierStructure();
            if (!erreurs.isEmpty()) {
                displayArea.appendText("\n⚠ ERREURS DETECTÉES:\n");
                for (String err : erreurs) {
                    displayArea.appendText("• " + err + "\n");
                }
            } else {
                displayArea.appendText("\n✓ Réseau valide\n");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de calculer le coût: " + e.getMessage());
        }
    }

    /**
     * Lance l'optimisation automatique du réseau.
     */
    private void optimiserReseau() {
        if (reseau == null) return;

        try {
            displayArea.appendText("\n=== OPTIMISATION EN COURS... ===\n");
            displayArea.appendText(String.format("Paramètre λ utilisé: %.2f\n", reseau.getLambda()));

            OptimisateurReseau opt = new OptimisateurReseau();
            Reseau reseauOptimal = opt.optimisationMultiDemarrages(reseau, 10);

            double coutInitial = reseau.calculerCout();
            double coutOptimal = reseauOptimal.calculerCout();

            reseau = reseauOptimal;

            displayArea.appendText("✓ Optimisation terminée!\n");
            displayArea.appendText(String.format("Coût initial: %.4f\n", coutInitial));
            displayArea.appendText(String.format("Coût optimal: %.4f\n", coutOptimal));
            displayArea.appendText(String.format("Amélioration: %.2f%%\n\n",
                    ((coutInitial - coutOptimal) / coutInitial) * 100));

            displayArea.appendText("=== NOUVELLE ARCHITECTURE ===\n");
            displayArea.appendText(reseau.toString() + "\n");

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'optimisation: " + e.getMessage());
        }
    }

    /**
     * Sauvegarde le réseau courant dans un fichier texte.
     */
    private void sauvegarderReseau() {
        if (reseau == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder le réseau optimisé");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                ReseauWriter writer = new ReseauWriter();
                writer.ecrireFichier(file.getAbsolutePath(), reseau);

                displayArea.appendText("\n=== SAUVEGARDE ===\n");
                displayArea.appendText("Réseau sauvegardé avec succès!\n");
                displayArea.appendText("Fichier: " + file.getAbsolutePath() + "\n");
                displayArea.appendText(String.format("Paramètre λ: %.2f\n", reseau.getLambda()));

                showAlert("Succès", "Réseau sauvegardé avec succès dans:\n"
                        + file.getAbsolutePath() + "\n\nParamètre λ: " + reseau.getLambda());
            } catch (IOException e) {
                showAlert("Erreur", "Erreur lors de la sauvegarde: " + e.getMessage());
            }
        }
    }

    /**
     * Active ou désactive les boutons d'action.
     *
     * @param enable true pour activer, false pour désactiver.
     */
    private void enableButtons(boolean enable) {
        btnAfficherCout.setDisable(!enable);
        btnOptimiser.setDisable(!enable);
        btnSauvegarder.setDisable(!enable);
    }

    /**
     * Affiche une fenêtre d'information.
     *
     * @param title   Titre de la fenêtre.
     * @param message Message affiché.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Retourne la scène associée à cette classe.
     *
     * @return La scène(page).
     */
    public Scene getScene() {
        return scene;
    }
}
