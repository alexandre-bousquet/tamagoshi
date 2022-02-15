package tamagoshi.exceptions;

public class NegativeLifeTimeException extends Exception {
    public NegativeLifeTimeException() {
    }

    public NegativeLifeTimeException(String message) {
        super(message);
    }
}
