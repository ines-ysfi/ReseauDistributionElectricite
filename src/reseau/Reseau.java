package reseau;

import java.util.*;


/**
 * Représente un réseau électrique composé de maisons et de générateurs.
 * 
 * On peut :
 * - ajouter des maisons et des générateurs,
 * - connecter et déconnecter des maisons aux générateurs,
 * - calculer la charge, la dispersion, la surcharge et le coût global du réseau,
 * - vérifier la structure du réseau pour détecter des problèmes.
 */

public class Reseau {

    private List<Maison> maisons;
    private List<Generateur> generateurs;
    private Map<Generateur, List<Maison>> connexions;
    private double lambda = 10.0; 

    
    /**
     * Crée un réseau vide (sans maisons ni générateurs).
     */
    public Reseau() {
        maisons = new ArrayList<>();
        generateurs = new ArrayList<>();
        connexions = new HashMap<>();
    }

    
    /**
     * Ajoute une maison au réseau.
     * Si la maison existe déjà, met à jour sa consommation.
     * 
     * @param maison la maison à ajouter
     * @return true si la maison est ajoutée, false si elle existait déjà
     */
    public boolean ajouterMaison(Maison maison) {
           	int index = maisons.indexOf(maison);
            if (index >= 0) {

                maisons.get(index).setConsommation(maison.getConsommation());
                return false; 
            } else {
                maisons.add(maison);
                return true; 
            }
    }

    
    /**
     * Ajoute un générateur au réseau.
     * Si le générateur existe déjà, met à jour sa capacité.
     * 
     * @param generateur le générateur à ajouter
     * @return true si ajouté, false si déjà présent
     */
    public boolean ajouterGenerateur(Generateur generateur) {
        int index = generateurs.indexOf(generateur);
        if (index >= 0) {
       
            generateurs.get(index).setCapaciteMax(generateur.getCapaciteMax());
            return false; 
        } else {
            generateurs.add(generateur);
            connexions.put(generateur, new ArrayList<>()); 
            return true; 
        }
    }
    
    
    /**
     * Connecte une maison à un générateur.
     * 
     * @param maison la maison à connecter
     * @param generateur le générateur auquel connecter la maison
     * @throws IllegalArgumentException si la maison ou le générateur n'existe pas
     */
    public void connecter(Maison maison, Generateur generateur) {
	    if (!maisons.contains(maison)) {
	        throw new IllegalArgumentException("Maison non enregistrée");
	    }
	    if (!generateurs.contains(generateur)) {
	        throw new IllegalArgumentException("Générateur non enregistré");
	    }
	
	    connexions.get(generateur).add(maison);
	}


    
    
    public void modifierConnexion(Maison maison, Generateur ancien, Generateur nouveau) {
        if (!maisons.contains(maison))
            throw new IllegalArgumentException("Maison inconnue");

        if (!generateurs.contains(ancien))
            throw new IllegalArgumentException("Ancien générateur inconnu");

        if (!generateurs.contains(nouveau))
            throw new IllegalArgumentException("Nouveau générateur inconnu");

        Generateur actuel = trouverGenerateurDeMaison(maison);

        if (!ancien.equals(actuel)) {
            throw new IllegalArgumentException("La maison n'est pas connectée à l'ancien générateur");
        }

        connexions.get(ancien).remove(maison);
        connexions.get(nouveau).add(maison);
    }


    public void deconnecter(Maison maison, Generateur generateur) {
        if (!maisons.contains(maison)) {
            throw new IllegalArgumentException("Maison non enregistrée dans le réseau");
        }
        
        if (!generateurs.contains(generateur)) {
            throw new IllegalArgumentException("Générateur non enregistré dans le réseau");
        }
        Generateur genActuel = trouverGenerateurDeMaison(maison);
        if (genActuel == null) {
            throw new IllegalArgumentException("La maison n'est pas connectée");
        }
        
        if (!genActuel.equals(generateur)) {
            throw new IllegalArgumentException("La maison n'est pas connectée à ce générateur");
        }
        
        
        connexions.get(generateur).remove(maison);
    }
    
