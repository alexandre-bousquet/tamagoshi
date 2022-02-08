package tamagoshi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class User {

    public static String saisieClavier() {
    /*
    Il faut gérer les exceptions, car l'entrée standard
    peut ne pas être disponible : le constructeur de la 
    classe InputStreamReader peut renvoyer une exception.
    */
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