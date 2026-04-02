package meditrack.storage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.DutySlot;
import meditrack.model.DutyType;

/**
 * Jackson-friendly version of {@link DutySlot}.
 * Serves as a Data Transfer Object (DTO) to safely serialize and deserialize
 * duty assignments to and from the JSON storage file.
 */
public class JsonAdaptedDutySlot {

    public static final String MISSING_FIELD_MSG = "DutySlot's %s field is missing.";

    public final String date;
    public final String startTime;
    public final String endTime;
    public final String dutyType;
    public final String personnelName;

    /**
     * Constructs a {@code JsonAdaptedDutySlot} with the given details.
     *
     * @param date          The calendar date of the duty.
     * @param startTime     The start time of the shift.
     * @param endTime       The end time of the shift.
     * @param dutyType      The specific type of duty.
     * @param personnelName The name of the assigned personnel.
     */
    @JsonCreator
    public JsonAdaptedDutySlot(@JsonProperty("date") String date,
                               @JsonProperty("startTime") String startTime,
                               @JsonProperty("endTime") String endTime,
                               @JsonProperty("dutyType") String dutyType,
                               @JsonProperty("personnelName") String personnelName) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dutyType = dutyType;
        this.personnelName = personnelName;
    }

    /**
     * Converts a domain {@code DutySlot} object into its JSON-friendly form.
     *
     * @param source The original DutySlot object.
     * @return The adapted JSON object.
     */
    public static JsonAdaptedDutySlot fromModelType(DutySlot source) {
        return new JsonAdaptedDutySlot(
                source.getDate().toString(),
                source.getStartTime().toString(),
                source.getEndTime().toString(),
                source.getDutyType().name(),
                source.getPersonnelName());
    }

    /**
     * Converts this JSON DTO back into the domain {@code DutySlot} object.
     *
     * @return The validated DutySlot object.
     * @throws CommandException If any stored field is missing or contains invalid formatting.
     */
    public DutySlot toModelType() throws CommandException {
        if (date == null || date.isBlank()) {
            throw new CommandException(String.format(MISSING_FIELD_MSG, "date"));
        }
        if (startTime == null || startTime.isBlank()) {
            throw new CommandException(String.format(MISSING_FIELD_MSG, "startTime"));
        }
        if (endTime == null || endTime.isBlank()) {
            throw new CommandException(String.format(MISSING_FIELD_MSG, "endTime"));
        }
        if (dutyType == null || dutyType.isBlank()) {
            throw new CommandException(String.format(MISSING_FIELD_MSG, "dutyType"));
        }
        if (personnelName == null || personnelName.isBlank()) {
            throw new CommandException(String.format(MISSING_FIELD_MSG, "personnelName"));
        }

        LocalDate slotDate;
        try {
            slotDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new CommandException("Invalid date format in duty slot: expected YYYY-MM-DD.");
        }

        LocalTime start;
        LocalTime end;
        try {
            start = LocalTime.parse(startTime);
            end = LocalTime.parse(endTime);
        } catch (DateTimeParseException e) {
            throw new CommandException("Invalid time format in duty slot: expected HH:mm.");
        }

        DutyType type;
        try {
            type = DutyType.valueOf(dutyType);
        } catch (IllegalArgumentException e) {
            throw new CommandException("Invalid duty type in data file: \"" + dutyType + "\"");
        }

        return new DutySlot(slotDate, start, end, type, personnelName);
    }
}