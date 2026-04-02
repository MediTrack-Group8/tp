package meditrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

/**
 * JUnit tests for DutySlot, ensuring midnight crossing logic and equality rules hold true.
 */
public class DutySlotTest {

    private final LocalDate testDate = LocalDate.of(2026, 4, 2);

    @Test
    public void constructor_nullName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new DutySlot(testDate, LocalTime.NOON, LocalTime.MIDNIGHT, DutyType.GUARD_DUTY, null));
    }

    @Test
    public void crossesMidnight_standardShift_returnsFalse() {
        DutySlot slot = new DutySlot(testDate, LocalTime.of(8, 0), LocalTime.of(12, 0), DutyType.PATROL, "Alice");
        assertFalse(slot.crossesMidnight());
        assertEquals("08:00 - 12:00", slot.getTimeSlotDisplay());
    }

    @Test
    public void crossesMidnight_overnightShift_returnsTrue() {
        DutySlot slot = new DutySlot(testDate, LocalTime.of(22, 0), LocalTime.of(2, 0), DutyType.SENTRY, "Bob");
        assertTrue(slot.crossesMidnight());
        assertEquals("22:00 - 02:00 (+1)", slot.getTimeSlotDisplay());
    }

    @Test
    public void equals_identicalFieldsDifferentCaseName_returnsTrue() {
        DutySlot s1 = new DutySlot(testDate, LocalTime.of(8, 0), LocalTime.of(10, 0), DutyType.STANDBY, "john doe");
        DutySlot s2 = new DutySlot(testDate, LocalTime.of(8, 0), LocalTime.of(10, 0), DutyType.STANDBY, "JOHN DOE");

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    public void equals_differentTime_returnsFalse() {
        DutySlot s1 = new DutySlot(testDate, LocalTime.of(8, 0), LocalTime.of(10, 0), DutyType.STANDBY, "Alice");
        DutySlot s2 = new DutySlot(testDate, LocalTime.of(9, 0), LocalTime.of(11, 0), DutyType.STANDBY, "Alice");

        assertNotEquals(s1, s2);
    }
}