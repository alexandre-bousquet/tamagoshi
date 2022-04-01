package tamagoshi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class User {

    /**
     * Récupère la prochaine saisie de l'utilisateur dans la console et la retourne.
     * @return La saisie de l'utilisateur
     */
    public static String saisieClavier() {
        try {
            BufferedReader clavier = new BufferedReader(new InputStreamReader(System.in));
            return clavier.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
            return null;
        }
    }
}