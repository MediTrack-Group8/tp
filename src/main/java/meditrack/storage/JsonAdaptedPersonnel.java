package meditrack.storage;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.BloodGroup;
import meditrack.model.Personnel;
import meditrack.model.Status;

/**
 * Jackson-friendly version of {@link Personnel}.
 * This class serves as a Data Transfer Object (DTO) to safely serialize and
 * deserialize personnel records to and from the JSON storage file.
 */
public class JsonAdaptedPersonnel {

    public static final String MISSING_FIELD_MSG = "Personnel's %s field is missing.";

    public final String name;
    public final String status;
    public final String bloodGroup;
    public final String allergies;
    public final String lastModified;
    public final String statusExpiryDate;

    /**
     * Constructs a {@code JsonAdaptedPersonnel} with the given personnel details.
     * Jackson uses this constructor automatically when reading from the JSON file.
     *
     * @param name             The name of the personnel.
     * @param status           The medical status as a string.
     * @param bloodGroup       The blood group as a string.
     * @param allergies        The recorded allergies.
     * @param lastModified     The timestamp of the last edit.
     * @param statusExpiryDate The expiry date of the current status.
     */
    @JsonCreator
    public JsonAdaptedPersonnel(
            @JsonProperty("name")             String name,
            @JsonProperty("status")           String status,
            @JsonProperty("bloodGroup")       String bloodGroup,
            @JsonProperty("allergies")        String allergies,
            @JsonProperty("lastModified")     String lastModified,
            @JsonProperty("statusExpiryDate") String statusExpiryDate) {
        this.name             = name;
        this.status           = status;
        this.bloodGroup       = bloodGroup;
        this.allergies        = allergies;
        this.lastModified     = lastModified;
        this.statusExpiryDate = statusExpiryDate;
    }

    /**
     * Converts a given {@code Personnel} domain object into this Jackson-friendly adapted object.
     *
     * @param source The original Personnel object.
     * @return The adapted JSON object ready for writing to disk.
     */
    public static JsonAdaptedPersonnel fromModelType(Personnel source) {
        return new JsonAdaptedPersonnel(
                source.getName(),
                source.getStatus().name(),
                source.getBloodGroup() != null ? source.getBloodGroup().name() : null,
                source.getAllergies(),
                source.getLastModified() != null ? source.getLastModified().toString() : LocalDateTime.now().toString(),
                source.getStatusExpiryDate() != null ? source.getStatusExpiryDate().toString() : null
        );
    }

    /**
     * Converts this JSON DTO back into the {@code Personnel} domain object.
     * Enforces strict validation to prevent application crashes from corrupted JSON files.
     *
     * @return The validated Personnel object.
     * @throws CommandException If any required field is missing or contains invalid data.
     */
    public Personnel toModelType() throws CommandException {
        if (name == null || name.isBlank()) {
            throw new CommandException(String.format(MISSING_FIELD_MSG, "name"));
        }
        if (status == null || status.isBlank()) {
            throw new CommandException(String.format(MISSING_FIELD_MSG, "status"));
        }

        Status parsedStatus;
        try {
            parsedStatus = Status.fromString(status);
        } catch (IllegalArgumentException e) {
            throw new CommandException("Invalid status value in data file: \"" + status + "\"");
        }

        BloodGroup parsedBloodGroup = (bloodGroup != null) ? BloodGroup.fromString(bloodGroup) : null;
        String parsedAllergies = (allergies != null) ? allergies : "";

        Personnel person = new Personnel(name.trim(), parsedStatus, parsedBloodGroup, parsedAllergies);

        if (lastModified != null && !lastModified.isBlank()) {
            person.setLastModified(LocalDateTime.parse(lastModified));
        }

        if (statusExpiryDate != null && !statusExpiryDate.isBlank()) {
            person.setStatusExpiryDate(LocalDate.parse(statusExpiryDate));
        }

        return person;
    }
}