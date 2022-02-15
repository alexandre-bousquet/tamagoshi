package tamagoshi.jeu;

import tamagoshi.exceptions.NegativeLifeTimeException;
import tamagoshi.exceptions.TamagoshiNumberException;
import tamagoshi.tamagoshis.*;
import tamagoshi.util.User;

import java.util.*;

/**
 * Classe de jeu.
 */
public class TamaGame {
    private int nbTamagoshis;

    /**
     * Liste des tamagoshis créer au départ du jeu.
     */
    private ArrayList<Tamagoshi> listeTamagoshisDepart;

    /**
     * Liste des tamagoshis vivants à l'instant T.
     */
    private ArrayList<Tamagoshi> listeTamagoshisEnCours;

    /**
     * Liste des noms possibles pour les tamagoshis.
     */
    private ArrayList<String> names = new ArrayList<>();

    private static final Locale languageCourant = Locale.getDefault();
    public static ResourceBundle messages = ResourceBundle.getBundle("MessageBundle", languageCourant);

    public TamaGame() {
        this.listeTamagoshisDepart = new ArrayList<>();
        this.listeTamagoshisEnCours = new ArrayList<>();
    }

    /**
     * Initialise la liste des noms possibles pour les tamagoshis.
     */
    public void initNamesList() {
        Scanner scan = new Scanner(Objects.requireNonNull(TamaGame.class.getResourceAsStream("/tamagoshi/names.txt")));
        while (scan.hasNextLine()) {
            String nom = scan.nextLine();
            names.add(nom);
        }
        scan.close();
        Collections.shuffle(names);
    }

    /**
     * Initialise les tamagoshis du jeu.
     */
    public void initTamagoshis() {
        System.out.println(messages.getString("askingHowManyTamagoshiToPlayWith"));
        boolean loop = true;
        while (loop) {
            try {
                this.setNbTamagoshis(Integer.parseInt(User.saisieClavier()));
                loop = false;
            } catch (TamagoshiNumberException | NumberFormatException e) {
                logger.severe(messages.getString("tamagoshiNumberExceptionMessage"));
            }
        }
        for (int i = 0; i < nbTamagoshis; i++) {
            double rand = Math.random();
            int indexName = new Random().nextInt(this.names.size());
            String name = this.names.get(indexName);
            this.names.remove(indexName);
            Tamagoshi t;
            if (rand <= 0.40) {
                t = new GrosJoueur(name);
            } else if (rand <= 0.8) {
                t = new GrosMangeur(name);
            } else if (rand <= 0.9) {
                t = new Cachotier(name);
            } else {
                t = new Bipolaire(name);
            }
            this.listeTamagoshisDepart.add(t);
            this.listeTamagoshisEnCours.add(t);
        }
    }

    /**
     * Exception générée lorsque la saisie clavier != Integer || <= 0
     * Initialise la durée de vie des tamagoshis
     */
    public void initLifeTime() {
        System.out.println(messages.getString("askingLifeTime"));
        boolean loop = true;
        while (loop) {
            try {
                Tamagoshi.setLifeTime(Integer.parseInt(User.saisieClavier()));
                loop = false;
            } catch (NegativeLifeTimeException | NumberFormatException e) {
                System.out.println(messages.getString("lifeTimeExceptionMessage"));
            }
        }
    }

    /**
     * Initialise le jeu avec les méthodes précédentes.
     */
    public void initialisation() throws NegativeLifeTimeException, TamagoshiNumberException {
        this.initNamesList();
        this.initTamagoshis();
        this.initLifeTime();
    }

    /**
     * Lance le jeu.
     */
    public void play() throws NegativeLifeTimeException, TamagoshiNumberException {
        this.initialisation();
        int cycle = 1;
        while (!this.listeTamagoshisEnCours.isEmpty() && cycle <= Tamagoshi.getLifeTime()) {
            this.listeTamagoshisEnCours.removeIf(t -> t.getEnergy() <= 0 || t.getFun() <= 0 || t.getAge() >= Tamagoshi.getLifeTime());
            if (this.listeTamagoshisEnCours.isEmpty()) {
                break;
            }
            System.out.println("------------ " + messages.getString("cycle")+ " n°" + cycle + " ------------");
            for (Tamagoshi t : this.listeTamagoshisEnCours) {
                t.parler();
            }
            this.nourrir();
            this.jouer();
            for (Tamagoshi t : this.listeTamagoshisEnCours) {
                t.consommeEnergy();
                t.consommeFun();
                t.vieillir();
            }
            cycle++;
        }
        System.out.println("------------- " + messages.getString("endOfTheGame") + " ------------");
        System.out.println("------------ " + messages.getString("result") + " -------------");
        this.resultat();
    }

