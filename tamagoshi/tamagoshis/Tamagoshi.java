package tamagoshi.tamagoshis;

import tamagoshi.exceptions.NegativeLifeTimeException;
import tamagoshi.graphic.TamaGameGraphique;
import tamagoshi.jeu.TamaGame;

import java.util.Random;

/**
 * Tamagoshi lambda.
 */
public abstract class Tamagoshi extends TamaGameGraphique {
    /**
     * Age du tamagoshi.
     */
    private int age;

    /**
     * Energie maximale d'un tamagoshi.
     */
    private final int maxEnergy;

    /**
     * Energie du tamagoshi (ne peut être supérieure à maxEnergy).
     */
    private int energy;

    /**
     * Nom du tamagoshi.
     */
    private final String name;

    /**
     * Durée de vie commune des tamagoshis.
     */
    private static int lifeTime;

    /**
     * Fun du tamagoshi.
     */
    private int fun;

    /**
     * Fun maximum du tamagoshi.
     */
    private final int maxFun;

    /**
     * Message d'état du tamagoshi.
     */
    private String message;

    /**
     * Constructeur de la classe {@link Tamagoshi}. Elle gère les {@link Tamagoshi} et toutes les classes qui en hérite. Il est utilisé dans une fabrique qui génère des {@link Tamagoshi} aléatoire et leur attribut un nom également aléatoire.
     * @param name {@link String} égal au nom souhaité du tamagoshi.
     */
    public Tamagoshi(String name) {
        this.age = 0;
        this.maxEnergy = new Random().nextInt(5) + 5;
        this.energy = new Random().nextInt(5) + 3;
        if (this.energy > this.maxEnergy) {
            this.energy = this.maxEnergy;
        }
        this.maxFun = new Random().nextInt(5) + 5;
        this.fun = new Random().nextInt(5) + 3;
        if (this.fun > this.maxFun) {
            this.fun = this.maxFun;
        }
        this.name = name;
    }

    /**
     * Le tamagoshi exprime dans quel état il est.
     */
    public void parler() {
        if (this.getEnergy() > 4 && this.getFun() > 4) {
            this.setMessage(TamaGame.messages.getString("everythingIsFine"));
        } else if (this.getEnergy() <= 4 && this.getFun() > 4) {
            this.setMessage(TamaGame.messages.getString("imHungry"));
        } else if (this.getEnergy() > 4 && this.getFun() <= 4) {
            this.setMessage(TamaGame.messages.getString("imBored"));
        } else {
            this.setMessage(TamaGame.messages.getString("imHungryAndBored"));
        }
    }

    /**
     * Remonte l'énergie du tamagoshi.
     */
    public void mange() {
        if (this.getEnergy() < this.getMaxEnergy()) {
            this.setEnergy(this.getEnergy() + (new Random().nextInt(4) + 2));
            if (this.getEnergy() > this.getMaxEnergy()) {
                this.setEnergy(this.getMaxEnergy());
            }
            this.setMessage(TamaGame.messages.getString("thanksIWasHungry"));
        } else {
            this.setMessage(TamaGame.messages.getString("imNotHungry"));
        }
    }

    /**
     * Remonte le fun du tamagoshi.
     */
    public void joue() {
        if (this.getFun() < this.getMaxFun()) {
            this.setFun(this.getFun() + (new Random().nextInt(4) + 2));
            if (this.getFun() > this.getMaxFun()) {
                this.setFun(this.getMaxFun());
            }
            this.setMessage(TamaGame.messages.getString("thanksIWasBored"));
        } else {
            this.setMessage(TamaGame.messages.getString("imNotBored"));
        }
    }

    /**
     * Baisse l'énergie du tamagoshi.
     * @return Faux s'il est mort.
     */
    public boolean consommeEnergy() {
        this.energy--;
        if (this.getEnergy() <= 0) {
            this.setMessage(TamaGame.messages.getString("dieEmptyEnergy"));
            return false;
        } else {
            return true;
        }
    }

    /**
     * Baisse le fun du tamagoshi.
     * @return Faux s'il est mort.
     */
    public boolean consommeFun() {
        this.fun--;
        if (this.getFun() <= 0) {
            this.setMessage(TamaGame.messages.getString("dieEmptyFun"));
            return false;
        } else {
            return true;
        }
    }

    /**
     * @deprecated car deux autres fonctions font son travail.
     * @return Faux s'il est mort.
     */
    @Deprecated
    public boolean consommeRessources() {
        this.energy--;
        this.fun--;
        if (this.getEnergy() <= 0) {
            System.out.println(this.getName() + " : Arrrrrggh !");
            return false;
        } else if (this.getFun() <= 0) {
            System.out.println(this.getName() + " : Je fais une dépression, ciao !");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Augmente l'âge du tamagoshi.
     */
    public boolean vieillir() {
        this.age++;
        return this.getAge() < getLifeTime();
    }

    /**
     * Vérifie si le tamagoshi est encore en vie ou non.
     * @return Vrai si son énergie et sont fun son tous les deux >= 0.
     */
    public boolean isAlive() {
        return this.getEnergy() >= 0 && this.getFun() >= 0;
    }

    // Getters & Setters

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void setFun(int fun) {
        this.fun = fun;
    }

    public int getAge() {
        return age;
    }

    public int getEnergy() {
        return energy;
    }

    public int getFun() {
        return fun;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public int getMaxFun() {
        return maxFun;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static void setLifeTime(int lifeTime) throws NegativeLifeTimeException {
        if (lifeTime <= 0) {
            throw new NegativeLifeTimeException(TamaGame.messages.getString("lifeTimeExceptionMessage"));
        }
        Tamagoshi.lifeTime = lifeTime;
    }

    public static int getLifeTime() {
        return lifeTime;
    }

    @Override
    public String toString() {
        return name + "{" +
                "age=" + this.age +
                ", maxEnergy=" + this.maxEnergy +
                ", energy=" + this.energy +
                ", maxFun=" + this.maxFun +
                ", fun=" + this.fun +
                '}';
    }
}
