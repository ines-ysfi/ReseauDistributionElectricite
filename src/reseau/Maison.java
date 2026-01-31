package reseau;

/**
 * Représente une maison dans le réseau.
 * 
 * Chaque maison a un nom et un type de consommation d'énergie.
 */
public class Maison {
    
    private Consommation consommation;
    private String nom;
    
    /**
     * Crée une maison avec un nom et une consommation donnée.
     * 
     * @param nom nom de la maison
     * @param consommation type de consommation
     */
    public Maison(String nom, Consommation consommation) {
        this.nom = nom;
        this.consommation = consommation;
    }

    public Consommation getConsommation() {
        return consommation;
    }

    public String getNom() {
        return nom;
    }

    public void setConsommation(Consommation consommation) {
        this.consommation = consommation;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Maison{" +
                "nom='" + nom + '\'' +
                ", consommation=" + consommation +
                '}';
    }

    
    /**
     * Deux maisons sont égales si elles ont le même nom.
     * 
     * @param o objet à comparer
     * @return vrai si les noms sont égaux, faux sinon
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  
        if (o == null || getClass() != o.getClass()) return false; 
        
        Maison maison = (Maison) o;
        return nom.equals(maison.nom); 
    }

    @Override
    public int hashCode() {
        return nom.hashCode();
    }
}
