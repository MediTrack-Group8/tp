package meditrack.logic.commands.personnel;

import meditrack.logic.commands.Command;
import meditrack.logic.commands.CommandResult;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.Model;
import meditrack.model.ModelManager;
import meditrack.model.Personnel;

/**
 * Removes a personnel member from the MediTrack roster by 1-based index.
 *
 * <p>Validation rules:
 * <ul>
 *   <li>Index must be a positive integer (parser)</li>
 *   <li>Index must be within the current list bounds (model)</li>
 * </ul>
 */
public class RemovePersonnelCommand extends Command {

    public static final String COMMAND_WORD = "remove_personnel";

    public static final String MESSAGE_SUCCESS = "Removed personnel: %s";
    public static final String MESSAGE_USAGE =
            COMMAND_WORD + ": Removes a personnel member by their list index.\n"
                    + "Parameters: INDEX (must be a positive integer)\n"
                    + "Example: " + COMMAND_WORD + " 2";

    private final int oneBasedIndex;

    /**
     * @param oneBasedIndex 1-based index as displayed in the UI list
     */
    public RemovePersonnelCommand(int oneBasedIndex) {
        this.oneBasedIndex = oneBasedIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        ModelManager manager = (ModelManager) model;
        Personnel removed = manager.deletePersonnel(oneBasedIndex);
        return new CommandResult(String.format(MESSAGE_SUCCESS, removed.getName()));
    }

    public int getOneBasedIndex() {
        return oneBasedIndex;
    }
}