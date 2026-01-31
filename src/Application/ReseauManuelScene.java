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
import io.ReseauWriter;
import optimisation.OptimisateurReseau;
import java.io.File;
import java.io.IOException;




/**
 * Interface permettant la construction manuelle d’un réseau électrique.
 *
 * Cette classe fournit une interface graphique permettant à l’utilisateur
 * d’ajouter des générateurs et des maisons, de créer ou supprimer des connexions,
 * de définir le paramètre λ (lambda), de calculer le coût du réseau,
 * de lancer une optimisation automatique et de sauvegarder le réseau dans un fichier.
 */
public class ReseauManuelScene {
    private Scene scene;
    private Stage primaryStage;
    private Reseau reseau;
    private TextArea displayArea;
    private TextField lambdaField;
    
    public ReseauManuelScene(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.reseau = new Reseau();
        this.reseau.setLambda(10.0); 
        createScene();
    }
    
    
    /**
     * Initialise et organise tous les composants graphiques de la scène.
     */
    private void createScene() {

        Label titre = new Label("Construction Manuelle du Réseau");
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
        
        Button btnSetLambda = new Button("Définir");
        btnSetLambda.setStyle("-fx-background-color: #9C27B0; -fx-text-fill: white;");
        btnSetLambda.setOnAction(e -> definirLambda());
        
        lambdaPanel.getChildren().addAll(lambdaLabel, lambdaField, btnSetLambda);

        displayArea = new TextArea();
        displayArea.setEditable(false);
        displayArea.setPrefHeight(400);
        displayArea.setPrefWidth(500);
        displayArea.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 12px;");

        Button btnAjouterGen = createActionButton("Ajouter Générateur", "#4CAF50");
        Button btnAjouterMaison = createActionButton("Ajouter Maison", "#2196F3");
        Button btnAjouterConnexion = createActionButton("Ajouter Connexion", "#FF9800");
        Button btnSupprimerConnexion = createActionButton("Supprimer Connexion", "#F44336");
        Button btnAfficherCout = createActionButton("Afficher Coût", "#9C27B0");
        Button btnOptimiser = createActionButton("Optimiser Automatiquement", "#FF5722");
        Button btnSauvegarder = createActionButton("Sauvegarder", "#607D8B");
        Button btnRetour = createActionButton("Retour à l'accueil", "#795548");
        

        btnAjouterGen.setOnAction(e -> ajouterGenerateur());
        btnAjouterMaison.setOnAction(e -> ajouterMaison());
        btnAjouterConnexion.setOnAction(e -> ajouterConnexion());
        btnSupprimerConnexion.setOnAction(e -> supprimerConnexion());
        btnAfficherCout.setOnAction(e -> afficherCout());
        btnOptimiser.setOnAction(e -> optimiserReseau());
        btnSauvegarder.setOnAction(e -> sauvegarderReseau());
        btnRetour.setOnAction(e -> {
            AccueilScene accueil = new AccueilScene(primaryStage);
            primaryStage.setScene(accueil.getScene());
        });

        VBox buttonBox = new VBox(10, 
            btnAjouterGen, 
            btnAjouterMaison, 
            new Separator(),
            btnAjouterConnexion,
            btnSupprimerConnexion,
            new Separator(),
            btnAfficherCout, 
            btnOptimiser, 
            btnSauvegarder,
            new Separator(),
            btnRetour);
        
        // Layout principal
        HBox mainLayout = new HBox(20);
        mainLayout.getChildren().addAll(buttonBox, displayArea);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));
        
        VBox root = new VBox(10, titre, lambdaPanel, new Separator(), mainLayout);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setBackground(new Background(new BackgroundFill(
            Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        
        scene = new Scene(root, 1100, 700);
    }

    /**
     * Définit la valeur du paramètre λ utilisé dans les calculs du réseau.
     */
    private void definirLambda() {
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
     * Crée un bouton d’action avec une couleur donnée.
     *
     * @param text texte affiché sur le bouton
     * @param color couleur de fond du bouton
     * @return le bouton configuré
     */
    private Button createActionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefWidth(250);
        btn.setPrefHeight(40);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: derive(" + color + ", -20%); -fx-text-fill: white; -fx-font-weight: bold;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;"));
        return btn;
    }
    
    
    /**
     * Ouvre une boîte de dialogue permettant d’ajouter un générateur au réseau.
     */
    private void ajouterGenerateur() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un générateur");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nomField = new TextField();
        nomField.setPromptText("Nom du générateur");
        TextField capaciteField = new TextField();
        capaciteField.setPromptText("Capacité maximale");
        
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Capacité:"), 0, 1);
        grid.add(capaciteField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType ajouterButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ajouterButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ajouterButton) {
                return nomField.getText() + "," + capaciteField.getText();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            try {
                String[] parts = result.split(",");
                String nom = parts[0].trim();
                int capacite = Integer.parseInt(parts[1].trim());
                
                Generateur gen = new Generateur(nom, capacite);
                reseau.ajouterGenerateur(gen);
                
                displayArea.appendText("✓ Générateur ajouté: " + nom + " (capacité: " + capacite + ")\n");
                afficherReseau();
            } catch (Exception e) {
                showAlert("Erreur", "Format invalide! Utilisez: nom,capacité\nEx: gen1,60");
            }
        });
    }
    
    
    /**
     * Ouvre une boîte de dialogue permettant d’ajouter une maison au réseau.
     */
    private void ajouterMaison() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une maison");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nomField = new TextField();
        nomField.setPromptText("Nom de la maison");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("BASSE", "NORMAL", "FORTE");
        typeCombo.setValue("NORMAL");
        
        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Consommation:"), 0, 1);
        grid.add(typeCombo, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType ajouterButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ajouterButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ajouterButton) {
                return nomField.getText() + "," + typeCombo.getValue();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            try {
                String[] parts = result.split(",");
                String nom = parts[0].trim();
                Consommation cons = Consommation.fromString(parts[1]);
                
                Maison maison = new Maison(nom, cons);
                reseau.ajouterMaison(maison);
                
                displayArea.appendText("✓ Maison ajoutée: " + nom + " (consommation: " + cons + ")\n");
                afficherReseau();
            } catch (Exception e) {
                showAlert("Erreur", "Format invalide! Utilisez: nom,type\nTypes: BASSE, NORMAL, FORTE");
            }
        });
    }
    
    
    /**
     * Permet d’ajouter ou de modifier une connexion entre une maison et un générateur.
     */
    private void ajouterConnexion() {

        if (reseau.getMaisons().isEmpty() || reseau.getGenerateurs().isEmpty()) {
            showAlert("Erreur", "Veuillez d'abord ajouter au moins une maison et un générateur!");
            return;
        }
        
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une connexion");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        ComboBox<String> maisonCombo = new ComboBox<>();
        ComboBox<String> genCombo = new ComboBox<>();

        for (Maison m : reseau.getMaisons()) {
            maisonCombo.getItems().add(m.getNom());
        }
        for (Generateur g : reseau.getGenerateurs()) {
            genCombo.getItems().add(g.getNom());
        }
        
        maisonCombo.setValue(maisonCombo.getItems().get(0));
        genCombo.setValue(genCombo.getItems().get(0));
        
        grid.add(new Label("Maison:"), 0, 0);
        grid.add(maisonCombo, 1, 0);
        grid.add(new Label("Générateur:"), 0, 1);
        grid.add(genCombo, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType ajouterButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ajouterButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ajouterButton) {
                return maisonCombo.getValue() + "," + genCombo.getValue();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            try {
                String[] parts = result.split(",");
                String nomMaison = parts[0];
                String nomGen = parts[1];
                
                Maison maison = reseau.trouverMaisonParNom(nomMaison);
                Generateur gen = reseau.trouverGenerateurParNom(nomGen);
                
                if (maison == null) {
                    showAlert("Erreur", "Maison '" + nomMaison + "' non trouvée!");
                    return;
                }
                
                if (gen == null) {
                    showAlert("Erreur", "Générateur '" + nomGen + "' non trouvé!");
                    return;
                }
                

                Generateur genActuel = reseau.getGenerateurDeMaison(maison);
                if (genActuel != null) {

                    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmation.setTitle("Modifier la connexion");
                    confirmation.setHeaderText("Cette maison est déjà connectée à " + genActuel.getNom());
                    confirmation.setContentText("Voulez-vous modifier la connexion vers " + gen.getNom() + "?");
                    
                    if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        reseau.modifierConnexion(maison, genActuel, gen);
                        displayArea.appendText("✓ Connexion modifiée: " + nomMaison + " (" + 
                                            genActuel.getNom() + " → " + nomGen + ")\n");
                    } else {
                        return;
                    }
                } else {
                    reseau.connecter(maison, gen);
                    displayArea.appendText("✓ Connexion ajoutée: " + nomMaison + " → " + nomGen + "\n");
                }
                
                afficherReseau();
            } catch (Exception e) {
                showAlert("Erreur", "Erreur: " + e.getMessage());
            }
        });
    }
    
    
    /**
     * Permet de supprimer une connexion existante dans le réseau.
     */
    private void supprimerConnexion() {

        boolean hasConnections = false;
        for (Generateur g : reseau.getGenerateurs()) {
            if (!reseau.getMaisonsDuGenerateur(g).isEmpty()) {
                hasConnections = true;
                break;
            }
        }
        
        if (!hasConnections) {
            showAlert("Information", "Aucune connexion à supprimer!");
            return;
        }
        
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Supprimer une connexion");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        ComboBox<String> maisonCombo = new ComboBox<>();

        for (Maison m : reseau.getMaisons()) {
            Generateur gen = reseau.getGenerateurDeMaison(m);
            if (gen != null) {
                maisonCombo.getItems().add(m.getNom() + " → " + gen.getNom());
            }
        }
        
        if (maisonCombo.getItems().isEmpty()) {
            showAlert("Information", "Aucune connexion à supprimer!");
            return;
        }
        
        maisonCombo.setValue(maisonCombo.getItems().get(0));
        
        grid.add(new Label("Connexion à supprimer:"), 0, 0);
        grid.add(maisonCombo, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType supprimerButton = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(supprimerButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == supprimerButton) {
                return maisonCombo.getValue();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            try {

                String nomMaison = result.split(" → ")[0];
                
                Maison maison = reseau.trouverMaisonParNom(nomMaison);
                if (maison == null) {
                    showAlert("Erreur", "Maison '" + nomMaison + "' non trouvée!");
                    return;
                }
                
                Generateur gen = reseau.getGenerateurDeMaison(maison);
                if (gen == null) {
                    showAlert("Erreur", "Cette maison n'est pas connectée!");
                    return;
                }

                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirmer la suppression");
                confirmation.setHeaderText("Supprimer la connexion?");
                confirmation.setContentText("Voulez-vous vraiment supprimer la connexion:\n" + 
                                          nomMaison + " → " + gen.getNom() + "?");
                
                if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                    reseau.deconnecter(maison, gen);
                    displayArea.appendText("✗ Connexion supprimée: " + nomMaison + " → " + gen.getNom() + "\n");
                    afficherReseau();
                }
                
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
        });
    }
    
    
    /**
     * Calcule et affiche le coût du réseau ainsi que les détails de surcharge et de dispersion.
     */
    private void afficherCout() {
        try {
            if (reseau.getMaisons().isEmpty() || reseau.getGenerateurs().isEmpty()) {
                showAlert("Information", "Ajoutez d'abord au moins un générateur et une maison!");
                return;
            }
            
            double cout = reseau.calculerCout();
            double dispersion = reseau.calculerDispersion();
            double surcharge = reseau.calculerSurcharge();
            double lambda = reseau.getLambda(); 
            
            displayArea.appendText("\n" + "=".repeat(50) + "\n");
            displayArea.appendText("COÛT DU RÉSEAU\n");
            displayArea.appendText("=".repeat(50) + "\n");
            displayArea.appendText(String.format("Paramètre λ: %.2f\n", lambda)); // AJOUT
            displayArea.appendText(String.format("Coût total: %.6f\n", cout));
            displayArea.appendText(String.format("Dispersion: %.6f\n", dispersion));
            displayArea.appendText(String.format("Surcharge: %.6f\n", surcharge));
            displayArea.appendText(String.format("λ × Surcharge: %.6f\n", lambda * surcharge)); // AJOUT
            
            displayArea.appendText("\nFormule: Coût = Dispersion + λ × Surcharge\n");
            displayArea.appendText(String.format("         %.6f = %.6f + %.2f × %.6f\n\n", 
                cout, dispersion, lambda, surcharge));

            displayArea.appendText("DÉTAILS PAR GÉNÉRATEUR:\n");
            displayArea.appendText("-".repeat(50) + "\n");
            
            for (Generateur g : reseau.getGenerateurs()) {
                int charge = 0;
                for (Maison m : reseau.getMaisonsDuGenerateur(g)) {
                    charge += m.getConsommation().getValeur();
                }
                double taux = (g.getCapaciteMax() > 0) ? (double)charge / g.getCapaciteMax() * 100 : 0;
                displayArea.appendText(String.format("%s: %d/%d (%.1f%%)", 
                    g.getNom(), charge, g.getCapaciteMax(), taux));
                
                if (charge > g.getCapaciteMax()) {
                    displayArea.appendText(" ← SURCHARGE!\n");
                } else {
                    displayArea.appendText("\n");
                }
            }
            
            var erreurs = reseau.verifierStructure();
            if (!erreurs.isEmpty()) {
                displayArea.appendText("\n" + "⚠".repeat(25) + "\n");
                displayArea.appendText("ERREURS DÉTECTÉES:\n");
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
     * Lance une optimisation automatique du réseau afin de minimiser son coût.
     */
    private void optimiserReseau() {
        try {
            
            if (reseau.getMaisons().isEmpty() || reseau.getGenerateurs().isEmpty()) {
                showAlert("Erreur", "Ajoutez d'abord au moins un générateur et une maison!");
                return;
            }
            
            var erreurs = reseau.verifierStructure();
            if (!erreurs.isEmpty()) {
                displayArea.appendText("\n" + "❌".repeat(20) + "\n");
                displayArea.appendText("IMPOSSIBLE D'OPTIMISER:\n");
                for (String err : erreurs) {
                    displayArea.appendText("• " + err + "\n");
                }
                return;
            }
            
            displayArea.appendText("\n" + "⚡".repeat(25) + "\n");
            displayArea.appendText("OPTIMISATION EN COURS...\n");
            displayArea.appendText("⚡".repeat(25) + "\n");
            displayArea.appendText(String.format("Paramètre λ utilisé: %.2f\n", reseau.getLambda())); 
            
            OptimisateurReseau opt = new OptimisateurReseau();

            Reseau reseauOptimal;

                reseauOptimal = opt.optimisationMultiDemarrages(reseau, 5);

            
            double coutInitial = reseau.calculerCout();
            double coutOptimal = reseauOptimal.calculerCout();
            
            reseau = reseauOptimal;
            
            displayArea.appendText("\n" + "✅".repeat(25) + "\n");
            displayArea.appendText("OPTIMISATION TERMINÉE!\n");
            displayArea.appendText("✅".repeat(25) + "\n");
            displayArea.appendText(String.format("Coût initial:   %.6f\n", coutInitial));
            displayArea.appendText(String.format("Coût optimal:   %.6f\n", coutOptimal));
            
            if (coutInitial > 0) {
                double amelioration = ((coutInitial - coutOptimal) / coutInitial) * 100;
                displayArea.appendText(String.format("Amélioration:   %.2f%%\n", amelioration));
            }
            
            afficherReseau();
            
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'optimisation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    /**
     * Sauvegarde le réseau courant dans un fichier texte choisi par l’utilisateur.
     */
    private void sauvegarderReseau() {

        if (reseau.getMaisons().isEmpty() && reseau.getGenerateurs().isEmpty()) {
            showAlert("Information", "Le réseau est vide! Ajoutez des éléments avant de sauvegarder.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder le réseau");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));
        

        fileChooser.setInitialFileName("reseau_" + 
            new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".txt");
        
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                ReseauWriter writer = new ReseauWriter();
                writer.ecrireFichier(file.getAbsolutePath(), reseau);
                
                displayArea.appendText("\n" + "💾".repeat(25) + "\n");
                displayArea.appendText("SAUVEGARDE RÉUSSIE\n");
                displayArea.appendText("💾".repeat(25) + "\n");
                displayArea.appendText("Fichier: " + file.getAbsolutePath() + "\n");
                displayArea.appendText("Taille: " + file.length() + " octets\n");
                displayArea.appendText(String.format("Paramètre λ: %.2f\n", reseau.getLambda())); // AJOUT
                
                showAlert("Succès", "Réseau sauvegardé avec succès dans:\n" + file.getAbsolutePath() + 
                    "\n\nParamètre λ: " + reseau.getLambda());
            } catch (IOException e) {
                showAlert("Erreur", "Erreur lors de la sauvegarde: " + e.getMessage());
            }
        }
    }
    
    private void afficherReseau() {
        displayArea.appendText("\n" + "=".repeat(60) + "\n");
        displayArea.appendText("ÉTAT ACTUEL DU RÉSEAU\n");
        displayArea.appendText("=".repeat(60) + "\n");

        int nbGen = reseau.getGenerateurs().size();
        int nbMaisons = reseau.getMaisons().size();
        int nbConnexions = 0;
        
        for (Generateur g : reseau.getGenerateurs()) {
            nbConnexions += reseau.getMaisonsDuGenerateur(g).size();
        }
        
        displayArea.appendText(String.format("Générateurs: %d | Maisons: %d | Connexions: %d\n", 
            nbGen, nbMaisons, nbConnexions));
        displayArea.appendText(String.format("Paramètre λ: %.2f\n\n", reseau.getLambda())); // AJOUT
        
        displayArea.appendText(reseau.toString() + "\n");
    }
    
    
    /**
     * Affiche une fenêtre d’information à l’utilisateur.
     *
     * @param title titre de la fenêtre
     * @param message message affiché
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().setPrefSize(400, 200);
        
        alert.showAndWait();
    }
    
    
    /**
     * Retourne la scène JavaFX associée à cette page.
     *
     * @return la scène JavaFX
     */
    public Scene getScene() {
        return scene;
    }
}