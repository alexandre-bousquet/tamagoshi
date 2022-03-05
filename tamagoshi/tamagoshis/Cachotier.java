package tamagoshi.tamagoshis;

import tamagoshi.jeu.TamaGame;

/**
 * Tamagoshi qui n'indique pas comment il va.
 */
public class Cachotier extends Tamagoshi {
    public Cachotier(String name) {
        super(name);
    }

    @Override
    public String parler() {
        return TamaGame.messages.getString("secretiveTalk");
    }
}
