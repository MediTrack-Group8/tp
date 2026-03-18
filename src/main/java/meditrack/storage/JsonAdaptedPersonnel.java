package meditrack.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson-friendly version of a Personnel record.
 * Used to serialize and deserialize Personnel objects to and from JSON format.
 */
public class JsonAdaptedPersonnel {
    public final String name;
    public final String status;

    /**
     * Constructs a {@code JsonAdaptedPersonnel} with the given personnel details.
     *
     * @param name The full name of the personnel.
     * @param status The medical readiness status of the personnel as a String.
     */
    @JsonCreator
    public JsonAdaptedPersonnel(@JsonProperty("name") String name,
                                @JsonProperty("status") String status) {
        this.name = name;
        this.status = status;
    }
}