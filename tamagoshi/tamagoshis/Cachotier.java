package tamagoshi.tamagoshis;

/**
 * Tamagoshi qui n'indique pas comment il va.
 */
public class Cachotier extends Tamagoshi {
    public Cachotier(String name) {
        super(name);
    }

    @Override
    public boolean parler() {
        System.out.println(this.getName() + " : Franchement tu devrais savoir de quoi j'ai besoin ! Réfléchi un minimum !");
        if (this.getEnergy() > 4 && this.getFun() > 4) {
            return true;
        } else if (this.getEnergy() <= 4 && this.getFun() > 4) {
            return false;
        } else if (this.getEnergy() > 4 && this.getFun() <= 4) {
            return false;
        } else {
            return false;
        }
    }
}