    /**
     * Trouve le générateur auquel une maison est connectée.
     * 
     * @param maison la maison recherchée
     * @return le générateur ou null si non connecté
     */
    private Generateur trouverGenerateurDeMaison(Maison maison) {
        for (Map.Entry<Generateur, List<Maison>> entry : connexions.entrySet()) {
            if (entry.getValue().contains(maison)) {
                return entry.getKey();
            }
        }
        return null;
    }

    
    /**
     * Calcule la charge (total consommation) de chaque générateur.
     * 
     * @return map générateur -> charge
     */
    
    public Map<Generateur, Integer> calculerCharges() {
        Map<Generateur, Integer> charges = new HashMap<>();

        for (Map.Entry<Generateur, List<Maison>> entry : connexions.entrySet()) {
            Generateur gen = entry.getKey();
            int charge = 0;

            for (Maison m : entry.getValue()) {
                charge += m.getConsommation().getValeur();
            }

            charges.put(gen, charge);
        }

        return charges;
    }

    
    /**
     * Calcule la dispersion des charges entre générateurs.
     * 
     * dispersion = Σ | (charge_i / capacite_i) - moyenne |
     * 
     * @return la dispersion
     */
    public double calculerDispersion() {
        Map<Generateur, Integer> charges = calculerCharges();
        double somme = 0.0;
        int nbGen = generateurs.size();

        for (Generateur gen : generateurs) {
            double capacite = gen.getCapaciteMax();
            int charge = charges.getOrDefault(gen, 0);
            double taux = (capacite > 0) ? charge / capacite : 0.0;
            somme += taux;
        }

        double moyenne = (nbGen > 0) ? somme / nbGen : 0.0;

       
        double dispersion = 0.0;
        for (Generateur gen : generateurs) {
            double capacite = gen.getCapaciteMax();
            int charge = charges.getOrDefault(gen, 0);
            double taux = (capacite > 0) ? charge / capacite : 0.0;
            dispersion += Math.abs(taux - moyenne);
        }

        return dispersion;
    }
    
    
    /**
     * Calcule la surcharge des générateurs (quand charge > capacité).
     * 
     * surcharge = Σ max(0, (charge_i - capacite_i) / capacite_i)
     * 
     * @return la surcharge
     */
    public double calculerSurcharge() {
        Map<Generateur, Integer> charges = calculerCharges();
        double surcharge = 0.0;

        for (Generateur gen : generateurs) {
            double capacite = gen.getCapaciteMax();
            int charge = charges.getOrDefault(gen, 0);

            if (capacite > 0 && charge > capacite) {
                surcharge += (double)(charge - capacite) / capacite;
            }
        }

        return surcharge;
    }
    
    
    /**
     * Calcule le coût global du réseau.
     * 
     * cout = dispersion + lambda * surcharge
     * 
     * @return le coût
     */
    public double calculerCout() {
        return calculerDispersion() + (lambda * calculerSurcharge());
    }

    /**
     * Cherche une maison dans le réseau par son nom.
     * 
     * @param nom nom de la maison recherchée
     * @return la maison si trouvée, sinon null
     */
    public Maison trouverMaisonParNom(String nom) {
        Maison temp = new Maison(nom, null);
        int index = maisons.indexOf(temp);
        return index >= 0 ? maisons.get(index) : null;
    }

    /**
     * Cherche un générateur dans le réseau par son nom.
     * 
     * @param nom nom du générateur recherché
     * @return le générateur si trouvé, sinon null
     */
    public Generateur trouverGenerateurParNom(String nom) {
        Generateur temp = new Generateur(nom, 0);
        int index = generateurs.indexOf(temp);
        return index >= 0 ? generateurs.get(index) : null;
    }

    /**
     * Retourne la liste des maisons qui ne sont connectées à aucun générateur.
     * 
     * @return liste des maisons non connectées
     */
    public List<Maison> getMaisonsNonConnectees() {
        Set<Maison> connectees = new HashSet<>();

        for (List<Maison> liste : connexions.values()) {
            connectees.addAll(liste);
        }

        List<Maison> nonCo = new ArrayList<>();
        for (Maison m : maisons) {
            if (!connectees.contains(m)) {
                nonCo.add(m);
            }
        }

        return nonCo;
    }

