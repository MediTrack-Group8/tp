package meditrack.logic;

import meditrack.logic.commands.Command;
import meditrack.logic.commands.CommandResult;
import meditrack.logic.commands.exceptions.CommandException;

/**
 * API of the Logic component.
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     * @param command The command to execute.
     * @return The result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     */
    CommandResult executeCommand(Command command) throws CommandException;
}