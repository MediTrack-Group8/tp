package meditrack.logic.commands.personnel;

import meditrack.logic.commands.Command;
import meditrack.logic.commands.CommandResult;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.Model;
import meditrack.model.ModelManager;
import meditrack.model.Status;

/**
 * Adds a new personnel member to the MediTrack roster.
 *
 * <p>Validation rules enforced (parser validates first; model enforces duplicate check):
 * <ul>
 *   <li>Name must be non-blank (parser)</li>
 *   <li>Status must be a valid {@link Status} value (parser)</li>
 *   <li>No duplicate name, case-insensitive (model)</li>
 * </ul>
 */
public class AddPersonnelCommand extends Command {

    public static final String COMMAND_WORD = "add_personnel";

    public static final String MESSAGE_SUCCESS = "Added personnel: %s [%s]";
    public static final String MESSAGE_USAGE =
            COMMAND_WORD + ": Adds a new personnel member to the roster.\n"
                    + "Parameters: n/NAME s/STATUS\n"
                    + "Example: " + COMMAND_WORD + " n/John Tan s/FIT";

    private final String name;
    private final Status status;

    /**
     * @param name   pre-validated non-blank name
     * @param status pre-validated status enum value
     */
    public AddPersonnelCommand(String name, Status status) {
        this.name = name;
        this.status = status;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        ModelManager manager = (ModelManager) model;
        manager.addPersonnel(name, status);
        return new CommandResult(String.format(MESSAGE_SUCCESS, name, status));
    }

    // Getters for testing
    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }
}