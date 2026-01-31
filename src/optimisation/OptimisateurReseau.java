package optimisation;

import java.util.*;
import reseau.*;


/**
 * Classe responsable de l'optimisation d'un réseau électrique.
 * 
 * L'objectif est de minimiser le coût global du réseau.
 */
public class OptimisateurReseau {

    private Random random = new Random();


    /**
     * Crée une copie complète d'un réseau.
     * 
     * Les maisons, générateurs et connexions sont dupliqués
     * afin de ne pas modifier le réseau original.
     * 
     * @param r réseau à copier
     * @return une copie indépendante du réseau
     */
    private Reseau copierReseau(Reseau r) {
        Reseau copie = new Reseau();
        for (Generateur g : r.getGenerateurs())
            copie.ajouterGenerateur(new Generateur(g.getNom(), g.getCapaciteMax()));
        for (Maison m : r.getMaisons())
            copie.ajouterMaison(new Maison(m.getNom(), m.getConsommation()));

        for (Maison m : r.getMaisons()) {
            Generateur g = r.getGenerateurDeMaison(m);
            if (g != null) {
                Generateur g2 = copie.trouverGenerateurParNom(g.getNom());
                Maison m2 = copie.trouverMaisonParNom(m.getNom());
                copie.connecter(m2, g2);
            }
        }
        return copie;
    }

    /**
     * Applique une optimisation gloutonne sur le réseau.
     * 
     * Pour chaque maison, on teste tous les générateurs possibles
     * et on choisit celui qui minimise le coût du réseau.
     * 
     * @param reseau réseau de départ
     * @return réseau optimisé
     */

    private Reseau optimisationGloutonne(Reseau reseau) {   
        Reseau best = copierReseau(reseau);
        for (Maison m : best.getMaisons()) {
            Generateur actuel = best.getGenerateurDeMaison(m);
            double coutActuel = best.calculerCout();
            Generateur meilleurGen = actuel;

            for (Generateur g : best.getGenerateurs()) {
                if (g.equals(actuel)) continue;
                Reseau tentative = copierReseau(best);
                tentative.modifierConnexion(
                        tentative.trouverMaisonParNom(m.getNom()),
                        tentative.trouverGenerateurParNom(actuel.getNom()),
                        tentative.trouverGenerateurParNom(g.getNom())
                );
                double newCout = tentative.calculerCout();
                if (newCout < coutActuel) {
                    coutActuel = newCout;
                    meilleurGen = g;
                }
            }

            if (!meilleurGen.equals(actuel)) {
                best.modifierConnexion(m, actuel, meilleurGen);
            }
        }
        return best;
    }

    /**
     * Génère une solution aléatoire à partir d'un réseau.
     * 
     * Chaque maison peut être reconnectée à un générateur choisi
     * aléatoirement.
     * 
     * @param reseau réseau de départ
     * @return nouvelle solution aléatoire
     */
    private Reseau genererSolutionAleatoire(Reseau reseau) {
        Reseau solution = copierReseau(reseau);
        List<Maison> maisons = solution.getMaisons();
        List<Generateur> generateurs = solution.getGenerateurs();
        
        for (Maison m : maisons) {
            Generateur actuel = solution.getGenerateurDeMaison(m);
            Generateur nouveau = generateurs.get(random.nextInt(generateurs.size()));
            
            if (actuel != null && !actuel.equals(nouveau)) {
                solution.modifierConnexion(m, actuel, nouveau);
            }
        }
        
        return solution;
    }

    
    
    /**
     * Applique une amélioration locale complète sur un réseau.
     * 
     * Des modifications aléatoires sont testées et conservées
     * uniquement si elles améliorent le coût.
     * 
     * L'algorithme s'arrête après un nombre maximal d'itérations
     * ou après trop d'itérations sans amélioration.
     * 
     * @param reseau réseau de départ
     * @param maxIterations nombre maximal d'itérations
     * @return réseau amélioré
     */
    private Reseau ameliorationLocaleComplete(Reseau reseau, int maxIterations) {
        Reseau current = copierReseau(reseau);
        double coutActuel = current.calculerCout();
        
        List<Maison> maisons = current.getMaisons();
        List<Generateur> generateurs = current.getGenerateurs();
        
        int iterationsSansAmelioration = 0;
        
        for (int i = 0; i < maxIterations && iterationsSansAmelioration < 1000; i++) {
            Maison m = maisons.get(random.nextInt(maisons.size()));
            Generateur ancien = current.getGenerateurDeMaison(m);
            Generateur nouveau = generateurs.get(random.nextInt(generateurs.size()));
            
            if (ancien.equals(nouveau)) continue;
            
            current.modifierConnexion(m, ancien, nouveau);
            double nouveauCout = current.calculerCout();
            
            if (nouveauCout < coutActuel) {
                coutActuel = nouveauCout;
                iterationsSansAmelioration = 0;
            } else {
                current.modifierConnexion(m, nouveau, ancien);
                iterationsSansAmelioration++;
            }
        }
        
        return current;
    }

    
    

    /**
     * Lance une optimisation par multi-démarrages.
     * 
     * La première solution est obtenue par optimisation gloutonne"intelligente",
     * les suivantes sont générées aléatoirement.
     * 
     * Chaque solution est ensuite améliorée localement,
     * et la meilleure solution globale est conservée.
     * 
     * @param reseau réseau initial
     * @param nombreDemarrages nombre de redémarrages
     * @return la meilleure solution trouvée
     */
    public Reseau optimisationMultiDemarrages(Reseau reseau, int nombreDemarrages) {
        Reseau meilleurGlobal = null;
        double meilleurCoutGlobal = Double.MAX_VALUE;
        int itt= reseau.getMaisons().size() * reseau.getGenerateurs().size()*1000;
        for (int restart = 0; restart < nombreDemarrages; restart++) {
            Reseau solution;
            
            if (restart == 0) {

                solution = optimisationGloutonne(reseau);
            } else {

                solution = genererSolutionAleatoire(reseau);
            }      

            solution = ameliorationLocaleComplete(solution, itt);
            
            double cout = solution.calculerCout();
            if (cout < meilleurCoutGlobal) {
                meilleurGlobal = solution;
                meilleurCoutGlobal = cout;
            }
        }
        
        return meilleurGlobal;
    }
}