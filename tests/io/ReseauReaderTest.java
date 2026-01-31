package io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import reseau.*;

class ReseauReaderTests {

    // helper method to create a temporary file with given content
    private Path creerFichierTemp(String contenu) throws IOException {
        Path tmp = Files.createTempFile("reseau_test", ".txt");
        Files.writeString(tmp, contenu);
        return tmp;
    }

    @Test
    void lireFichier_fichierValide_reseauConstruit() throws IOException {
        String contenu = """
            generateur(g1,100).
            maison(m1,BASSE).
            maison(m2,FORTE).
            connexion(g1,m1).
            connexion(g1,m2).
            """;

        Path fichier = creerFichierTemp(contenu);
        ReseauReader reader = new ReseauReader();
        Reseau reseau = reader.lireFichier(fichier.toString());

        assertEquals(1, reseau.getGenerateurs().size());
        assertEquals(2, reseau.getMaisons().size());

        Maison m1 = reseau.trouverMaisonParNom("m1");
        Maison m2 = reseau.trouverMaisonParNom("m2");
        Generateur g1 = reseau.trouverGenerateurParNom("g1");

        assertEquals(g1, reseau.getGenerateurDeMaison(m1));
        assertEquals(g1, reseau.getGenerateurDeMaison(m2));
    }

    @Test
    void lireFichier_ligneSansPoint_exception() throws IOException {
        String contenu = "generateur(g1,100)";

        Path fichier = creerFichierTemp(contenu);
        ReseauReader reader = new ReseauReader();

        assertThrows(IOException.class, () -> reader.lireFichier(fichier.toString()));
    }

    @Test
    void lireFichier_instructionInconnue_exception() throws IOException {
        String contenu = "foo(bar).";

        Path fichier = creerFichierTemp(contenu);
        ReseauReader reader = new ReseauReader();

        IOException ex = assertThrows(IOException.class, () -> reader.lireFichier(fichier.toString()));
        assertTrue(ex.getMessage().contains("Instruction inconnue"));
    }

    @Test
    void lireFichier_generateurApresMaison_exception() throws IOException {
        String contenu = """
            maison(m1,BASSE).
            generateur(g1,100).
            """;

        Path fichier = creerFichierTemp(contenu);
        ReseauReader reader = new ReseauReader();

        assertThrows(IOException.class, () -> reader.lireFichier(fichier.toString()));
    }

    @Test
    void lireFichier_maisonApresConnexion_exception() throws IOException {
        String contenu = """
            generateur(g1,100).
            maison(m1,BASSE).
            connexion(g1,m1).
            maison(m2,BASSE).
            """;

        Path fichier = creerFichierTemp(contenu);
        ReseauReader reader = new ReseauReader();

        IOException ex = assertThrows(IOException.class, () -> reader.lireFichier(fichier.toString()));
        assertTrue(ex.getMessage().contains("Une maison apparaît après les connexions"));
    }

    @Test
    void lireFichier_generateurArgumentsInvalides_exception() throws IOException {
        String contenu = "generateur(g1).";

        Path fichier = creerFichierTemp(contenu);
        ReseauReader reader = new ReseauReader();

        IOException ex = assertThrows(IOException.class, () -> reader.lireFichier(fichier.toString()));
        assertTrue(ex.getMessage().contains("Format générateur invalide"));
    }

    @Test
    void lireFichier_maisonArgumentsInvalides_exception() throws IOException {
        String contenu = "maison(m1).";

        Path fichier = creerFichierTemp(contenu);
        ReseauReader reader = new ReseauReader();

        IOException ex = assertThrows(IOException.class, () -> reader.lireFichier(fichier.toString()));
        assertTrue(ex.getMessage().contains("Format maison invalide"));
    }

    @Test
    void lireFichier_connexionArgumentsInvalides_exception() throws IOException {
        String contenu = """
            generateur(g1,100).
            maison(m1,BASSE).
            connexion(g1).
            """;

        Path fichier = creerFichierTemp(contenu);
        ReseauReader reader = new ReseauReader();

        IOException ex = assertThrows(IOException.class, () -> reader.lireFichier(fichier.toString()));
        assertTrue(ex.getMessage().contains("Format connexion invalide"));
    }

    @Test
    void lireFichier_connexionObjetInexistant_exception() throws IOException {
        String contenu = """
            generateur(g1,100).
            connexion(g1,m1).
            """;

        Path fichier = creerFichierTemp(contenu);
        ReseauReader reader = new ReseauReader();

        IOException ex = assertThrows(IOException.class, () -> reader.lireFichier(fichier.toString()));
        assertTrue(ex.getMessage().contains("Connexion invalide"));
    }

    @Test
    void lireFichier_maisonConsommationInvalide_exception() throws IOException {
        String contenu = "maison(m1,ULTRA).";

        Path fichier = creerFichierTemp(contenu);
        ReseauReader reader = new ReseauReader();

        assertThrows(IOException.class, () -> reader.lireFichier(fichier.toString()));
    }
}
