package meditrack.storage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.DutySlot;
import meditrack.model.DutyType;

public class JsonAdaptedDutySlotTest {

    private static final String VALID_DATE = "2026-04-02";
    private static final String VALID_START = "08:00";
    private static final String VALID_END = "10:00";
    private static final String VALID_TYPE = "GUARD_DUTY";
    private static final String VALID_NAME = "John Doe";

    @Test
    public void toModelType_validDetails_returnsDutySlot() throws CommandException {
        JsonAdaptedDutySlot jsonSlot = new JsonAdaptedDutySlot(VALID_DATE, VALID_START, VALID_END, VALID_TYPE, VALID_NAME);
        DutySlot slot = jsonSlot.toModelType();

        assertEquals(LocalDate.parse(VALID_DATE), slot.getDate());
        assertEquals(LocalTime.parse(VALID_START), slot.getStartTime());
        assertEquals(DutyType.GUARD_DUTY, slot.getDutyType());
        assertEquals(VALID_NAME, slot.getPersonnelName());
    }

    @Test
    public void toModelType_missingDate_throwsCommandException() {
        JsonAdaptedDutySlot jsonSlot = new JsonAdaptedDutySlot(null, VALID_START, VALID_END, VALID_TYPE, VALID_NAME);
        assertThrows(CommandException.class, jsonSlot::toModelType);
    }

    @Test
    public void toModelType_invalidTime_throwsCommandException() {
        JsonAdaptedDutySlot jsonSlot = new JsonAdaptedDutySlot(VALID_DATE, "25:00", VALID_END, VALID_TYPE, VALID_NAME);
        assertThrows(CommandException.class, jsonSlot::toModelType);
    }

    @Test
    public void fromModelType_validSlot_success() {
        DutySlot slot = new DutySlot(LocalDate.of(2026, 1, 1), LocalTime.NOON, LocalTime.MIDNIGHT, DutyType.SENTRY, "Alice");
        assertDoesNotThrow(() -> JsonAdaptedDutySlot.fromModelType(slot));
    }
}