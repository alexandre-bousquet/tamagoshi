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
    public void parler() {
        this.generateHumor();
        super.parler();
    }

    @Override
    public boolean consommeFun() {
        if (this.getHumor() <= 3) {
            this.setFun(this.getFun() - 2);
        }
        return super.consommeFun();
    }

    @Override
    public boolean consommeEnergy() {
        if (this.getHumor() <= 3) {
            this.setEnergy(this.getEnergy() - 2);
        }
        return super.consommeEnergy();
    }

    public int getHumor() {
        return humor;
    }
}
