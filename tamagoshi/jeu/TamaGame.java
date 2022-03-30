package tamagoshi.jeu;

import tamagoshi.exceptions.NegativeLifeTimeException;
import tamagoshi.exceptions.TamagoshiNumberException;
import tamagoshi.tamagoshis.*;
import tamagoshi.util.User;

import java.util.*;
import java.util.logging.*;

/**
 * Classe de jeu.
 */
public class TamaGame {
    /**
     * Nombre de tamagoshis dans la partie.
     */
    private int nbTamagoshis;

    /**
     * Liste des tamagoshis créer au départ du jeu.
     */
    private List<Tamagoshi> listeTamagoshisDepart;

    /**
     * Liste des tamagoshis vivants à l'instant T.
     */
    private List<Tamagoshi> listeTamagoshisEnCours;

    /**
     * Liste des noms possibles pour les tamagoshis.
     */
    private List<String> names = new ArrayList<>();

    /**
     * Langage courant de l'ordinateur qui exécute le programme.
     */
    private static final Locale languageCourant = Locale.getDefault();

    /**
     * Messages dans la langue du langage courant.
     */
    public static ResourceBundle messages = ResourceBundle.getBundle("MessageBundle", languageCourant);

    /**
     * Root logger pour faire des logs.
     */
    public static Logger logger = Logger.getLogger("");

    /**
     * Constructeur de TamaGame
     */
    public TamaGame() {
        logger.setLevel(Level.ALL);
        StreamHandler handler = new StreamHandler();
        logger.addHandler(handler);
        this.listeTamagoshisDepart = new ArrayList<>();
        this.listeTamagoshisEnCours = new ArrayList<>();
    }

    /**
     * Exception générée lorsque la saisie clavier != Integer || <= 0 || > 5 (voir méthode { @link TamaGame#setNbTamagoshis(int) }).
     * Initialise les tamagoshis du jeu.
     */
    private void initTamagoshis() {
        System.out.println(messages.getString("askingHowManyTamagoshiToPlayWith"));
        FabriqueTamagoshi.getInstance();
        while (true) {
            try {
                this.setNbTamagoshis(Integer.parseInt(User.saisieClavier()));
                break;
            } catch (TamagoshiNumberException | NumberFormatException e) {
                logger.severe(messages.getString("tamagoshiNumberExceptionMessage"));
            }
        }
        for (int i = 0; i < this.getNbTamagoshis(); i++) {
            Tamagoshi t = FabriqueTamagoshi.generateRandomTamagoshi();
            this.listeTamagoshisDepart.add(t);
            this.listeTamagoshisEnCours.add(t);
        }
    }

    /**
     * Exception générée lorsque la saisie clavier != Integer || <= 0 (voir méthode {@link Tamagoshi#setLifeTime(int)}).
     * Initialise la durée de vie des tamagoshis.
     */
    private void initLifeTime() {
        System.out.println(messages.getString("askingLifeTime"));
        while (true) {
            try {
                Tamagoshi.setLifeTime(Integer.parseInt(User.saisieClavier()));
                break;
            } catch (NegativeLifeTimeException | NumberFormatException e) {
                logger.severe(messages.getString("lifeTimeExceptionMessage"));
            }
        }
    }

    /**
     * Initialise le jeu avec les méthodes précédentes.
     */
    private void initialisation() {
        this.initTamagoshis();
        this.initLifeTime();
    }

    /**
     * Lance le jeu.
     */
    public void play() {
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
                System.out.println(t.getName() + " : " + t.getMessage());
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
    private void nourrir() {
        System.out.println(messages.getString("whichTamagoshiToFeed"));
        Tamagoshi tamagoshi = this.getTamagoshiAction();
        tamagoshi.mange();
        System.out.println(tamagoshi.getName() + " : " + tamagoshi.getMessage());
    }

    /**
     * Demande à l'utilisateur de saisir un tamagoshi avec lequel jouer.
     */
    private void jouer() {
        System.out.println(messages.getString("whichTamagoshiToPlayWith"));
        Tamagoshi tamagoshi = this.getTamagoshiAction();
        tamagoshi.joue();
        System.out.println(tamagoshi.getName() + " : " + tamagoshi.getMessage());
    }

    /**
     * Fonction qui retourne le tamagoshi dont le numéro est saisis par l'utilisateur au clavier.
     * @return Tamagoshi choisit par l'utilisateur
     */
    private Tamagoshi getTamagoshiAction() {
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
    private int score() {
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
    private void resultat() {
        for (Tamagoshi t : this.listeTamagoshisDepart) {
            StringBuilder str = new StringBuilder()
                    .append(t.getName())
                    .append(" ")
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

    private int getNbTamagoshis() {
        return nbTamagoshis;
    }

    private void setNbTamagoshis(int nbTamagoshis) throws TamagoshiNumberException {
        if (nbTamagoshis <= 0 || nbTamagoshis > 5) {
            throw new TamagoshiNumberException(messages.getString("tamagoshiNumberExceptionMessage"));
        }
        this.nbTamagoshis = nbTamagoshis;
    }

    /**
     * Affiche la liste des tamagoshis.
     * @return {String} Données de la partie
     */
    @Override
    public String toString() {
        return "TamaGame : " + "\n" +
                "- Liste Tamagoshis au départ : " + this.listeTamagoshisDepart + "\n" +
                "- Liste Tamagoshis vivants : " + this.listeTamagoshisEnCours + "\n";
    }

    public static void main(String[] args) {
        TamaGame game = new TamaGame();
        game.play();
    }
}
