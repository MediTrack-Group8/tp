package meditrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * JUnit tests for the Personnel model, verifying state management and equality rules.
 */
public class PersonnelTest {

    @Test
    public void constructor_nullName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Personnel(null, Status.FIT));
    }

    @Test
    public void constructor_blankName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Personnel("   ", Status.FIT));
    }

    @Test
    public void constructor_nullStatus_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Personnel("John", null));
    }

    @Test
    public void constructor_validInputs_createsProperlyTrimmedPersonnel() {
        Personnel person = new Personnel("  Jane Doe  ", Status.CASUALTY, BloodGroup.O_POS, " Dust ");
        assertEquals("Jane Doe", person.getName());
        assertEquals(Status.CASUALTY, person.getStatus());
        assertEquals(BloodGroup.O_POS, person.getBloodGroup());
        assertEquals("Dust", person.getAllergies());
        assertNotNull(person.getLastModified());
    }

    @Test
    public void setStatus_updatesStatusAndLastModified() throws InterruptedException {
        Personnel person = new Personnel("John", Status.FIT);
        LocalDateTime initialTime = person.getLastModified();

        // Small sleep to ensure the timestamp actually ticks forward
        Thread.sleep(10);

        person.setStatus(Status.MC);
        assertEquals(Status.MC, person.getStatus());
        assertTrue(person.getLastModified().isAfter(initialTime));
    }

    @Test
    public void isDeployable_statusFit_returnsTrue() {
        Personnel fitPerson = new Personnel("John", Status.FIT);
        assertTrue(fitPerson.isDeployable());
    }

    @Test
    public void isDeployable_statusNotFit_returnsFalse() {
        Personnel mcPerson = new Personnel("John", Status.MC);
        assertFalse(mcPerson.isDeployable());
    }

    @Test
    public void equals_sameNameDifferentCase_returnsTrue() {
        Personnel p1 = new Personnel("john doe", Status.FIT);
        Personnel p2 = new Personnel("JOHN DOE", Status.CASUALTY);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    public void equals_differentName_returnsFalse() {
        Personnel p1 = new Personnel("John", Status.FIT);
        Personnel p2 = new Personnel("Jane", Status.FIT);
        assertNotEquals(p1, p2);
    }
}