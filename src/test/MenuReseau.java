package test;

import java.util.List;
import java.util.Scanner;
import reseau.*;
import io.*;
import optimisation.*;

public class MenuReseau {

    private Reseau reseau;
    private final Scanner scanner;
    private final boolean modeManuel;
    private String fichierEntree;

    /**
     * Constructeur pour le mode manuel.
     * Crée un réseau vide que l'utilisateur pourra configurer
     * interactivement via le menu de configuration.
     */
    public MenuReseau() {
        this.reseau = new Reseau();
        this.scanner = new Scanner(System.in);
        this.modeManuel = true;
        this.fichierEntree = null;
    }

    /**
     * Constructeur pour mode fichier (chargement depuis un fichier)
     */
    public MenuReseau(String filePath, double lambda) {
        this.scanner = new Scanner(System.in);
        this.modeManuel = false;
        this.fichierEntree = filePath;
        chargerReseau(filePath, lambda);
    }

    /**
     * Point d'entrée principal du menu
     */
    public void demarrer() {
        if (modeManuel) {
            menuConfiguration();
        } else {
            menuOptimisation();
        }
    }

    
    /**
     * Affiche et gère le menu de configuration du réseau en mode manuel.
     * Permet à l'utilisateur de construire progressivement le réseau
     * en ajoutant des générateurs, maisons et connexions.
     * Une fois terminé, lance le menu d'optimisation.
     */

    private void menuConfiguration() {
        System.out.println("=== Bienvenue dans le gestionnaire de réseau électrique ===\n");
        boolean fini = false;
        
        while (!fini) {
            afficherMenuConfiguration();
            String choixStr = scanner.nextLine().trim();
            int choix;
            
            try {
                choix = Integer.parseInt(choixStr);
            } catch (NumberFormatException e) {
                System.err.println("Veuillez entrer un nombre entre 1 et 5.\n");
                continue;
            }

            switch (choix) {
                case 1:
                    ajouterGenerateur();
                    break;
                case 2:
                    ajouterMaison();
                    break;
                case 3:
                    ajouterConnexion();
                    break;
                case 4: 
                    supprimerConnexion();
                    break;
                case 5:
                    if (verifierReseauComplet()) {
                        fini = true;
                        menuOptimisation();
                    }
                    break;
                default:
                    System.err.println("Option invalide. Choisissez entre 1 et 5.\n");
            }
        }
    }

    private void afficherMenuConfiguration() {
        System.out.println("--- Menu de configuration ---");
        System.out.println("1) Ajouter un générateur");
        System.out.println("2) Ajouter une maison");
        System.out.println("3) Ajouter une connexion");
        System.out.println("4) Supprimer une connexion");
        System.out.println("5) Terminer la configuration");
        System.out.print("Choix : ");
    }

    
    
    /**
     * Affiche et gère le menu d'optimisation du réseau.
     * Permet à l'utilisateur de :
     *   Lancer l'optimisation automatique
     *   Sauvegarder la solution courante
     *   Quitter le programme
     */
    private void menuOptimisation() {
        System.out.println("\n Configuration terminée ! Menu d'optimisation.\n");
        boolean quitter = false;

        while (!quitter) {
            afficherMenuOptimisation();
            String choixStr = scanner.nextLine().trim();
            int choix;
            
            try {
                choix = Integer.parseInt(choixStr);
            } catch (NumberFormatException e) {
                System.err.println("Veuillez entrer un nombre entre 1 et 3.\n");
                continue;
            }

            switch (choix) {
                case 1:
                    resolutionAutomatique();
                    break;
                case 2:
                    sauvegarderSolution();
                    break;
                case 3:
                    System.out.println("\nProgramme terminé. Au revoir !");
                    quitter = true;
                    break;
                default:
                    System.err.println("Choix invalide. Tapez 1, 2 ou 3.");
            }
        }
    }

    private void afficherMenuOptimisation() {
        System.out.println("--- Menu d'optimisation ---");
        System.out.println("1) Résolution automatique");
        System.out.println("2) Sauvegarder la solution");
        System.out.println("3) Quitter");
        System.out.print("Votre choix : ");
    }


