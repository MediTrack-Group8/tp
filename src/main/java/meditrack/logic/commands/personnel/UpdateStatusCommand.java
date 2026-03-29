package meditrack.logic.commands.personnel;

import meditrack.logic.commands.Command;
import meditrack.logic.commands.CommandResult;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.Model;
import meditrack.model.ModelManager;
import meditrack.model.Personnel;
import meditrack.model.Role;
import meditrack.model.Status;
import meditrack.model.Session;

import java.time.LocalDate;
import java.util.List;

/** Changes someone's status, optionally attaching an MC duration. */
public class UpdateStatusCommand extends Command {

    public static final String COMMAND_WORD = "update_status";

    public static final String MESSAGE_SUCCESS = "Updated status of %s to: %s";
    public static final String MESSAGE_SUCCESS_WITH_DURATION = "Updated status of %s to: %s (Expires: %s)";

    private final int oneBasedIndex;
    private final Status newStatus;
    private final int durationDays;

    /**
     * @param oneBasedIndex 1-based index
     * @param newStatus     new status
     */
    public UpdateStatusCommand(int oneBasedIndex, Status newStatus) {
        this(oneBasedIndex, newStatus, 0); // Default to 0 days
    }

    /**
     * @param oneBasedIndex 1-based index
     * @param newStatus     new status
     * @param durationDays  number of days the status should last
     */
    public UpdateStatusCommand(int oneBasedIndex, Status newStatus, int durationDays) {
        this.oneBasedIndex = oneBasedIndex;
        this.newStatus = newStatus;
        this.durationDays = durationDays;
    }

    /** Updates the person's status and expiry date. */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        Role currentRole = Session.getInstance().getRole();

        if (currentRole == Role.FIELD_MEDIC && newStatus != Status.CASUALTY) {
            throw new CommandException("Field Medics are only authorized to update status to CASUALTY.");
        }

        ModelManager manager = (ModelManager) model;
        Personnel person = manager.getFilteredPersonnelList(null).get(oneBasedIndex - 1);
        String name = person.getName();

        manager.setPersonnelStatus(oneBasedIndex, newStatus);

        if (durationDays > 0 && (newStatus == Status.MC || newStatus == Status.LIGHT_DUTY)) {
            LocalDate expiryDate = LocalDate.now(manager.getClock()).plusDays(durationDays);

            person.setStatusExpiryDate(expiryDate);
            return new CommandResult(String.format(MESSAGE_SUCCESS_WITH_DURATION, name, newStatus, expiryDate));
        } else {
            person.setStatusExpiryDate(null);
            return new CommandResult(String.format(MESSAGE_SUCCESS, name, newStatus));
        }
    }

    /** Medical officer and Field Medic. */
    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.MEDICAL_OFFICER, Role.FIELD_MEDIC);
    }

    /** Returns the 1-based index. */
    public int getOneBasedIndex() {
        return oneBasedIndex;
    }

    /** Returns the new status. */
    public Status getNewStatus() {
        return newStatus;
    }
}