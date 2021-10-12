package tamagoshi.jeu;

import tamagoshi.tamagoshis.Tamagoshi;
import tamagoshi.util.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class TamaGame {
    private ArrayList<Tamagoshi> listeTamagoshisDepart;
    private ArrayList<Tamagoshi> listeTamagoshisEnCours;
    private ArrayList<String> names = new ArrayList<>();

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
        Scanner scan = new Scanner(TamaGame.class.getResourceAsStream("/tamagoshi/names.txt"));
        int ligne = 1;
        while (scan.hasNextLine()) {
            String nom = scan.nextLine();
            names.add(nom);
            ligne++;
        }
        scan.close();
        Collections.shuffle(names);
        System.out.println("Entrez le nombre de tamagoshis désiré : ");
        int nbTamagoshi = Integer.parseInt(User.saisieClavier());
        System.out.println("Entrez la durée de vie des tamagoshis : ");
        Tamagoshi.setLifeTime(Integer.parseInt(User.saisieClavier()));
        /*while (nbTamagoshi <= 0) {
            System.out.println("Bon fait un effort on te demande juste un entier positif là !");
            nbTamagoshi = Integer.parseInt(User.saisieClavier());
        }*/
        for (int i = 0; i < nbTamagoshi; i++) {
            double rand = Math.random();
            int indexName = new Random().nextInt(names.size());
            String name = names.get(indexName);
            names.remove(indexName);
            Tamagoshi t;
            if (rand <= 0.45) {
                t = new GrosJoueur(name);
            } else if (rand > 0.45 && rand <= 0.9) {
                t = new GrosMangeur(name);
            } else {
                t = new ConnardJoueur(name);
            }
            this.listeTamagoshisDepart.add(t);
            this.listeTamagoshisEnCours.add(t);
        }
    }

    public void play() {
        this.initialisation();
        int cycle = 1;
        while (!this.listeTamagoshisEnCours.isEmpty() && cycle <= Tamagoshi.getLifeTime()) {
            System.out.println("------------ Cycle n°" + cycle + " ------------");
            this.listeTamagoshisEnCours.removeIf(t -> t.getEnergy() <= 0 || t.getFun() <= 0 || t.getAge() >= Tamagoshi.getLifeTime());
            if (this.listeTamagoshisEnCours.isEmpty()) {
                break;
            }
            for (Tamagoshi t : this.listeTamagoshisEnCours) {
                t.parler();
            }
            this.nourrir();
            this.jouer();
            for (Tamagoshi t : this.listeTamagoshisEnCours) {
                //t.consommeRessources();
                t.consommeEnergy();
                t.consommeFun();
                t.vieillir();
            }
            cycle++;
        }
        System.out.println("------------- Fin de partie ------------");
        System.out.println("------------ Bilan -------------");
        this.resultat();
    }

    public void nourrir() {
        System.out.println("Nourrir quel tamagoshi ?");
        this.getTamagoshiAction().mange();
    }

    public void jouer() {
        System.out.println("Jouer avec quel tamagoshi ?");
        this.getTamagoshiAction().joue();
    }

    public Tamagoshi getTamagoshiAction() {
        int count = 0;
        for (Tamagoshi t : this.listeTamagoshisEnCours) {
            System.out.println("(" + count + ") " + t.getName());
            count++;
        }
        System.out.println("Entrez un choix : ");
        int tamagoshiSelected = Integer.parseInt(User.saisieClavier());
        if (tamagoshiSelected > this.listeTamagoshisEnCours.size() - 1) {
            while (tamagoshiSelected > this.listeTamagoshisEnCours.size() - 1) {
                System.out.println("Choix de tamagoshi invalide !");
                tamagoshiSelected = Integer.parseInt(User.saisieClavier());
            }
        }
        return this.listeTamagoshisEnCours.get(tamagoshiSelected);
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
        for (Tamagoshi t : this.listeTamagoshisDepart) {
            if (this.listeTamagoshisEnCours.contains(t)) {
                System.out.println(t.getName() + " qui était un " + t.getClass().getSimpleName() + " a survécu et vous remercie :)");
            } else {
                System.out.println(t.getName() + " qui était un " + t.getClass().getSimpleName() + " n'est pas arrivé au bout et ne vous félicite pas :(");
            }
        }
        System.out.println("Niveau de difficulté : " + this.listeTamagoshisDepart.size());
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
