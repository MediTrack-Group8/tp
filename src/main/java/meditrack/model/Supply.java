package meditrack.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a medical supply item or batch in the inventory.
 */
public class Supply {

    private final String name;
    private final int quantity;
    private final LocalDate expiryDate;

    /**
     * Constructs a new Supply batch.
     *
     * @param name       The name of the supply. Must not be null.
     * @param quantity   The quantity of items in this batch.
     * @param expiryDate The expiration date of this batch. Must not be null.
     */
    public Supply(String name, int quantity, LocalDate expiryDate) {
        this.name = Objects.requireNonNull(name, "Supply name must not be null.");
        this.quantity = quantity;
        this.expiryDate = Objects.requireNonNull(expiryDate, "Expiry date must not be null.");
    }

    /**
     * Retrieves the supply name.
     *
     * @return The name string.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the stock count for this specific batch.
     *
     * @return The integer quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Retrieves the expiration date of the supply.
     *
     * @return The LocalDate of expiration.
     */
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /**
     * Checks equality based solely on the supply name (case-insensitive).
     * * @param other The object to compare against.
     * @return {@code true} if both objects are Supplies with matching names.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Supply)) {
            return false;
        }
        Supply otherSupply = (Supply) other;
        return name.equalsIgnoreCase(otherSupply.name);
    }

    /**
     * Generates a hash code based on the lowercased name.
     *
     * @return The integer hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

    /**
     * Returns a string representation of the supply batch.
     *
     * @return A formatted string showing name, quantity, and expiry date.
     */
    @Override
    public String toString() {
        return String.format("Supply{name='%s', quantity=%d, expiryDate=%s}", name, quantity, expiryDate);
    }
}