package meditrack.logic.parser.exceptions;
/**
 * Represents an error that occurs during the validation of user inputs.
 * For example: Entering a negative quantity or an invalid date.
 */
public class ParseException extends Exception {
    public ParseException(String message) {
        super(message);
    }
    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}