    /**
     * Calcule la somme des consommations de toutes les maisons du réseau.
     * 
     * @return somme des consommations
     */
    private int sommeConsommations() {
        int somme = 0;
        for (Maison m : maisons) {
            somme += m.getConsommation().getValeur();
        }
        return somme;
    }

    /**
     * Calcule la somme des capacités de tous les générateurs du réseau.
     * 
     * @return somme des capacités
     */
    private double sommeCapacites() {
        double somme = 0.0;
        for (Generateur g : generateurs) {
            somme += g.getCapaciteMax();
        }
        return somme;
    }


    /**
     * Vérifie la structure du réseau pour détecter des erreurs.
     * 
     * - Maisons non connectées
     * - Maisons connectées à plusieurs générateurs
     * - Demande totale > capacité totale
     * 
     * @return liste des messages d'erreurs
     */
    public List<String> verifierStructure() {
        List<String> erreurs = new ArrayList<>();

        if (generateurs.isEmpty()) {
            erreurs.add("Le réseau ne contient aucun générateur.");
        }

        if (maisons.isEmpty()) {
            erreurs.add("Le réseau ne contient aucune maison.");
        }

        
        List<Maison> nonConnectees = getMaisonsNonConnectees();
        if (!nonConnectees.isEmpty()) {
            for (Maison m : nonConnectees) {
                erreurs.add("La maison " + m.getNom() + " n'est pas connectée à un générateur.");
            }
        }

        
        Map<Maison, Integer> compteur = new HashMap<>();
        for (List<Maison> liste : connexions.values()) {
            for (Maison m : liste) {
                compteur.put(m, compteur.getOrDefault(m, 0) + 1);
            }
        }

        for (Map.Entry<Maison, Integer> entry : compteur.entrySet()) {
            if (entry.getValue() > 1) {
                erreurs.add(
                    "La maison " + entry.getKey().getNom() +
                    " est connectée à plusieurs générateurs."
                );
            }
        }

        int demandeTotale = sommeConsommations();
        double capaciteTotale = sommeCapacites();

        if (demandeTotale > capaciteTotale) {
            erreurs.add(
                "Demande totale (" + demandeTotale +
                ") supérieure à la capacité totale des générateurs (" +
                capaciteTotale + ")"
            );
        }

        return erreurs;
    }
    

    @Override
   
    public String toString() {
        StringBuilder sb = new StringBuilder();

       
        sb.append("GÉNÉRATEURS: \n");
        for (Generateur g : generateurs) {
            sb.append(" - ").append(g).append("\n");
        }

        sb.append("\nMAISONS: \n");
        for (Maison m : maisons) {
            sb.append(" - ").append(m).append("\n");
        }

        sb.append("\nCONNEXIONS: \n");
        for (Generateur g : generateurs) {
            sb.append(" - ").append(g.getNom()).append(" -> ");

            List<Maison> maisonsDuGen = connexions.getOrDefault(g, Collections.emptyList());

            if (maisonsDuGen.isEmpty()) {
                sb.append("(aucune maison)");
            } else {
                for (int i = 0; i < maisonsDuGen.size(); i++) {
                    sb.append(maisonsDuGen.get(i).getNom());
                    if (i < maisonsDuGen.size() - 1) {
                        sb.append(", ");
                    }
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    
    
    public List<Maison> getMaisons() {
        return new ArrayList<>(maisons);
    }

    public List<Generateur> getGenerateurs() {
        return new ArrayList<>(generateurs);
    }

    public Generateur getGenerateurDeMaison(Maison maison) {
        return trouverGenerateurDeMaison(maison);
    }

    public List<Maison> getMaisonsDuGenerateur(Generateur gen) {
        return connexions.getOrDefault(gen, new ArrayList<>());
    }


    public Map<Generateur,List<Maison>> getConnexions(){
    	return connexions;
    }



    public void setLambda(double lambda) {
        this.lambda = lambda;
    }
    
    public double getLambda() {
        return lambda;
    }


}
