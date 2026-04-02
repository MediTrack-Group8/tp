package meditrack.logic.commands.personnel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import meditrack.logic.commands.CommandResult;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.ModelManager;
import meditrack.model.Personnel;
import meditrack.model.Role;
import meditrack.model.Session;
import meditrack.model.Status;

/**
 * JUnit tests for the UpdateStatusCommand, focusing on Role-Based Access Control and date calculations.
 */
public class UpdateStatusCommandTest {

    private ModelManager modelManager;

    @BeforeEach
    public void setUp() throws CommandException {
        modelManager = new ModelManager();
        // Add a dummy personnel at index 1 for testing
        modelManager.addPersonnel("Test Soldier", Status.FIT);
    }

    @Test
    public void execute_medicalOfficerAssignsMcWithDuration_success() throws CommandException {
        // Arrange: Log in as Medical Officer
        Session.getInstance().setRole(Role.MEDICAL_OFFICER);
        UpdateStatusCommand command = new UpdateStatusCommand(1, Status.MC, 3);

        // Act
        CommandResult result = command.execute(modelManager);

        // Assert
        Personnel soldier = modelManager.getFilteredPersonnelList(null).get(0);
        assertEquals(Status.MC, soldier.getStatus());

        LocalDate expectedExpiry = LocalDate.now(modelManager.getClock()).plusDays(3);
        assertEquals(expectedExpiry, soldier.getStatusExpiryDate());
        assertEquals(String.format(UpdateStatusCommand.MESSAGE_SUCCESS_WITH_DURATION, "Test Soldier", Status.MC, expectedExpiry), result.getFeedbackToUser());
    }

    @Test
    public void execute_medicalOfficerRevertsToFit_clearsExpiryDate() throws CommandException {
        // Arrange: Log in as Medical Officer, give the soldier an existing MC and expiry date
        Session.getInstance().setRole(Role.MEDICAL_OFFICER);
        Personnel soldier = modelManager.getFilteredPersonnelList(null).get(0);
        soldier.setStatus(Status.MC);
        soldier.setStatusExpiryDate(LocalDate.now().plusDays(2));

        UpdateStatusCommand command = new UpdateStatusCommand(1, Status.FIT);

        // Act
        CommandResult result = command.execute(modelManager);

        // Assert: Status should be FIT and expiry date must be null
        assertEquals(Status.FIT, soldier.getStatus());
        assertNull(soldier.getStatusExpiryDate());
        assertEquals(String.format(UpdateStatusCommand.MESSAGE_SUCCESS, "Test Soldier", Status.FIT), result.getFeedbackToUser());
    }

    @Test
    public void execute_fieldMedicAssignsCasualty_success() throws CommandException {
        // Arrange: Log in as Field Medic
        Session.getInstance().setRole(Role.FIELD_MEDIC);
        UpdateStatusCommand command = new UpdateStatusCommand(1, Status.CASUALTY);

        // Act
        CommandResult result = command.execute(modelManager);

        // Assert: Field Medic successfully applied Casualty
        Personnel soldier = modelManager.getFilteredPersonnelList(null).get(0);
        assertEquals(Status.CASUALTY, soldier.getStatus());
        assertEquals(String.format(UpdateStatusCommand.MESSAGE_SUCCESS, "Test Soldier", Status.CASUALTY), result.getFeedbackToUser());
    }

    @Test
    public void execute_fieldMedicAssignsMc_throwsCommandException() {
        // Arrange: Log in as Field Medic
        Session.getInstance().setRole(Role.FIELD_MEDIC);
        UpdateStatusCommand command = new UpdateStatusCommand(1, Status.MC);

        // Act & Assert: Should throw an exception because Field Medics cannot assign MC
        CommandException exception = assertThrows(CommandException.class, () -> {
            command.execute(modelManager);
        });

        assertEquals("Field Medics are only authorized to update status to CASUALTY.", exception.getMessage());
    }
}