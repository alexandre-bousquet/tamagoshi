package tamagoshi.tamagoshis;

import tamagoshi.exceptions.NegativeLifeTimeException;
import tamagoshi.jeu.TamaGame;

import java.util.Random;

/**
 * Tamagoshi lambda.
 */
public class Tamagoshi {
    /**
     * Ege du tamagoshi.
     */
    private int age;
    /**
     * Energie maximale d'un tamagoshi.
     */
    private int maxEnergy;
    /**
     * Energie du tamagoshi (ne peut être supérieure à maxEnergy).
     */
    private int energy;
    private String name;
    private static int lifeTime;
    private int fun;
    private int maxFun;

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
     * @return boolean
     */
    public boolean parler() {
        if (this.getEnergy() > 4 && this.getFun() > 4) {
            System.out.println(this.getName() + " : " + TamaGame.messages.getString("everythingIsFine"));
            return true;
        } else if (this.getEnergy() <= 4 && this.getFun() > 4) {
            System.out.println(this.getName() + " : " + TamaGame.messages.getString("imHungry"));
            return false;
        } else if (this.getEnergy() > 4 && this.getFun() <= 4) {
            System.out.println(this.getName() + " : " + TamaGame.messages.getString("imBored"));
            return false;
        } else {
            System.out.println(this.getName() + " : " + TamaGame.messages.getString("imHungryAndBored"));
            return false;
        }
    }

    /**
     * Remonte l'énergie du tamagoshi.
     * @return boolean
     */
    public boolean mange() {
        if (this.getEnergy() < this.getMaxEnergy()) {
            this.setEnergy(this.getEnergy() + (new Random().nextInt(3) + 1));
            if (this.getEnergy() > this.getMaxEnergy()) {
                this.setEnergy(this.getMaxEnergy());
            }
            System.out.println(this.getName() + " : " + TamaGame.messages.getString("thanksIWasHungry"));
            return true;
        } else {
            System.out.println(this.getName() + " : " + TamaGame.messages.getString("imNotHungry"));
            return false;
        }
    }

    /**
     * Remonte le fun du tamagoshi.
     * @return boolean
     */
    public boolean joue() {
        if (this.getFun() < this.getMaxFun()) {
            this.setFun(this.getFun() + (new Random().nextInt(3) + 1));
            if (this.getFun() > this.getMaxFun()) {
                this.setFun(this.getMaxFun());
            }
            System.out.println(this.getName() + " : " + TamaGame.messages.getString("thanksIWasBored"));
            return true;
        } else {
            System.out.println(this.getName() + " : " + TamaGame.messages.getString("imNotBored"));
            return false;
        }
    }

    /**
     * Baisse l'énergie du tamagoshi.
     * @return boolean
     */
    public boolean consommeEnergy() {
        this.energy--;
        if (this.getEnergy() <= 0) {
            System.out.println(this.getName() + " : " + TamaGame.messages.getString("dieEmptyEnergy"));
            return false;
        } else {
            return true;
        }
    }

    /**
     * Baisse le fun du tamagoshi.
     * @return boolean
     */
    public boolean consommeFun() {
        this.fun--;
        if (this.getFun() <= 0) {
            System.out.println(this.getName() + " : " + TamaGame.messages.getString("dieEmptyFun"));
            return false;
        } else {
            return true;
        }
    }

    /**
     * @deprecated car deux autres fonctions font son travail.
     * @return boolean
     */
    @Deprecated
    public boolean consommeRessources() {
        this.energy--;
        this.fun--;
        if (this.getEnergy() <= 0) {
            System.out.println(this.getName() + " : Arrrrrggh !");
            return false;
        } else if (this.fun <= 0) {
            System.out.println(this.getName() + " : Je fais une dépression, ciao !");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Augmente l'âge du tamagoshi.
     */
    public void vieillir() {
        this.age++;
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