    /**
     * Charge un réseau depuis un fichier et configure le paramètre lambda.
     * Vérifie que le réseau chargé respecte toutes les contraintes requises.
     * En cas d'erreur, termine le programme avec un code d'erreur.
     * 
     * @param filePath le chemin du fichier à charger
     * @param lambda 
     */
    private void chargerReseau(String filePath, double lambda) {
        try {
            ReseauReader reader = new ReseauReader();
            reseau = reader.lireFichier(filePath);
            reseau.setLambda(lambda);
            
            if (!verifierReseauComplet()) {
                System.err.println("Le réseau ne respecte pas les contraintes requises!");
                System.exit(1);
            }

            System.out.println("Fichier chargé avec succès !\n");

        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
            System.exit(1);
        }
    }
    
    
    /**
     * Vérifie que le réseau respecte toutes les contraintes structurelles.
     * Affiche les erreurs éventuelles et retourne false si le réseau
     * n'est pas valide.
     * 
     * @return true si le réseau est complet et valide, false sinon
     */
    private boolean verifierReseauComplet() {
        List<String> erreurs = reseau.verifierStructure();

        if (!erreurs.isEmpty()) {
            System.out.println("\n Le réseau contient des erreurs :");
            for (String err : erreurs) {
                System.out.println(" - " + err);
            }
            System.out.println();
            return false;
        }

        return true;
    }



    /**
     * Ajoute un générateur au réseau de manière interactive.
     * Demande à l'utilisateur de saisir le nom et la capacité maximale
     * du générateur. Si le générateur existe déjà, met à jour sa capacité.
     */
    private void ajouterGenerateur() {
        System.out.print("Entrez le nom et la capacité du générateur (ex: G1 60) : ");
        String[] parts = scanner.nextLine().trim().split("\\s+");
        
        if (parts.length != 2) {
            System.err.println("Format invalide ! Utilisez : nom capacité\n");
            return;
        }

        String nom = parts[0];
        int capaciteMax;

        try {
            capaciteMax = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            System.err.println("La capacité doit être un nombre !\n");
            return;
        }

        if (capaciteMax <= 0) {
            System.err.println("La capacité doit être positive !\n");
            return;
        }

        Generateur nouveau = new Generateur(nom, capaciteMax);
        boolean ajoute = reseau.ajouterGenerateur(nouveau);

        if (ajoute) {
            System.out.println(" Générateur " + nom + " ajouté.\n");
        } else {
            System.out.println(" Le générateur existe déjà. Capacité mise à jour.\n");
        }
    }

    
    /**
     * Ajoute une maison au réseau de manière interactive.
     * Demande à l'utilisateur de saisir le nom et le type de consommation
     * de la maison. Si la maison existe déjà, met à jour sa consommation.
     */
    private void ajouterMaison() {
        System.out.print("Entrez le nom et le type de maison (ex: M1 NORMAL) : ");
        String[] parts = scanner.nextLine().trim().split("\\s+");
        
        if (parts.length != 2) {
            System.err.println("Format invalide ! Utilisez : nom TYPE (BASSE, NORMAL ou FORTE)\n");
            return;
        }
        
        String nom = parts[0];
        Consommation cons;
        
        try {
            cons = Consommation.fromString(parts[1]);
        } catch (IllegalArgumentException e) {
            System.err.println("Type invalide ! Utilisez : BASSE, NORMAL ou FORTE\n");
            return;
        }

        Maison nouvelle = new Maison(nom, cons);
        boolean ajoutee = reseau.ajouterMaison(nouvelle);

        if (ajoutee) {
            System.out.println("Maison " + nom + " ajoutée.\n");
        } else {
            System.out.println("La maison existe déjà. Consommation mise à jour.\n");
        }
    }

