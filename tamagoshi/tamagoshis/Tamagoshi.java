package tamagoshi.tamagoshis;

import java.util.Random;

public class Tamagoshi {
    private int age;
    private int maxEnergy;
    private int energy;
    private String name;
    private static int lifeTime = 10;
    private Random random;
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

    public boolean parler() {
        if (this.energy > 4 && this.fun > 4) {
            System.out.println(this.getName() + " : Tout va bien pour le moment !");
            return true;
        } else if (this.energy <= 4 && this.fun > 4) {
            System.out.println(this.getName() + " : Je suis affamé nourris moi humain !");
            return false;
        } else if (this.energy > 4 && this.fun <= 4) {
            System.out.println(this.getName() + " : Je meurs d'ennui, distrais moi !");
            return false;
        } else {
            System.out.println(this.getName() + " : Je suis affamé et je meurs d'ennui va falloir te bouger un peu !");
            return false;
        }
    }

    public boolean mange() {
        if (this.energy < this.maxEnergy) {
            this.setEnergy(this.energy + (new Random().nextInt(3) + 1));
            if (this.energy > this.maxEnergy) {
                this.setEnergy(this.maxEnergy);
            }
            System.out.println(this.getName() + " : Palala j'avais trop la dalle merci !");
            return true;
        } else {
            System.out.println(this.getName() + " : Je n'ai pas faim !");
            return false;
        }
    }

    public boolean joue() {
        if (this.fun < this.maxFun) {
            this.setFun(this.fun + (new Random().nextInt(3) + 1));
            if (this.fun > this.maxFun) {
                this.setFun(this.maxFun);
            }
            System.out.println(this.getName() + " : On se marre HAHAHA (cringe) !");
            return true;
        } else {
            System.out.println(this.getName() + " : Je n'ai pas envie de jouer laisse moi tranquille !");
            return false;
        }
    }

    public boolean consommeEnergy() {
        this.energy--;
        if (this.energy <= 0) {
            System.out.println(this.getName() + " : Arrrrrggh !");
            return false;
        } else {
            return true;
        }
    }

    public boolean consommeFun() {
        this.fun--;
        if (this.fun <= 0) {
            System.out.println(this.getName() + " : Je fais une dépression, ciao !");
            return false;
        } else {
            return true;
        }
    }

    @Deprecated
    public boolean consommeRessources() {
        this.energy--;
        this.fun--;
        if (this.energy <= 0) {
            System.out.println(this.getName() + " : Arrrrrggh !");
            return false;
        } else if (this.fun <= 0) {
            System.out.println(this.getName() + " : Je fais une dépression, ciao !");
            return false;
        } else {
            return true;
        }
    }

    public void vieillir() {
        this.age++;
    }

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

    public String getName() {
        return name;
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

    public static int getLifeTime() {
        return lifeTime;
    }

    public static void main(String[] args) {
        Tamagoshi alex = new Tamagoshi("Alex");
        System.out.println(alex);
        alex.parler();
        alex.consommeRessources();
        alex.mange();
        alex.mange();
        alex.mange();
        alex.consommeRessources();
        alex.consommeRessources();
        alex.consommeRessources();
        alex.consommeRessources();
    }
}
