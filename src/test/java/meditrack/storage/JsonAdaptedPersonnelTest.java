package meditrack.storage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.Personnel;
import meditrack.model.Status;

/**
 * JUnit tests for JsonAdaptedPersonnel to ensure safe data parsing and corrupted file handling.
 */
public class JsonAdaptedPersonnelTest {

    private static final String VALID_NAME = "Alice";
    private static final String VALID_STATUS = "FIT";
    private static final String VALID_BLOOD_GROUP = "A_POS";
    private static final String VALID_ALLERGIES = "Peanuts";
    private static final String VALID_LAST_MODIFIED = "2026-01-01T10:15:30";
    private static final String VALID_EXPIRY = "2026-05-01";

    @Test
    public void toModelType_validPersonnelDetails_returnsPersonnel() throws CommandException {
        JsonAdaptedPersonnel personnel = new JsonAdaptedPersonnel(VALID_NAME, VALID_STATUS,
                VALID_BLOOD_GROUP, VALID_ALLERGIES, VALID_LAST_MODIFIED, VALID_EXPIRY);

        Personnel modelPersonnel = personnel.toModelType();

        assertEquals(VALID_NAME, modelPersonnel.getName());
        assertEquals(Status.FIT, modelPersonnel.getStatus());
        assertEquals("Peanuts", modelPersonnel.getAllergies());
    }

    @Test
    public void toModelType_nullName_throwsCommandException() {
        JsonAdaptedPersonnel personnel = new JsonAdaptedPersonnel(null, VALID_STATUS,
                VALID_BLOOD_GROUP, VALID_ALLERGIES, VALID_LAST_MODIFIED, VALID_EXPIRY);

        assertThrows(CommandException.class, personnel::toModelType);
    }

    @Test
    public void toModelType_blankName_throwsCommandException() {
        JsonAdaptedPersonnel personnel = new JsonAdaptedPersonnel("  ", VALID_STATUS,
                VALID_BLOOD_GROUP, VALID_ALLERGIES, VALID_LAST_MODIFIED, VALID_EXPIRY);

        assertThrows(CommandException.class, personnel::toModelType);
    }

    @Test
    public void toModelType_invalidStatus_throwsCommandException() {
        JsonAdaptedPersonnel personnel = new JsonAdaptedPersonnel(VALID_NAME, "SUPER_FIT",
                VALID_BLOOD_GROUP, VALID_ALLERGIES, VALID_LAST_MODIFIED, VALID_EXPIRY);

        assertThrows(CommandException.class, personnel::toModelType);
    }

    @Test
    public void fromModelType_validPersonnel_success() {
        Personnel p = new Personnel("Bob", Status.MC);
        assertDoesNotThrow(() -> JsonAdaptedPersonnel.fromModelType(p));
    }
}