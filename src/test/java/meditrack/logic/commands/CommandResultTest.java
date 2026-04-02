package meditrack.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * JUnit tests for CommandResult equality and instantiation.
 */
public class CommandResultTest {

    @Test
    public void constructor_nullFeedback_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandResult(null));
    }

    @Test
    public void getFeedbackToUser_returnsCorrectString() {
        CommandResult result = new CommandResult("Action completed successfully.");
        assertEquals("Action completed successfully.", result.getFeedbackToUser());
    }

    @Test
    public void equals_sameFeedback_returnsTrue() {
        CommandResult r1 = new CommandResult("Same message");
        CommandResult r2 = new CommandResult("Same message");

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    public void equals_differentFeedback_returnsFalse() {
        CommandResult r1 = new CommandResult("Message A");
        CommandResult r2 = new CommandResult("Message B");

        assertNotEquals(r1, r2);
    }
}