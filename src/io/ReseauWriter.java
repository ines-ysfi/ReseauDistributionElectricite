package io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import reseau.*;

public class ReseauWriter {

	
	  /**
     * Écrit le contenu du réseau dans un fichier.
     * 
     * Les générateurs sont écrits en premier, puis les maisons,
     * puis les connexions entre générateurs et maisons.
     * 
     * @param chemin chemin du fichier à créer/écrire
     * @param reseau le réseau à écrire dans le fichier
     * @throws IOException si le fichier ne peut pas être créé ou écrit
     */
    public void ecrireFichier(String chemin, Reseau reseau) throws IOException {
        FileWriter fw = new FileWriter(chemin);

        for (Generateur g : reseau.getGenerateurs()) {
            fw.write("generateur(" + g.getNom() + "," + g.getCapaciteMax() + ").\n");
        }

        for (Maison m : reseau.getMaisons()) {
            fw.write("maison(" + m.getNom() + "," + m.getConsommation().name() + ").\n");
        }

        Map<Generateur, List<Maison>> connexions = reseau.getConnexions();
        for (Map.Entry<Generateur, List<Maison>> entry : connexions.entrySet()) {
            Generateur g = entry.getKey();
            List<Maison> maisons = entry.getValue();

            for (Maison m : maisons) {
                fw.write("connexion(" + g.getNom() + "," + m.getNom() + ").\n");
            }
        }
        fw.close();
    }
}
