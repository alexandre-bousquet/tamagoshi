module fr.iut.tamagoshi {
    requires java.logging;
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;

    exports tamagoshi.tamagoshis;
    exports tamagoshi.jeu;
    exports tamagoshi.util;
    exports tamagoshi.exceptions;
    exports tamagoshi.graphic;
}