package tamagoshi.tamagoshis;

/**
 * Tamagoshi qui consomme deux fois plus d'énergie que les autres.
 */
public class GrosMangeur extends Tamagoshi {
    public GrosMangeur(String name) {
        super(name);
    }

    @Override
    public boolean consommeEnergy() {
        this.setEnergy(this.getEnergy() - 2);
        if (this.getEnergy() <= 0) {
            System.out.println(this.getName() + " : Arrrrrggh !");
            return false;
        } else {
            return true;
        }
    }
}
