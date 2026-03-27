package meditrack.model;

/**
 * Represents the type of duty assigned in a {@link DutySlot}.
 */
public enum DutyType {
    GUARD_DUTY("Guard Duty"),
    MEDICAL_COVER("Medical Cover"),
    PATROL("Patrol"),
    STANDBY("Standby"),
    SENTRY("Sentry");

    private final String displayName;

    DutyType(String displayName) {
        this.displayName = displayName;
    }

    /** Human-readable label used in the UI. */
    @Override
    public String toString() {
        return displayName;
    }
}
