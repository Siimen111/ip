package revel;

/**
 * Represents user-facing errors in the Revel application.
 */
public class RevelException extends Exception {
    public RevelException(String message) {
        super(message);
    }
}
