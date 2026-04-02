package meditrack.logic;

import meditrack.logic.commands.Command;
import meditrack.logic.commands.CommandResult;
import meditrack.logic.commands.exceptions.CommandException;

/**
 * API of the Logic component.
 * Defines the strict contract for the main execution engine of the application.
 * Responsible for routing commands, enforcing security protocols, and triggering data persistence.
 */
public interface Logic {

    /**
     * Executes the specified command and returns the resulting feedback.
     * Enforces Role-Based Access Control (RBAC) prior to execution and triggers an auto-save upon success.
     *
     * @param command The validated command to execute.
     * @return The result of the command execution, containing feedback for the UI to display.
     * @throws CommandException If an error occurs during execution, if the user lacks authorization,
     * or if saving the data to the hard drive fails.
     */
    CommandResult executeCommand(Command command) throws CommandException;
}