    /**
     * Ajoute une connexion entre une maison et un générateur.
     * 
     * Identifie automatiquement quel élément est la maison et quel
     * élément est le générateur, quel que soit l'ordre de saisie.
     */
    private void ajouterConnexion() {
        System.out.print("Entrez la connexion (ex: M1 G1) : ");
        String[] saisie = scanner.nextLine().trim().split("\\s+");

        if (saisie.length != 2) {
            System.err.println("Format invalide. Exemple attendu : M1 G1\n");
            return;
        }
        
        String nom1 = saisie[0];
        String nom2 = saisie[1];

        Maison maison = reseau.trouverMaisonParNom(nom1);
        Generateur gen = reseau.trouverGenerateurParNom(nom2);

        if (maison == null || gen == null) {
            maison = reseau.trouverMaisonParNom(nom2);
            gen = reseau.trouverGenerateurParNom(nom1);
        }
        
        if (maison == null || gen == null) {
            System.err.println("Impossible d'identifier maison et générateur. Vérifiez les noms.\n");
            return;
        }
        
        try {
            reseau.connecter(maison, gen);
            System.out.println("Connexion ajoutée : " + maison.getNom() + " → " + gen.getNom() + "\n");
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur : " + e.getMessage() + "\n");
        }
    }

    
    /**
     * Supprime une connexion entre une maison et un générateur.
     * 
     * Identifie automatiquement quel élément est la maison et quel
     * élément est le générateur, quel que soit l'ordre de saisie.
     */
    private void supprimerConnexion() {
        System.out.print("Entrez la connexion à supprimer (ex: M1 G1) : ");
        String[] parts = scanner.nextLine().trim().split("\\s+");
        
        if (parts.length != 2) {
            System.err.println("Format invalide. Exemple attendu : M1 G1\n");
            return;
        }
        
        String nom1 = parts[0];
        String nom2 = parts[1];

        Maison maison = reseau.trouverMaisonParNom(nom1);
        Generateur gen = reseau.trouverGenerateurParNom(nom2);

        if (maison == null || gen == null) {
            maison = reseau.trouverMaisonParNom(nom2);
            gen = reseau.trouverGenerateurParNom(nom1);
        }
        
        if (maison == null || gen == null) {
            System.err.println("Impossible d'identifier maison et générateur. Vérifiez les noms.\n");
            return;
        }

        try {
            reseau.deconnecter(maison, gen);
            System.out.println("Connexion supprimée : " + maison.getNom() + " ✗ " + gen.getNom() + "\n");
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur : " + e.getMessage() + "\n");
        }
    }


    

    /**
     * Lance l'optimisation automatique du réseau.
     * 
     * Effectue plusieurs démarrages de l'algorithme d'optimisation
     * pour trouver la meilleure configuration possible. Affiche le
     * coût initial, le coût optimal trouvé et la configuration finale.
     */
    private void resolutionAutomatique() {

        OptimisateurReseau opt = new OptimisateurReseau();

        System.out.println("=== Coût du réseau initial ===");
        System.out.printf("Coût initial : %.6f\n\n", reseau.calculerCout());


        System.out.println("=== Lancement de l'algo d'optimisation ===");

        Reseau reseauOptimal = opt.optimisationMultiDemarrages(reseau, 10);
        double coutOptimal = reseauOptimal.calculerCout();

        this.reseau = reseauOptimal;

        System.out.println("=== Réseau optimal ===");
        System.out.println(reseau);
        System.out.printf("Coût minimal : %.6f\n\n", coutOptimal);
    }

    

    /**
     * Sauvegarde la solution courante dans un fichier.
     * 
     * Demande à l'utilisateur de spécifier le nom du fichier de sortie "different du fichier d'entree".
     */
    private void sauvegarderSolution() {
        System.out.print("Nom du fichier de sauvegarde : ");
        String fichier = scanner.nextLine().trim();

        if (fichier.equals(fichierEntree)) {
            System.err.println(
                "Erreur : le fichier de sauvegarde doit être différent du fichier d'entrée.\n"
            );
            return;
        }

        try {
            ReseauWriter writer = new ReseauWriter();
            writer.ecrireFichier(fichier, reseau);
            System.out.println("Sauvegarde réussie dans " + fichier + "\n");
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage() + "\n");
        }
    }
}