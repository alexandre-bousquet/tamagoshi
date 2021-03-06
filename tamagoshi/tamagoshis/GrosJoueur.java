package tamagoshi.tamagoshis;

/**
 * Tamagoshi qui consomme deux fois plus de fun que les autres.
 */
public class GrosJoueur extends Tamagoshi {
    public GrosJoueur(String name) {
        super(name);
    }

    @Override
    public boolean consommeFun() {
        this.setFun(this.getFun() - 1);
        return super.consommeFun();
    }
}
