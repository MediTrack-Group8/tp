package meditrack.logic.commands.personnel;

import meditrack.logic.commands.Command;
import meditrack.logic.commands.CommandResult;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.Model;
import meditrack.model.ModelManager;
import meditrack.model.Status;

/**
 * Updates the medical readiness status of a personnel member by 1-based index.
 *
 * <p>Validation rules:
 * <ul>
 *   <li>Index must be a positive integer (parser)</li>
 *   <li>Status must be a valid {@link Status} enum value (parser)</li>
 *   <li>Index must be within the current list bounds (model)</li>
 * </ul>
 */
public class UpdateStatusCommand extends Command {

    public static final String COMMAND_WORD = "update_status";

    public static final String MESSAGE_SUCCESS = "Updated status of %s to: %s";
    public static final String MESSAGE_USAGE =
            COMMAND_WORD + ": Updates the status of a personnel member.\n"
                    + "Parameters: INDEX s/STATUS\n"
                    + "Valid statuses: FIT, LIGHT_DUTIES, UNFIT\n"
                    + "Example: " + COMMAND_WORD + " 1 s/UNFIT";

    private final int oneBasedIndex;
    private final Status newStatus;

    /**
     * @param oneBasedIndex 1-based index as displayed in the UI list
     * @param newStatus     pre-validated new status
     */
    public UpdateStatusCommand(int oneBasedIndex, Status newStatus) {
        this.oneBasedIndex = oneBasedIndex;
        this.newStatus = newStatus;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        ModelManager manager = (ModelManager) model;
        // Fetch name before update for the feedback message
        String name = manager.getFilteredPersonnelList(null)
                .get(oneBasedIndex - 1).getName();
        manager.setPersonnelStatus(oneBasedIndex, newStatus);
        return new CommandResult(String.format(MESSAGE_SUCCESS, name, newStatus));
    }

    public int getOneBasedIndex() {
        return oneBasedIndex;
    }

    public Status getNewStatus() {
        return newStatus;
    }
}