    /**
     * Demande à l'utilisateur de saisir un tamagoshi à nourrir.
     */
    public void nourrir() {
        System.out.println(messages.getString("whichTamagoshiToFeed"));
        this.getTamagoshiAction().mange();
    }

    /**
     * Demande à l'utilisateur de saisir un tamagoshi avec lequel jouer.
     */
    public void jouer() {
        System.out.println(messages.getString("whichTamagoshiToPlayWith"));
        this.getTamagoshiAction().joue();
    }

    /**
     * Fonction qui retourne le tamagoshi dont le numéro est saisis par l'utilisateur au clavier.
     * @return Tamagoshi choisit par l'utilisateur
     */
    public Tamagoshi getTamagoshiAction() {
        int count = 0;
        for (Tamagoshi t : this.listeTamagoshisEnCours) {
            System.out.println("(" + count + ") " + t.getName());
            count++;
        }
        System.out.println(messages.getString("makeAChoice"));
        int tamagoshiSelected = 0;
        boolean loop = true;
        while (loop) {
            try {
                tamagoshiSelected = Integer.parseInt(User.saisieClavier());
                if (tamagoshiSelected <= this.listeTamagoshisEnCours.size() - 1) {
                    loop = false;
                } else {
                    System.out.println(messages.getString("invalidTamagoshiChoice"));
                }
            } catch (NumberFormatException e) {
                System.out.println(messages.getString("invalidTamagoshiChoice"));
                e.getMessage();
            }
        }
        return this.listeTamagoshisEnCours.get(tamagoshiSelected);
    }

    /**
     * Calcule le score et le retourne.
     * Il est égal à ((âge des tamagoshis en vie * 100) / âge total de tous les tamagoshis)
     * @return {int} = score calculé
     */
    public int score() {
        int maxAge = Tamagoshi.getLifeTime() * this.listeTamagoshisDepart.size();
        int currentAge = 0;
        for (Tamagoshi t : this.listeTamagoshisDepart) {
            currentAge += t.getAge();
        }
        return currentAge * 100 / maxAge;
    }

    /**
     * Affiche le score, la difficulté et l'état de chaque tamagoshi à la fin de la partie.
     */
    public void resultat() {
        for (Tamagoshi t : this.listeTamagoshisDepart) {
            StringBuilder str = new StringBuilder()
                    .append(t.getName())
                    .append(messages.getString("whoWasA"))
                    .append(" ")
                    .append(t.getClass().getSimpleName())
                    .append(" ");
            if (this.listeTamagoshisEnCours.contains(t)) {
                str.append(messages.getString("hasSurvived"));
            } else {
                str.append(messages.getString("hasNotSurvived"));
            }
            System.out.println(str);
        }
        System.out.println(messages.getString("difficultyLevel") + " : " + this.listeTamagoshisDepart.size());
        System.out.println(messages.getString("finalScore") + " : " + this.score() + "%");
    }

    public void setNbTamagoshis(int nbTamagoshis) throws TamagoshiNumberException {
        if (nbTamagoshis <= 0 || nbTamagoshis > 5) {
            throw new TamagoshiNumberException(messages.getString("tamagoshiNumberExceptionMessage"));
        }
        this.nbTamagoshis = nbTamagoshis;
    }

    /**
     *
     * @return {String} Données de la partie
     */
    @Override
    public String toString() {
        return "TamaGame : " + "\n" +
                "- Liste Tamagoshis au départ : " + this.listeTamagoshisDepart + "\n" +
                "- Liste Tamagoshis vivants : " + this.listeTamagoshisEnCours + "\n";
    }

    public static void main(String[] args) throws NegativeLifeTimeException, TamagoshiNumberException {
        TamaGame game = new TamaGame();
        game.play();
    }
}
