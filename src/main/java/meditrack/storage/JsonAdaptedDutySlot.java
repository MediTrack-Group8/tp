package meditrack.storage;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.DutySlot;
import meditrack.model.DutyType;

/**
 * Jackson-friendly version of a {@link DutySlot} record.
 *
 * <p>JSON format:
 * <pre>
 * {
 *   "startTime": "08:00",
 *   "endTime":   "10:00",
 *   "dutyType":  "GUARD_DUTY",
 *   "personnelName": "Jack"
 * }
 * </pre>
 */
public class JsonAdaptedDutySlot {

    public static final String MISSING_FIELD_MSG = "DutySlot's %s field is missing.";

    public final String startTime;
    public final String endTime;
    public final String dutyType;
    public final String personnelName;

    /** Jackson deserialisation constructor. */
    @JsonCreator
    public JsonAdaptedDutySlot(@JsonProperty("startTime") String startTime,
                                @JsonProperty("endTime") String endTime,
                                @JsonProperty("dutyType") String dutyType,
                                @JsonProperty("personnelName") String personnelName) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.dutyType = dutyType;
        this.personnelName = personnelName;
    }

    /** Converts a domain {@link DutySlot} to its JSON-friendly form. */
    public static JsonAdaptedDutySlot fromModelType(DutySlot source) {
        return new JsonAdaptedDutySlot(
                source.getStartTime().toString(),
                source.getEndTime().toString(),
                source.getDutyType().name(),
                source.getPersonnelName());
    }

    /** Converts this DTO back to a domain {@link DutySlot}. */
    public DutySlot toModelType() throws CommandException {
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

        return new DutySlot(start, end, type, personnelName);
    }
}
