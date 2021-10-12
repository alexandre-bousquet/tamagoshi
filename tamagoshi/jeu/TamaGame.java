package tamagoshi.jeu;

import tamagoshi.tamagoshis.Tamagoshi;
import tamagoshi.util.User;

import java.util.ArrayList;

public class TamaGame {
    private ArrayList<Tamagoshi> listeTamagoshisDepart;
    private ArrayList<Tamagoshi> listeTamagoshisEnCours;

    public TamaGame() {
        this.listeTamagoshisDepart = new ArrayList<>();
        this.listeTamagoshisEnCours = new ArrayList<>();
    }

    public static void main(String[] args) {
        TamaGame game = new TamaGame();
        game.play();
        //System.out.println(game);
    }

    public void initialisation() {
        System.out.println("Entrez le nombre de tamagoshi.tamagoshis désiré : ");
        int nbTamagoshi = Integer.parseInt(User.saisieClavier());
        for (int i = 0; i < nbTamagoshi; i++) {
            System.out.println("Entrez le nom du tamagoshi numéro " + i + " : ");
            Tamagoshi t = new Tamagoshi(User.saisieClavier());
            this.listeTamagoshisDepart.add(t);
            this.listeTamagoshisEnCours.add(t);
        }
    }

    public void play() {
        this.initialisation();
        int cycle = 1;
        while (!this.listeTamagoshisEnCours.isEmpty() && cycle <= Tamagoshi.getLifeTime()) {
            System.out.println("------------ Cycle n°" + cycle + "-------------");
            this.listeTamagoshisEnCours.removeIf(t -> t.getEnergy() <= 0 || t.getFun() <= 0 || t.getAge() >= Tamagoshi.getLifeTime() );
            if (this.listeTamagoshisEnCours.isEmpty()) {
                break;
            }
            for (Tamagoshi t : this.listeTamagoshisEnCours) {
                t.parler();
            }
            System.out.println("Nourrir quel tamagoshi ?");
            this.nourrir();
            System.out.println("Jouer avec quel tamagoshi ?");
            this.jouer();
            for (Tamagoshi t : this.listeTamagoshisEnCours) {
                t.consommeRessources();
                t.vieillir();
            }
            cycle++;
        }
        System.out.println("------------ Fin de partie -------------");
        System.out.println("------------ Bilan -------------");
        this.resultat();
    }

    public void nourrir() {
        this.getTamagoshiAction().mange();
    }

    public void jouer() {
        this.getTamagoshiAction().joue();
    }

    public Tamagoshi getTamagoshiAction() {
        int count = 0;
        for (Tamagoshi t : this.listeTamagoshisEnCours) {
            System.out.println("(" + count + ") " + t.getName());
            count++;
        }
        System.out.println("Entrez un choix : ");
        int tamagoshi = Integer.parseInt(User.saisieClavier());
        if (tamagoshi > this.listeTamagoshisEnCours.size() - 1) {
            while (tamagoshi > this.listeTamagoshisEnCours.size()) {
                System.out.println("Choix de tamagoshi invalide !");
                tamagoshi = Integer.parseInt(User.saisieClavier());
            }
        }
        return this.listeTamagoshisEnCours.get(tamagoshi);
    }

    public void afficherTamagochisEnCours() {
        /*String affichage = "";
        for (int i = 0; i < this.listeTamagoshisEnCours.size(); i++) {
            affichage += "(" + i + ") " + this.listeTamagoshisEnCours.get(i).getName();
        }*/
    }

    public int score() {
        int maxAge = Tamagoshi.getLifeTime() * this.listeTamagoshisDepart.size();
        int currentAge = 0;
        for (Tamagoshi t : this.listeTamagoshisDepart) {
            currentAge += t.getAge();
        }
        return currentAge * 100 / maxAge;
    }

    public void resultat() {
        for (Tamagoshi t : this.listeTamagoshisEnCours) {
            if (this.listeTamagoshisDepart.contains(t)) {
                System.out.println(t.getName() + " a survécu et vous remercie :)");
            } else {
                System.out.println(t.getName() + " n'est pas arrivé au bout et ne vous félicite pas :(");
            }
        }
        System.out.println("Score obtenu : " + this.score() + "%");
    }

    @Override
    public String toString() {
        return "TamaGame {" +
                "listeTamagoshisDepart=" + this.listeTamagoshisDepart +
                ", listeTamagoshisVivants=" + this.listeTamagoshisEnCours +
                '}';
    }
}
