package reseau;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

class GenerateurTests {

    @Test
    void equals_deuxGenerateursMemeNom_sontEgaux() {
        Generateur g1 = new Generateur("G1", 100);
        Generateur g2 = new Generateur("G1", 200); // Capacite différente

        assertEquals(g1, g2);
    }

    @Test
    void equals_nomsDifferents_pasEgaux() {
        Generateur g1 = new Generateur("G1", 100);
        Generateur g2 = new Generateur("G2", 100);

        assertNotEquals(g1, g2);
    }

    @Test
    void hashCode_coherentAvecEquals() {
        Generateur g1 = new Generateur("G1", 100);
        Generateur g2 = new Generateur("G1", 200);

        assertEquals(g1.hashCode(), g2.hashCode());
    }

    @Test
    void generateurUtilisableCommeCleDeMap() {
        Generateur g1 = new Generateur("G1", 100);
        Generateur g2 = new Generateur("G1", 200);

        Map<Generateur, String> map = new HashMap<>();
        map.put(g1, "OK");

        assertEquals("OK", map.get(g2));
    }
}
