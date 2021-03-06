package tamagoshi.tamagoshis;

import tamagoshi.jeu.TamaGame;

import java.util.*;

/**
 * Patron de conception fabrique concrete qui permet de ne pas modifier le code de l'application en cas d'ajout d'un Tamagoshi sauf au niveau de la méthode {@link FabriqueTamagoshi#generateRandomTamagoshi()}.
 */
public class FabriqueTamagoshi {
    /**
     * Instance unique de {@link FabriqueTamagoshi}.
     */
    private static FabriqueTamagoshi instance;

    /**
     * Liste des noms disponible pour les {@link Tamagoshi} générés.
     */
    private final List<String> listeNomTamagoshi;

    /**
     * Constructeur de la classe {@link FabriqueTamagoshi}. Il ne peut être appelé que dans {@link FabriqueTamagoshi#getInstance()}.
     */
    private FabriqueTamagoshi() {
        this.listeNomTamagoshi = new ArrayList<>();
        this.initNamesList();
    }

    /**
     * Patron de conception Singleton qui permet d'avoir qu'une seule instance possible de la fabrique.
     * @return La seule et unique instance de la {@link FabriqueTamagoshi} (si elle n'existe pas elle est créée).
     */
    public static FabriqueTamagoshi getInstance() {
        if (instance == null) {
            instance = new FabriqueTamagoshi();
        }
        return instance;
    }

    /**
     * Initialise la liste des noms possibles pour les {@link Tamagoshi}.
     */
    private void initNamesList() {
        Scanner scan = new Scanner(Objects.requireNonNull(TamaGame.class.getResourceAsStream("/tamagoshi/names.txt")));
        while (scan.hasNextLine()) {
            String nom = scan.nextLine();
            this.listeNomTamagoshi.add(nom);
        }
        scan.close();
        Collections.shuffle(this.listeNomTamagoshi);
    }

    /**
     * Génère un {@link Tamagoshi} aléatoire avec un nom aléatoire en fonction de probabilités établies par le programmeur.
     * @return Le {@link Tamagoshi} généré.
     */
    public Tamagoshi generateRandomTamagoshi() {
        double rand = Math.random();
        int indexName = new Random().nextInt(this.listeNomTamagoshi.size());
        String name = this.listeNomTamagoshi.get(indexName);
        this.listeNomTamagoshi.remove(indexName);
        Tamagoshi t;
        if (rand <= 0.40) {
            t = new GrosJoueur(name);
        } else if (rand <= 0.8) {
            t = new GrosMangeur(name);
        } else if (rand <= 0.9) {
            t = new Cachotier(name);
        } else if (rand <= 0.95) {
            t = new Bipolaire(name);
        } else {
            t = new Suicidaire(name);
        }
        return t;
    }
}
