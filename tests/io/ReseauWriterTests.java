package io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import reseau.*;

class ReseauWriterTests {

    @Test
    void ecrireFichier_reseauSimple_contenuCorrect() throws IOException {
        // Création du réseau
        Reseau reseau = new Reseau();

        Generateur g1 = new Generateur("g1", 100);
        Generateur g2 = new Generateur("g2", 50);

        Maison m1 = new Maison("m1", Consommation.BASSE);
        Maison m2 = new Maison("m2", Consommation.FORTE);

        reseau.ajouterGenerateur(g1);
        reseau.ajouterGenerateur(g2);
        reseau.ajouterMaison(m1);
        reseau.ajouterMaison(m2);

        reseau.connecter(m1, g1);
        reseau.connecter(m2, g2);

        // Fichier temporaire
        Path fichier = Files.createTempFile("reseau_writer_test", ".txt");

        // Écriture
        ReseauWriter writer = new ReseauWriter();
        writer.ecrireFichier(fichier.toString(), reseau);

        // Lecture du contenu
        String contenu = Files.readString(fichier);

        // Vérification générateurs
        assertTrue(contenu.contains("generateur(g1,100)."));
        assertTrue(contenu.contains("generateur(g2,50)."));

        // Vérification maisons
        assertTrue(contenu.contains("maison(m1,BASSE)."));
        assertTrue(contenu.contains("maison(m2,FORTE)."));

        // Vérification connexions
        assertTrue(contenu.contains("connexion(g1,m1)."));
        assertTrue(contenu.contains("connexion(g2,m2)."));
    }

    @Test
    void ecrireFichier_reseauVide_fichierVide() throws IOException {
        Reseau reseau = new Reseau();

        Path fichier = Files.createTempFile("reseau_writer_vide", ".txt");

        ReseauWriter writer = new ReseauWriter();
        writer.ecrireFichier(fichier.toString(), reseau);

        String contenu = Files.readString(fichier);
        assertTrue(contenu.isBlank());
    }
}
