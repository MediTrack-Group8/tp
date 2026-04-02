package meditrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * JUnit tests for the Supply model.
 */
public class SupplyTest {

    @Test
    public void constructor_nullName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new Supply(null, 10, LocalDate.of(2027, 1, 1)));
    }

    @Test
    public void constructor_nullExpiryDate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new Supply("Bandages", 10, null));
    }

    @Test
    public void getters_returnCorrectValues() {
        LocalDate expiry = LocalDate.of(2028, 5, 15);
        Supply supply = new Supply("IV Drip", 25, expiry);

        assertEquals("IV Drip", supply.getName());
        assertEquals(25, supply.getQuantity());
        assertEquals(expiry, supply.getExpiryDate());
    }

    @Test
    public void equals_sameNameDifferentCase_returnsTrue() {
        // Because equality is strictly tied to name, identical names are treated as equal
        // regardless of differing quantities or dates.
        Supply s1 = new Supply("bandages", 10, LocalDate.of(2027, 1, 1));
        Supply s2 = new Supply("BANDAGES", 50, LocalDate.of(2028, 1, 1));

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    public void equals_differentName_returnsFalse() {
        Supply s1 = new Supply("Bandages", 10, LocalDate.of(2027, 1, 1));
        Supply s2 = new Supply("Morphine", 10, LocalDate.of(2027, 1, 1));

        assertNotEquals(s1, s2);
    }
}