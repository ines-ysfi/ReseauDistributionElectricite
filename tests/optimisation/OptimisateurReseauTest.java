package optimisation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reseau.*;

public class OptimisateurReseauTest {

    private Reseau reseau;
    private OptimisateurReseau optimisateur;

    @BeforeEach
    void setUp() {
        reseau = new Reseau();
        optimisateur = new OptimisateurReseau();

        Generateur g1 = new Generateur("g1", 100);
        Generateur g2 = new Generateur("g2", 100);

        Maison m1 = new Maison("m1", Consommation.BASSE);   // 10
        Maison m2 = new Maison("m2", Consommation.NORMAL);  // 20
        Maison m3 = new Maison("m3", Consommation.FORTE);   // 40

        reseau.ajouterGenerateur(g1);
        reseau.ajouterGenerateur(g2);

        reseau.ajouterMaison(m1);
        reseau.ajouterMaison(m2);
        reseau.ajouterMaison(m3);

        reseau.connecter(m1, g1);
        reseau.connecter(m2, g1);
        reseau.connecter(m3, g1);
    }

    // ==========================
    // TESTS PRINCIPAUX
    // ==========================

    @Test
    void optimisationMultiDemarrages_retourneUnReseauNonNull() {
        Reseau resultat = optimisateur.optimisationMultiDemarrages(reseau, 5);
        assertNotNull(resultat);
    }

    @Test
    void optimisationMultiDemarrages_neDegradePasLeCout() {
        double coutInitial = reseau.calculerCout();
        Reseau resultat = optimisateur.optimisationMultiDemarrages(reseau, 10);
        double coutFinal = resultat.calculerCout();

        assertTrue(coutFinal <= coutInitial,
                "Le coût final doit être inférieur ou égal au coût initial");
    }

    @Test
    void optimisationMultiDemarrages_toutesLesMaisonsRestentConnectees() {
        Reseau resultat = optimisateur.optimisationMultiDemarrages(reseau, 10);

        for (Maison m : resultat.getMaisons()) {
            assertNotNull(
                resultat.getGenerateurDeMaison(m),
                "La maison " + m.getNom() + " doit être connectée"
            );
        }
    }

    @Test
    void optimisationMultiDemarrages_fonctionneAvecUnSeulDemarrage() {
        Reseau resultat = optimisateur.optimisationMultiDemarrages(reseau, 1);
        assertNotNull(resultat);
    }

    @Test
    void optimisationMultiDemarrages_plusieursDemarrages_donneUnReseauValide() {
        Reseau resultat = optimisateur.optimisationMultiDemarrages(reseau, 20);

        assertNotNull(resultat);
        assertFalse(resultat.getGenerateurs().isEmpty());
        assertFalse(resultat.getMaisons().isEmpty());
    }
}
