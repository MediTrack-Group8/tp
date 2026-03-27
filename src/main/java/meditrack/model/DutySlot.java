package meditrack.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents one scheduled duty slot in the duty roster.
 *
 * <p>A slot records a time window, the type of duty, and the name of the
 * personnel member assigned. All fields are immutable after construction.
 *
 * <p>Compatible with {@link meditrack.storage.JsonAdaptedDutySlot} for
 * JSON (de)serialisation.
 */
public class DutySlot {

    private static final DateTimeFormatter DISPLAY_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final LocalTime startTime;
    private final LocalTime endTime;
    private final DutyType dutyType;
    private final String personnelName;

    /**
     * Constructs a duty slot.
     *
     * @param startTime     start of the duty window — must be non-null
     * @param endTime       end of the duty window — must be non-null
     * @param dutyType      type of duty — must be non-null
     * @param personnelName name of assigned person — must be non-null and non-blank
     */
    public DutySlot(LocalTime startTime, LocalTime endTime,
                    DutyType dutyType, String personnelName) {
        Objects.requireNonNull(startTime, "startTime must not be null.");
        Objects.requireNonNull(endTime, "endTime must not be null.");
        Objects.requireNonNull(dutyType, "dutyType must not be null.");
        if (personnelName == null || personnelName.isBlank()) {
            throw new IllegalArgumentException("Personnel name must not be blank.");
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.dutyType = dutyType;
        this.personnelName = personnelName.trim();
    }

    /** Start of the duty window. */
    public LocalTime getStartTime() {
        return startTime;
    }

    /** End of the duty window. */
    public LocalTime getEndTime() {
        return endTime;
    }

    /** Type of duty assigned. */
    public DutyType getDutyType() {
        return dutyType;
    }

    /** Name of the personnel member assigned. */
    public String getPersonnelName() {
        return personnelName;
    }

    /** Formatted time range for display, e.g. {@code "08:00 - 10:00"}. */
    public String getTimeSlotDisplay() {
        return startTime.format(DISPLAY_FMT) + " - " + endTime.format(DISPLAY_FMT);
    }

    @Override
    public String toString() {
        return getTimeSlotDisplay() + " | " + dutyType + " | " + personnelName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DutySlot other)) {
            return false;
        }
        return startTime.equals(other.startTime)
                && endTime.equals(other.endTime)
                && dutyType == other.dutyType
                && personnelName.equalsIgnoreCase(other.personnelName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, dutyType, personnelName.toLowerCase());
    }
}
