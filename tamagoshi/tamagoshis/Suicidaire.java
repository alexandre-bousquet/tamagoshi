package tamagoshi.tamagoshis;

/**
 * Tamagoshi qui consomme deux fois plus de fun et d'énergie que les autres.
 */
public class Suicidaire extends Tamagoshi {
    public Suicidaire(String name) {
        super(name);
    }

    @Override
    public boolean consommeEnergy() {
        this.setEnergy(this.getEnergy() - 1);
        return super.consommeEnergy();
    }
    
    @Override
    public boolean consommeFun() {
        this.setFun(this.getFun() - 1);
        return super.consommeFun();
    }
}
