package meditrack.logic;

import java.io.IOException;
import java.util.List;

import meditrack.logic.commands.Command;
import meditrack.logic.commands.CommandResult;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.Model;
import meditrack.model.Role;
import meditrack.storage.Storage;

/**
 * The main logic execution engine.
 * Runs commands, strictly enforces the user's operational role permissions,
 * and automatically commits changes to the storage layer.
 */
public class LogicManager implements Logic {

    private final Model model;
    private final Storage storage;

    /**
     * Constructs a LogicManager with the required model and storage dependencies.
     *
     * @param model   The in-memory data model.
     * @param storage The storage layer handling hard drive persistence.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
    }

    /**
     * Executes the command, checks RBAC permissions, and saves to storage.
     *
     * @param command The command to execute.
     * @return The command's result message.
     * @throws CommandException If the user lacks permission, execution fails, or storage throws an IOException.
     */
    @Override
    public CommandResult executeCommand(Command command) throws CommandException {
        List<Role> allowedRoles = command.getRequiredRoles();

        // Enforce Role-Based Access Control (RBAC)
        if (allowedRoles != null && !allowedRoles.isEmpty()) {
            Role currentRole = model.getSession().getRole();

            if (currentRole == null || !allowedRoles.contains(currentRole)) {
                throw new CommandException("You do not have permission to execute this command. "
                        + "Allowed roles: " + allowedRoles);
            }
        }

        // Execute the actual command logic
        CommandResult commandResult = command.execute(model);

        // Auto-save the updated model state to the hard drive
        try {
            storage.saveMediTrackData(model.getMediTrack());
        } catch (IOException ioe) {
            throw new CommandException("Could not save data to file: " + ioe.getMessage());
        }

        return commandResult;
    }
}