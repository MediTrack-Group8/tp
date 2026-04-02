package meditrack.logic.commands.personnel;

import java.util.List;

import meditrack.logic.commands.Command;
import meditrack.logic.commands.CommandResult;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.BloodGroup;
import meditrack.model.Model;
import meditrack.model.Role;
import meditrack.model.Session;
import meditrack.model.Status;

/**
 * Adds a new personnel member to the roster.
 * Enforces Role-Based Access Control (RBAC): Platoon Commanders are restricted to
 * adding personnel with a PENDING status only.
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
    private final BloodGroup bloodGroup;
    private final String allergies;

    /**
     * Constructs a command to add a personnel member with basic details.
     *
     * @param name   The display name of the personnel.
     * @param status The initial medical readiness status.
     */
    public AddPersonnelCommand(String name, Status status) {
        this(name, status, null, "");
    }

    /**
     * Constructs a command to add a personnel member with a full medical profile.
     *
     * @param name       The display name of the personnel.
     * @param status     The initial medical readiness status.
     * @param bloodGroup The ABO+Rh blood classification.
     * @param allergies  A string detailing known allergies.
     */
    public AddPersonnelCommand(String name, Status status, BloodGroup bloodGroup, String allergies) {
        this.name       = name;
        this.status     = status;
        this.bloodGroup = bloodGroup;
        this.allergies  = (allergies == null) ? "" : allergies;
    }

    /**
     * Executes the command, adding the personnel to the model if RBAC checks pass.
     *
     * @param model The application model interface.
     * @return A CommandResult containing the success message.
     * @throws CommandException If a duplicate name exists or RBAC authorization fails.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        Role currentRole = Session.getInstance().getRole();

        // RBAC Check: Platoon Commanders can only draft pending records
        if (currentRole == Role.PLATOON_COMMANDER && status != Status.PENDING) {
            throw new CommandException("Platoon Commanders can only add personnel with PENDING status.");
        }

        model.addPersonnel(name, status, bloodGroup, allergies);
        return new CommandResult(String.format(MESSAGE_SUCCESS, name, status));
    }

    /**
     * Retrieves the list of roles authorized to execute this command.
     *
     * @return A list containing Role.MEDICAL_OFFICER and Role.PLATOON_COMMANDER.
     */
    @Override
    public List<Role> getRequiredRoles() {
        return List.of(Role.MEDICAL_OFFICER, Role.PLATOON_COMMANDER);
    }

    /** Retrieves the personnel name. */
    public String getName() {
        return name;
    }

    /** Retrieves the intended status. */
    public Status getStatus() {
        return status;
    }

    /** Retrieves the blood group, or {@code null} if not recorded. */
    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    /** Retrieves the allergies description. */
    public String getAllergies() {
        return allergies;
    }
}