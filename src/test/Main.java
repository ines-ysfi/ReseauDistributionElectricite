package test;



/**
 * Classe principale du programme.
 * 
 * Gère le lancement de l'application selon les arguments fournis :
 * - sans argument : lancement en mode manuel
 * - avec un fichier et un paramètre lambda : chargement automatique du réseau
 */
public class Main {

    public static void main(String[] args) {
        MenuReseau menu;

        if (args.length == 0) {
            menu = new MenuReseau();

        } else if (args.length == 2) {
            try {
                String filePath = args[0];
                double lambda = Double.parseDouble(args[1]);
                menu = new MenuReseau(filePath, lambda);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    "Valeur de lambda invalide : un nombre est attendu."
                );
            }

        } else {
            throw new IllegalArgumentException(
                "Argument manquant : vous devez fournir le fichier et la valeur de lambda."
            );
        }

        menu.demarrer();
    }
}
