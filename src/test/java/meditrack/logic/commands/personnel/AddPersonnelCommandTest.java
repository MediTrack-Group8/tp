package meditrack.logic.commands.personnel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import meditrack.logic.commands.CommandResult;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.ModelManager;
import meditrack.model.Role;
import meditrack.model.Status;

/**
 * JUnit tests for AddPersonnelCommand, ensuring data integrity and RBAC security.
 */
public class AddPersonnelCommandTest {

    private ModelManager modelManager;

    @BeforeEach
    public void setUp() {
        modelManager = new ModelManager();
    }

    @Test
    public void execute_medicalOfficerAddsFit_success() throws CommandException {
        modelManager.getSession().setRole(Role.MEDICAL_OFFICER);
        AddPersonnelCommand cmd = new AddPersonnelCommand("John Tan", Status.FIT);

        CommandResult result = cmd.execute(modelManager);

        assertEquals(1, modelManager.getPersonnelCount());
        assertEquals(String.format(AddPersonnelCommand.MESSAGE_SUCCESS, "John Tan", Status.FIT), result.getFeedbackToUser());
    }

    @Test
    public void execute_platoonCommanderAddsPending_success() throws CommandException {
        modelManager.getSession().setRole(Role.PLATOON_COMMANDER);
        AddPersonnelCommand cmd = new AddPersonnelCommand("Bob Lee", Status.PENDING);

        CommandResult result = cmd.execute(modelManager);

        assertEquals(1, modelManager.getPersonnelCount());
        assertEquals(Status.PENDING, modelManager.getFilteredPersonnelList(null).get(0).getStatus());
    }

    @Test
    public void execute_platoonCommanderAddsFit_throwsCommandException() {
        modelManager.getSession().setRole(Role.PLATOON_COMMANDER);
        AddPersonnelCommand cmd = new AddPersonnelCommand("Bob Lee", Status.FIT);

        CommandException ex = assertThrows(CommandException.class, () -> cmd.execute(modelManager));
        assertEquals("Platoon Commanders can only add personnel with PENDING status.", ex.getMessage());
    }

    @Test
    public void execute_duplicatePersonnel_throwsCommandException() throws CommandException {
        modelManager.getSession().setRole(Role.MEDICAL_OFFICER);
        modelManager.addPersonnel("John Tan", Status.FIT); // Add initially

        AddPersonnelCommand cmd = new AddPersonnelCommand("John Tan", Status.MC); // Attempt duplicate

        assertThrows(CommandException.class, () -> cmd.execute(modelManager));
    }
}