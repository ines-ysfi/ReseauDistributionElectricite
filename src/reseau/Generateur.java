package reseau;



/**
 * Représente un générateur.
 * 
 * Chaque générateur a un nom et une capacité maximale.
 * On peut comparer deux générateurs par leur nom.
 */
public class Generateur {
    private String nom;
    private int capaciteMax;

    
    /**
     * Crée un générateur avec un nom et une capacité maximale.
     * 
     * @param nom nom du générateur
     * @param capaciteMax capacité maximale
     */
    public Generateur(String nom, int capaciteMax) {
        this.nom = nom;
        this.capaciteMax = capaciteMax;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
    public int getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(int capaciteMax) {
        this.capaciteMax = capaciteMax;
    }


    @Override
    public String toString(){
        return "Generateur "+nom+" capaciteMax: "+capaciteMax;
    }


    /**
     * Deux générateurs sont égaux si leurs noms sont identiques.
     * 
     * @param o objet à comparer
     * @return vrai si les noms sont égaux, faux sinon
     */
     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false; 
        
        Generateur that = (Generateur) o;
        return nom.equals(that.nom); 
    }

    @Override
    public int hashCode() {
        return nom.hashCode();
    }
}
