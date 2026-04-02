package meditrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * JUnit tests for the DutyType enum, ensuring proper display formatting and value resolution.
 */
public class DutyTypeTest {

    @Test
    public void toString_formatsCorrectlyForUI() {
        assertEquals("Guard Duty", DutyType.GUARD_DUTY.toString());
        assertEquals("Medical Cover", DutyType.MEDICAL_COVER.toString());
        assertEquals("Patrol", DutyType.PATROL.toString());
        assertEquals("Standby", DutyType.STANDBY.toString());
        assertEquals("Sentry", DutyType.SENTRY.toString());
    }

    @Test
    public void valueOf_validString_resolvesToEnum() {
        // This test ensures that Jackson JSON parsing (which relies on valueOf)
        // won't break if someone accidentally renames an enum key.
        assertNotNull(DutyType.valueOf("GUARD_DUTY"));
        assertNotNull(DutyType.valueOf("MEDICAL_COVER"));
        assertEquals(DutyType.PATROL, DutyType.valueOf("PATROL"));
    }
}