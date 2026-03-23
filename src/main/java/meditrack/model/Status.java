package meditrack.model;

/**
 * Represents the medical readiness status of a Personnel member.
 *
 * <p>Valid values match the team's data schema used in
 * {@link meditrack.storage.JsonAdaptedPersonnel}.
 */
public enum Status {
    FIT,
    LIGHT_DUTIES,
    UNFIT;

    /**
     * Returns true if this status qualifies the person for full deployment.
     * Only {@code FIT} personnel appear in the duty roster.
     */
    public boolean isDeployable() {
        return this == FIT;
    }

    /**
     * Parses a string (case-insensitive, underscores or spaces) to a Status value.
     *
     * @param value raw string from user input or JSON storage
     * @return the matching Status
     * @throws IllegalArgumentException if {@code value} does not match any Status
     */
    public static Status fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Status value must not be null.");
        }
        switch (value.trim().toUpperCase().replace(' ', '_')) {
            case "FIT":
                return FIT;
            case "LIGHT_DUTIES":
                return LIGHT_DUTIES;
            case "UNFIT":
                return UNFIT;
            default:
                throw new IllegalArgumentException(
                        "Invalid status: \"" + value + "\". Valid values: FIT, LIGHT_DUTIES, UNFIT");
        }
    }

    @Override
    public String toString() {
        return name().replace('_', ' ');
    }
}