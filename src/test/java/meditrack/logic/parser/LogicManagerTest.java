package meditrack.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import meditrack.logic.commands.Command;
import meditrack.logic.commands.CommandResult;
import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.Model;
import meditrack.model.ModelManager;
import meditrack.model.ReadOnlyMediTrack;
import meditrack.model.Role;
import meditrack.model.Session;
import meditrack.storage.Storage;

/**
 * JUnit tests for the LogicManager, verifying RBAC enforcement and IO error handling.
 */
public class LogicManagerTest {

    private ModelManager model;
    private LogicManager logic;
    private boolean storageSaveCalled;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        storageSaveCalled = false;

        // A simple inline storage stub that just flips a boolean so we know it was called
        Storage successfulStorageStub = new Storage() {
            @Override
            public Optional<ReadOnlyMediTrack> readMediTrackData() { return Optional.empty(); }

            @Override
            public void saveMediTrackData(ReadOnlyMediTrack data) { storageSaveCalled = true; }
        };

        logic = new LogicManager(model, successfulStorageStub);
    }

    @Test
    public void executeCommand_validRole_executesAndSavesSuccessfully() throws CommandException {
        // Arrange
        Session.getInstance().setRole(Role.MEDICAL_OFFICER);
        Command dummyCommand = new DummyCommand(List.of(Role.MEDICAL_OFFICER));

        // Act
        CommandResult result = logic.executeCommand(dummyCommand);

        // Assert
        assertEquals("Success", result.getFeedbackToUser());
        assertEquals(true, storageSaveCalled, "LogicManager should trigger an auto-save upon success.");
    }

    @Test
    public void executeCommand_unauthorizedRole_throwsCommandException() {
        // Arrange
        Session.getInstance().setRole(Role.FIELD_MEDIC);
        // Command strictly requires Medical Officer
        Command restrictedCommand = new DummyCommand(List.of(Role.MEDICAL_OFFICER));

        // Act & Assert
        CommandException ex = assertThrows(CommandException.class, () -> logic.executeCommand(restrictedCommand));
        assertEquals(true, ex.getMessage().contains("You do not have permission to execute this command."));
        assertEquals(false, storageSaveCalled, "LogicManager should NOT save if authorization fails.");
    }

    @Test
    public void executeCommand_nullRole_throwsCommandException() {
        // Arrange
        Session.getInstance().clear(); // No role logged in
        Command restrictedCommand = new DummyCommand(List.of(Role.PLATOON_COMMANDER));

        // Act & Assert
        assertThrows(CommandException.class, () -> logic.executeCommand(restrictedCommand));
    }

    @Test
    public void executeCommand_storageThrowsIoException_throwsCommandException() {
        // Arrange: Create a failing storage stub that throws an IOException on purpose
        Storage failingStorageStub = new Storage() {
            @Override
            public Optional<ReadOnlyMediTrack> readMediTrackData() { return Optional.empty(); }

            @Override
            public void saveMediTrackData(ReadOnlyMediTrack data) throws IOException {
                throw new IOException("Simulated hard drive failure");
            }
        };

        LogicManager failingLogic = new LogicManager(model, failingStorageStub);
        Session.getInstance().setRole(Role.MEDICAL_OFFICER);
        Command dummyCommand = new DummyCommand(List.of(Role.MEDICAL_OFFICER));

        // Act & Assert
        CommandException ex = assertThrows(CommandException.class, () -> failingLogic.executeCommand(dummyCommand));
        assertEquals("Could not save data to file: Simulated hard drive failure", ex.getMessage());
    }

    // --- Utility Stub Class for Testing ---

    /**
     * A simple stub command used exclusively for testing the LogicManager's RBAC routing.
     */
    private static class DummyCommand extends Command {
        private final List<Role> requiredRoles;

        public DummyCommand(List<Role> requiredRoles) {
            this.requiredRoles = requiredRoles;
        }

        @Override
        public CommandResult execute(Model model) {
            return new CommandResult("Success");
        }

        @Override
        public List<Role> getRequiredRoles() {
            return requiredRoles;
        }
    }
}