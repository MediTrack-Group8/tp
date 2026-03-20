package meditrack.model;

import java.util.Objects;

/**
 * Represents a Personnel record in the MediTrack roster.
 *
 * <p>Equality is determined by name (case-insensitive) so that the duplicate-check
 * in {@link ModelManager} works correctly across add operations.
 *
 * <p>Compatible with {@link meditrack.storage.JsonAdaptedPersonnel} for JSON (de)serialisation.
 */
public class Personnel {

    private final String name;
    private Status status;

    /**
     * Constructs a Personnel record.
     *
     * @param name   display name — must be non-null and non-blank
     * @param status initial medical readiness status — must be non-null
     */
    public Personnel(String name, Status status) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Personnel name must not be blank.");
        }
        Objects.requireNonNull(status, "Status must not be null.");
        this.name = name.trim();
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    /** Updates the medical readiness status of this personnel member. */
    public void setStatus(Status status) {
        Objects.requireNonNull(status, "Status must not be null.");
        this.status = status;
    }

    /** Returns true if this personnel member is deployable (i.e. status is FIT). */
    public boolean isDeployable() {
        return status == Status.FIT;
    }

    /**
     * Two Personnel objects are equal if their names match (case-insensitive).
     * Used for duplicate detection during {@code addPersonnel}.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Personnel)) {
            return false;
        }
        Personnel other = (Personnel) obj;
        return this.name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return name + " [" + status + "]";
    }
}