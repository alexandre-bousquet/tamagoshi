package tamagoshi.tamagoshis;

import java.util.Random;

/**
 * Tamagoshi qui change d'humeur change à chaque tour. L'humeur impacte la quantité de ressources consommées.
 */
public class Bipolaire extends Tamagoshi {
    private int humor;

    public Bipolaire(String name) {
        super(name);
    }

    public void generateHumor() {
        this.humor = new Random().nextInt(1) + 10;
    }

    @Override
    public boolean parler() {
        this.generateHumor();
        return super.parler();
    }

    @Override
    public boolean consommeFun() {
        if (this.getHumor() >= 2) {
            this.setFun(this.getFun() - 1);
        } else {
            this.setFun(this.getFun() - 3);
        }
        if (this.getEnergy() <= 0) {
            System.out.println(this.getName() + " : Arrrrrggh !");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean consommeEnergy() {
        if (this.getHumor() >= 2) {
            this.setEnergy(this.getEnergy() - 1);
        } else {
            this.setEnergy(this.getEnergy() - 3);
        }
        if (this.getEnergy() <= 0) {
            System.out.println(this.getName() + " : Arrrrrggh !");
            return false;
        } else {
            return true;
        }
    }

    public int getHumor() {
        return humor;
    }
}
