# Projet : Réseau de Distribution d'Électricité 

## Auteurs      BEDAD YASMINE      YOUSFI INES

## Description du projet:

Ce projet Java simule un réseau électrique simple et intelligent qui permet de :

*Représenter un ensemble de générateurs (avec leurs noms et leurs capacité en kW)  et un ensemble de maisons (noms et type de consommation : BASSE (10Kw), NORMAL (20Kw), FORTE (40Kw)) ainsi que les connexions entre ces générateurs et ces maisons  
*Calculer le cout global d'un réseau  composé de deux parties :
 Coût = Dispersion + (λ × Surcharge)
Dispersion : Écart entre le taux d'utilisation des générateurs                                                            Surcharge : Pénalité quand un générateur dépasse sa capacité maximale
*Proposer une meilleure architecture pour ce réseau à fin de démineur le cout globale et essayer  d'assurer l'équilibre entre  les sources et les consommateurs d'une manière automatique en se basant sur un algorithme simple et bien précis.
 
 **Fonctionnalités implémentées **
 
1) Configuration de  réseau manuelle:
 Construction de réseau manuellement: ajout de générateurs, maisons et connexions
suppression de connexions.

2) Lecture/Ecriture de fichiers :
 Chargement et sauvegarde de réseau  depuis/vers fichier de format texte spécifique.

3)Validation de réseau:
 Vérification robuste de la syntaxe (du réseau manuel ou du fichier) et des contraintes métiers du réseau (exp somme de capacité des générateurs>somme demandes consommateurs, une maison est reliée à un seul générateur, une maison possède obligatoirement un type de consommation...)
 
4)Interface utilisateur en ligne de commande:
 Menu interactif pour configurer un reseau manuellement ou en charger un à partir d'un fichier.
 
5)Interface graphique :
une interface graphique qui propose la configuration du réseau en ses deux maniéres (reseau manuel ou chargement de réseau a partir d'un fichier.

6)Optimisation de reseau :
 Implémentation d'algorithme de recherche local à multi-démarrage pour réduire le cout de distribution et assurer un certain équilibrage et trouver une meilleure configuration réseau.
 
7)Gestion complete des erreurs: 
 Gestion d'exceptions d'Entrées/Sorties, de format de fichiers ..

 
 **Structure du projet**
 
```
 ProjetPAA/
├── src/
|   ├── Application/
│   │   ├── AcceuilScene.java      #page d'acceuil qui propose les 2 méthodes de configurations
│   │   ├── ChargerFichierScene.java     # page de chargement du fichier du réseau à configurer
│   │   ├── Main.java                     # Classe qui lance l'interface
│   │   └── ReseauManuelScene.java    #page de construction de reseau manuellement 
│   ├── io/ 
│   │   ├── ReseauReader.java                 # Lecture et validation des fichiers
│   │   └── ReseauWriter.java                  # Sauvegarde du réseau en fichier
│   ├── optimisation/
│   │   └── OptimisateurReseau.java           # Algorithme d'optimisation
│   ├── reseau/
│   │   ├── Consommation.java                 # Enum type de maison BASSE/NORMAL/FORTE
│   │   ├── Generateur.java                   # Classe générateur
│   │   ├── Maison.java                       # Classe maison
│   │   └── Reseau.java                       # Calculs de cout et vérifications
│   └── test/
│       ├── Main.java                         # Point d'entrée (gestion des arguments)                      |	    |                                       
│       └── MenuReseau.java                   # Interface utilisateur en console(textuelle)
├── Tests/                                    #tests unitaires pour les differentes classes
|   ├── io/
│   │   ├── ReseauReaderTest.java   
│   │   └── ReseauWriterTest.java    
│   ├── optimisation/
│   │   └── OptimisateurReseauTest.java 
│   ├── reseau/
│   │   ├── GenerateurTest.java      
│   │   ├── MaisonTest.java          
│   │   └── ReseauTest.java          
│ 
|── Files.txt                                #fichiers des instances 
|
└── README.md                                # Ce fichier
```
  **Mode d'execution**
  Le projet propose deux points d'entrée pour l'exécution du programme. Le premier permet une utilisation via une interface textuelle en ligne de commande, tandis que le second lance une interface graphique développée avec JavaFX. Ces deux interfaces utilisent les mêmes classes de gestion du réseau et le même algorithme d'optimisation
  
 **la classe  pour exécuter le programme en utlisant l'interface textuelle** est test.Main 
 Deux façons pour lancer le programme 
 
 Mode manuel 
   java -cp bin test.Main
    
 Mode avec fichier 
  java -cp bin test.Main chemin/vers/fichier.txt 10.0  # on spécifie le chemin vers fichier et valeur λ
 
**la classe pour éxecuter le programme en utilisant l'interface graphique** estApplication.Main
  java --module-path "../javafx/lib" --add-modules javafx.controls Application.Main
   
   ###dépendances externes
Java 17
JavaFX pour l'interface graphique 
JUnit framework de tests

** Algorithme d'optimisation **

L'algorithme améliore le réseau en combinant plusieurs stratégies simples mais efficaces :

- Il effectue plusieurs démarrages indépendants pour explorer différentes configurations.
- Le premier démarrage utilise une solution gloutonne :pour chaque maison, l'algorithme teste sa connexion avec les différents générateurs et choisit à chaque fois celui qui améliore immédiatement le coût du réseau  pour obtenir rapidement une base de bonne qualité
- À partir de chaque solution, l'algorithme modifie aléatoirement les connexions entre maisons et générateurs, et ne conserve que les changements qui diminuent le coût total.
- Les démarrages suivants partent de solutions entièrement aléatoires, et la même amélioration locale est appliquée.
- À la fin, la meilleure configuration trouvée parmi tous les démarrages est conservée comme résultat final.

Cette approche est une amélioration du pseudo-code naïf, qui ne faisait que modifier aléatoirement les connexions à partir d'une seule solution initiale. Le glouton initial et les multi-démarrages permettent d'explorer l'espace des solutions plus efficacement et d'éviter les minima locaux.

