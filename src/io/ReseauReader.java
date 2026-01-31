package io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import reseau.*;

public class ReseauReader {
    /**
     * Lit le fichier et retourne un réseau construit.
     * 
     * @param chemin chemin vers le fichier
     * @return le réseau construit
     * @throws IOException si le fichier est mal formaté ou introuvable
     */
    public Reseau lireFichier(String chemin) throws IOException {
        Reseau reseau = new Reseau();

        try (BufferedReader br = new BufferedReader(new FileReader(chemin))) {
            String ligne;
            int numeroLigne = 0;

            int etat = 0; 

            while ((ligne = br.readLine()) != null) {
                numeroLigne++;
                ligne = ligne.trim();

                if (ligne.isEmpty()) continue;

                if (!ligne.endsWith(".")) {
                    throw erreur(numeroLigne, ligne,
                            "La ligne doit se terminer par un point");
                }

                ligne = ligne.substring(0, ligne.length() - 1);

                if (ligne.startsWith("generateur(")) {

                    if (etat > 0)
                        throw erreur(numeroLigne, ligne,
                                "Un générateur apparaît après les maisons ou connexions");

                    lireGenerateur(ligne, reseau, numeroLigne);

                } else if (ligne.startsWith("maison(")) {

                    if (etat > 1)
                        throw erreur(numeroLigne, ligne,
                                "Une maison apparaît après les connexions");

                    etat = 1;
                    lireMaison(ligne, reseau, numeroLigne);

                } else if (ligne.startsWith("connexion(")) {

                    etat = 2;
                    lireConnexion(ligne, reseau, numeroLigne);

                } else {
                    throw erreur(numeroLigne, ligne, "Instruction inconnue");
                }
            }
        }

        return reseau;
    }

    
    /**
     * Lit une ligne générateur et l'ajoute au réseau. 
     * @param ligne la ligne du fichier à lire (ex: "generateur(gen1,60).")
     * @param reseau le réseau dans lequel ajouter le générateur
     * @param num numéro de la ligne dans le fichier (pour message d'erreur)
     * @throws IOException si le format est invalide
     */

    private void lireGenerateur(String ligne, Reseau reseau, int num) throws IOException {

        try {
            String contenu = ligne.substring("generateur(".length(), ligne.length() - 1);
            String[] parts = contenu.split(",");

            if (parts.length != 2)
                throw new IllegalArgumentException();

            String nom = parts[0];
            int capacite = Integer.parseInt(parts[1]);

            reseau.ajouterGenerateur(new Generateur(nom, capacite));

        } catch (Exception e) {
            throw erreur(num, ligne, "Format générateur invalide (ex: generateur(gen1,60))");
        }
    }

    
    /**
     * Lit une ligne maison et l'ajoute au réseau.
     * 
     * @param ligne la ligne du fichier à lire (ex: "maison(maison1,NORMAL).")
     * @param reseau le réseau dans lequel ajouter la maison
     * @param num numéro de la ligne dans le fichier (pour message d'erreur)
     * @throws IOException si le format est invalide
     */

    private void lireMaison(String ligne, Reseau reseau, int num) throws IOException {

        try {
            String contenu = ligne.substring("maison(".length(), ligne.length() - 1);
            String[] parts = contenu.split(",");

            if (parts.length != 2)
                throw new IllegalArgumentException();

            String nom = parts[0];
            Consommation cons = Consommation.fromString(parts[1]);

            reseau.ajouterMaison(new Maison(nom, cons));

        } catch (Exception e) {
            throw erreur(num, ligne,
                    "Format maison invalide (ex: maison(maison1,NORMAL))");
        }
    }

    
    
    /**
     * Lit une ligne connexion et connecte la maison au générateur.
     * 
     * 
     * @param ligne la ligne du fichier à lire (ex: "connexion(gen1,maison1).")
     * @param reseau le réseau dans lequel établir la connexion
     * @param num numéro de la ligne dans le fichier (pour message d'erreur)
     * @throws IOException si le format est invalide ou si maison/générateur inexistant
     */
    private void lireConnexion(String ligne, Reseau reseau, int num) throws IOException {

        try {
            String contenu = ligne.substring("connexion(".length(), ligne.length() - 1);
            String[] parts = contenu.split(",");

            if (parts.length != 2)
                throw new IllegalArgumentException();

            String a = parts[0];
            String b = parts[1];

            Maison maison = reseau.trouverMaisonParNom(a);
            Generateur gen = reseau.trouverGenerateurParNom(b);

            if (maison == null || gen == null) {
                maison = reseau.trouverMaisonParNom(b);
                gen = reseau.trouverGenerateurParNom(a);
            }

            if (maison == null || gen == null) {
                throw erreur(num, ligne,
                        "Connexion invalide : maison ou générateur inexistant");
            }

            reseau.connecter(maison, gen);

        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw erreur(num, ligne,
                    "Format connexion invalide (ex: connexion(gen1,maison1))");
        }
    }


    private IOException erreur(int numLigne, String ligne, String message) {
        return new IOException(
                "Erreur ligne " + numLigne + " : " + message + "\n>> " + ligne
        );
    }
}
