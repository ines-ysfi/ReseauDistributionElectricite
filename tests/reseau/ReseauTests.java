package reseau;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReseauTests {

    private Reseau reseau;
    private Maison m1;
    private Maison m2;
    private Generateur g1;
    private Generateur g2;

    @BeforeEach
    void setUp() {
        reseau = new Reseau();

        m1 = new Maison("M1", Consommation.BASSE);
        m2 = new Maison("M2", Consommation.FORTE);

        g1 = new Generateur("G1", 100);
        g2 = new Generateur("G2", 50);

        reseau.ajouterMaison(m1);
        reseau.ajouterMaison(m2);

        reseau.ajouterGenerateur(g1);
        reseau.ajouterGenerateur(g2);
    }


    @Test
    void ajouterMaison_nouvelleMaison_retourneTrue() {
        Maison m3 = new Maison("M3", Consommation.BASSE);
        assertTrue(reseau.ajouterMaison(m3));
    }

    @Test
    void ajouterMaison_existante_retourneFalse() {
        Maison m1bis = new Maison("M1", Consommation.FORTE);
        assertFalse(reseau.ajouterMaison(m1bis));
    }

    @Test
    void ajouterGenerateur_nouveau_retourneTrue() {
        Generateur g3 = new Generateur("G3", 200);
        assertTrue(reseau.ajouterGenerateur(g3));
    }

    @Test
    void ajouterGenerateur_existant_retourneFalse() {
        Generateur g1bis = new Generateur("G1", 300);
        assertFalse(reseau.ajouterGenerateur(g1bis));
    }


    @Test
    void connecter_maisonEtGenerateurValides() {
        reseau.connecter(m1, g1);
        assertEquals(g1, reseau.getGenerateurDeMaison(m1));
    }

    @Test
    void connecter_maisonNonEnregistree_exception() {
        Maison inconnue = new Maison("X", Consommation.BASSE);
        assertThrows(IllegalArgumentException.class,() -> reseau.connecter(inconnue, g1));
    }

    @Test
    void connecter_generateurNonEnregistre_exception() {
        Generateur inconnu = new Generateur("X", 10);
        assertThrows(IllegalArgumentException.class,
                () -> reseau.connecter(m1, inconnu));
    }

    @Test
    void modifierConnexion_valide() {
        reseau.connecter(m1, g1);
        reseau.modifierConnexion(m1, g1, g2);

        assertEquals(g2, reseau.getGenerateurDeMaison(m1));
    }
    
    @Test
    void modifierConnexion_maisonInconnue_exception() {
        Maison inconnue = new Maison("X", Consommation.BASSE);

        assertThrows(IllegalArgumentException.class, () -> {
            reseau.modifierConnexion(inconnue, g1, g2);
        });
    }


    @Test
    void modifierConnexion_ancienGenerateurInconnu_exception() {
        reseau.ajouterMaison(m1);

        Generateur inconnu = new Generateur("X", 10);

        assertThrows(IllegalArgumentException.class, () -> {  reseau.modifierConnexion(m1, inconnu, g2);});
    }

    @Test
    void modifierConnexion_maisonPasConnecteeAAncien_exception() {
        reseau.connecter(m1, g2); // connectée à g2

        assertThrows(IllegalArgumentException.class, () -> {
            reseau.modifierConnexion(m1, g1, g2);
        });
    }

    @Test
    void modifierConnexion_nouveauGenerateurInconnu_exception() {
        reseau.ajouterMaison(m1);
        reseau.ajouterGenerateur(g1);

        Generateur inconnu = new Generateur("X", 10);

        assertThrows(IllegalArgumentException.class, () -> {
            reseau.modifierConnexion(m1, g1, inconnu);
        });
    }

    @Test
    void deconnecter_maisonConnectee_ok() {
        reseau.connecter(m1, g1);
        reseau.deconnecter(m1, g1);

        assertNull(reseau.getGenerateurDeMaison(m1));
    }

    @Test
    void deconnecter_maisonInconnue_exception() {
        Maison inconnue = new Maison("X", Consommation.BASSE);

        assertThrows(IllegalArgumentException.class, () -> {
            reseau.deconnecter(inconnue, g1);
        });
    }

    @Test
    void deconnecter_generateurInconnu_exception() {
        reseau.ajouterMaison(m1);

        Generateur inconnu = new Generateur("X", 10);

        assertThrows(IllegalArgumentException.class, () -> {
            reseau.deconnecter(m1, inconnu);
        });
    }

    @Test
    void deconnecter_maisonNonConnectee_exception() {
        assertThrows(IllegalArgumentException.class, () -> {
            reseau.deconnecter(m1, g1);
        });
    }

    @Test
    void deconnecter_maisonConnecteeAUnAutreGenerateur_exception() {
        reseau.connecter(m1, g2);

        assertThrows(IllegalArgumentException.class, () -> {
            reseau.deconnecter(m1, g1);
        });
    }


    @Test
    void calculerCharges_reseauSimple() {
        reseau.connecter(m1, g1);
        reseau.connecter(m2, g1);

        Map<Generateur, Integer> charges = reseau.calculerCharges();

        int attendu = m1.getConsommation().getValeur() +
                      m2.getConsommation().getValeur();

        assertEquals(attendu, charges.get(g1));
    }

    @Test
    void calculerSurcharge_aucuneSurcharge() {
        reseau.connecter(m1, g1);
        assertEquals(0.0, reseau.calculerSurcharge(), 0.0001);
    }
    
    @Test
    void calculerSurcharge_exact() {
        reseau = new Reseau();

        Maison m1 = new Maison("M1", Consommation.FORTE);  // 40
        Maison m2 = new Maison("M2", Consommation.FORTE);  // 40
        reseau.ajouterMaison(m1);
        reseau.ajouterMaison(m2);

        Generateur g1 = new Generateur("G1", 70);  // total capacity = 70
        reseau.ajouterGenerateur(g1);

        reseau.connecter(m1, g1);
        reseau.connecter(m2, g1);

        double surcharge = reseau.calculerSurcharge();
        assertEquals(0.142857, surcharge, 0.0001);
    }
    
    
    @Test
    void calculerDispersion_exact() {
        reseau = new Reseau();

        Maison m1 = new Maison("M1", Consommation.BASSE);  // 10
        Maison m2 = new Maison("M2", Consommation.FORTE);  // 40
        reseau.ajouterMaison(m1);
        reseau.ajouterMaison(m2);

        Generateur g1 = new Generateur("G1", 100);
        Generateur g2 = new Generateur("G2", 50);
        reseau.ajouterGenerateur(g1);
        reseau.ajouterGenerateur(g2);

        reseau.connecter(m1, g1);
        reseau.connecter(m2, g2);

        double dispersion = reseau.calculerDispersion();
        assertEquals(0.7, dispersion, 0.0001);
    }

    @Test
    void calculerCout_nonNegatif() {
        reseau.connecter(m1, g1);
        reseau.connecter(m2, g2);

        double cout = reseau.calculerCout();
        assertTrue(cout >= 0);
    }
    
    @Test
    void calculerCout_exact() {
        reseau = new Reseau();
        reseau.setLambda(10.0);  

        // Maisons
        Maison m1 = new Maison("M1", Consommation.BASSE);  // 10
        Maison m2 = new Maison("M2", Consommation.FORTE);  // 40
        reseau.ajouterMaison(m1);
        reseau.ajouterMaison(m2);

        // Générateurs
        Generateur g1 = new Generateur("G1", 100);
        Generateur g2 = new Generateur("G2", 50);
        reseau.ajouterGenerateur(g1);
        reseau.ajouterGenerateur(g2);

        // Connexions
        reseau.connecter(m1, g1);
        reseau.connecter(m2, g2);
        double cout = reseau.calculerCout();
        assertEquals(0.7, cout, 0.0001);
    }


    @Test
    void verifierStructure_reseauValide_aucuneErreur() {
        reseau.connecter(m1, g1);
        reseau.connecter(m2, g2);

        List<String> erreurs = reseau.verifierStructure();
        assertTrue(erreurs.isEmpty());
    }

    @Test
    void verifierStructure_maisonNonConnectee_detectee() {
        reseau.connecter(m1, g1);

        List<String> erreurs = reseau.verifierStructure();
        assertFalse(erreurs.isEmpty());
    }
    
    
    @Test
    void verifierStructure_aucuneMaison_detectee() {
        Reseau r = new Reseau();
        r.ajouterGenerateur(new Generateur("G1", 100));

        List<String> erreurs = r.verifierStructure();

        assertTrue(
            erreurs.contains("Le réseau ne contient aucune maison.")
        );
    }

    @Test
    void verifierStructure_aucunGenerateur_detecte() {
        Reseau r = new Reseau();
        r.ajouterMaison(new Maison("M1", Consommation.BASSE));

        List<String> erreurs = r.verifierStructure();

        assertTrue(
            erreurs.contains("Le réseau ne contient aucun générateur.")
        );
    }
    
    @Test
    void verifierStructure_maisonConnecteeAPlusieursGenerateurs_detectee() {
        Reseau r = new Reseau();

        Maison m = new Maison("M1", Consommation.BASSE);
        Generateur g1 = new Generateur("G1", 100);
        Generateur g2 = new Generateur("G2", 100);

        r.ajouterMaison(m);
        r.ajouterGenerateur(g1);
        r.ajouterGenerateur(g2);

        r.connecter(m, g1);
        r.connecter(m, g2);

        List<String> erreurs = r.verifierStructure();

        assertTrue(
            erreurs.stream().anyMatch(e ->
                e.contains("connectée à plusieurs générateurs")
            )
        );
    }



    @Test
    void verifierStructure_demandeSuperieureCapacite_detectee() {
        reseau = new Reseau();

        reseau.ajouterMaison(m1);
        reseau.ajouterMaison(m2);

        Generateur petit = new Generateur("Petit", 1);
        reseau.ajouterGenerateur(petit);

        reseau.connecter(m1, petit);
        reseau.connecter(m2, petit);

        List<String> erreurs = reseau.verifierStructure();

        assertFalse(erreurs.isEmpty());
    }


}
