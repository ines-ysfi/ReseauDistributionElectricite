package reseau;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class MaisonTests {

    @Test
    void equals_deuxMaisonsMemeNom_sontEgales() {
        Maison m1 = new Maison("M1", Consommation.BASSE);
        Maison m2 = new Maison("M1", Consommation.FORTE);

        assertEquals(m1, m2);
    }

    @Test
    void equals_nomsDifferents_pasEgales() {
        Maison m1 = new Maison("M1", Consommation.BASSE);
        Maison m2 = new Maison("M2", Consommation.BASSE);

        assertNotEquals(m1, m2);
    }

    @Test
    void hashCode_coherentAvecEquals() {
        Maison m1 = new Maison("M1", Consommation.BASSE);
        Maison m2 = new Maison("M1", Consommation.FORTE);

        assertEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    void maisonUtilisableCommeCleDeMap() {
        Maison m1 = new Maison("M1", Consommation.NORMAL);
        Maison m2 = new Maison("M1", Consommation.FORTE);

        Map<Maison, String> map = new HashMap<>();
        map.put(m1, "OK");

        assertEquals("OK", map.get(m2));
    }
}
