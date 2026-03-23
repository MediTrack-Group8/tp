package meditrack.logic.commands.exceptions;

/**
 * Represents an error that occurs during execution of a command.
 * For example: A Field Medic trying to update a personnel status (unauthorized).
 */
public class CommandException extends Exception {
    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }
}