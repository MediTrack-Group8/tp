package meditrack.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * JUnit tests for the Index utility, verifying boundary limits and conversion math.
 */
public class IndexTest {

    @Test
    public void createIndex_negativeZeroBased_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Index.fromZeroBased(-1));
    }

    @Test
    public void createIndex_zeroOneBased_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Index.fromOneBased(0));
    }

    @Test
    public void getZeroBased_fromOneBasedInput_calculatesCorrectly() {
        Index index = Index.fromOneBased(5);
        assertEquals(4, index.getZeroBased());
    }

    @Test
    public void getOneBased_fromZeroBasedInput_calculatesCorrectly() {
        Index index = Index.fromZeroBased(4);
        assertEquals(5, index.getOneBased());
    }

    @Test
    public void equals_sameIndices_returnsTrue() {
        Index i1 = Index.fromOneBased(3);
        Index i2 = Index.fromZeroBased(2); // 3 (1-based) == 2 (0-based)

        assertEquals(i1, i2);
        assertEquals(i1.hashCode(), i2.hashCode());
    }

    @Test
    public void equals_differentIndices_returnsFalse() {
        Index i1 = Index.fromOneBased(3);
        Index i2 = Index.fromOneBased(4);

        assertNotEquals(i1, i2);
    }

    @Test
    public void toString_returnsOneBasedString() {
        Index index = Index.fromZeroBased(0);
        assertEquals("1", index.toString());
    }
}