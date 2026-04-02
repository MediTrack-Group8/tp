package meditrack.commons.core;

/**
 * Represents an index value for list operations.
 * Safely handles the conversion between zero-based internal data arrays
 * and the one-based indices displayed to the user in the UI.
 */
public class Index {

    private final int zeroBasedIndex;

    /**
     * Private constructor to enforce the use of factory methods.
     *
     * @param zeroBasedIndex The internal array index.
     * @throws IllegalArgumentException If the index is negative.
     */
    private Index(int zeroBasedIndex) {
        if (zeroBasedIndex < 0) {
            throw new IllegalArgumentException("Index must be non-negative.");
        }
        this.zeroBasedIndex = zeroBasedIndex;
    }

    /**
     * Creates an Index object from a 0-based integer.
     *
     * @param zeroBasedIndex The index used by internal arrays and lists.
     * @return A safely wrapped Index object.
     */
    public static Index fromZeroBased(int zeroBasedIndex) {
        return new Index(zeroBasedIndex);
    }

    /**
     * Creates an Index object from a 1-based integer.
     *
     * @param oneBasedIndex The index inputted by the user or displayed in the UI.
     * @return A safely wrapped Index object representing the corresponding 0-based index.
     */
    public static Index fromOneBased(int oneBasedIndex) {
        return new Index(oneBasedIndex - 1);
    }

    /**
     * Retrieves the 0-based index for internal array operations.
     *
     * @return The 0-based integer.
     */
    public int getZeroBased() {
        return zeroBasedIndex;
    }

    /**
     * Retrieves the 1-based index for UI display and user feedback.
     *
     * @return The 1-based integer.
     */
    public int getOneBased() {
        return zeroBasedIndex + 1;
    }

    /**
     * Checks equality based strictly on the internal zero-based index value.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Index)) {
            return false;
        }
        return zeroBasedIndex == ((Index) other).zeroBasedIndex;
    }

    @Override
    public int hashCode() {
        return zeroBasedIndex;
    }

    /**
     * Returns the 1-based index as text for natural human reading.
     */
    @Override
    public String toString() {
        return String.valueOf(getOneBased());
    }
}