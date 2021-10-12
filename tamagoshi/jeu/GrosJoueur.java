package tamagoshi.jeu;

import tamagoshi.tamagoshis.Tamagoshi;

public class GrosJoueur extends Tamagoshi {
    public GrosJoueur(String name) {
        super(name);
    }

    @Override
    public boolean consommeFun() {
        this.setFun(this.getFun() - 2);
        if (this.getFun() <= 0) {
            System.out.println(this.getName() + " : Je fais une dépression, ciao !");
            return false;
        } else {
            return true;
        }
    }
}
