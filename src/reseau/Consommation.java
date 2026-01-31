package reseau;



/**
 * Représente les différents niveaux de consommation d'une maison.
 * 
 * Chaque type de consommation est associé à une valeur numérique
 * qui correspond à la demande en énergie.
 */
public enum Consommation {
    BASSE(10),
    NORMAL(20),
    FORTE(40);

    private final int conso;

    /**
     * Constructeur de l'énumération.
     *
     * @param demand valeur de consommation associée
     */
    private Consommation(int demand) {
        this.conso = demand;
    }

    
    /**
     * Retourne la valeur numérique de la consommation.
     *
     * @return la consommation
     */
    public int getValeur() {
        return conso;
    }

    /**
     * Convertit une chaîne de caractères en type Consommation.
     *
     * @param str chaîne représentant le type de consommation
     * @return le type de consommation correspondant
     * @throws IllegalArgumentException si la chaîne est invalide
     */
    public static Consommation fromString(String str) {
        switch (str.toUpperCase()) {
            case "BASSE" -> { return BASSE; }
            case "NORMAL" -> { return NORMAL; }
            case "FORTE" -> { return FORTE; }
            default -> throw new IllegalArgumentException("Type de consommation invalide: " + str);
        }
    }
}
