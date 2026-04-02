package meditrack.storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.Supply;

/**
 * Jackson-friendly version of {@link Supply}.
 * Serves as a Data Transfer Object (DTO) to safely serialize and deserialize
 * medical supply batches to and from the JSON storage file.
 */
public class JsonAdaptedSupply {

    public static final String MISSING_FIELD_MSG = "Supply's %s field is missing.";

    public final String name;
    public final int quantity;
    public final String expiryDate;

    /**
     * Constructs a {@code JsonAdaptedSupply} with the given details.
     * Jackson automatically calls this when parsing the JSON save file.
     *
     * @param name       The name of the supply.
     * @param quantity   The stock count.
     * @param expiryDate The expiration date string.
     */
    @JsonCreator
    public JsonAdaptedSupply(@JsonProperty("name") String name,
                             @JsonProperty("quantity") int quantity,
                             @JsonProperty("expiryDate") String expiryDate) {
        this.name = name;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    /**
     * Converts a domain {@code Supply} object into its JSON-friendly form for storage.
     *
     * @param source The original Supply object.
     * @return The adapted JSON object.
     */
    public static JsonAdaptedSupply fromModelType(Supply source) {
        return new JsonAdaptedSupply(
                source.getName(),
                source.getQuantity(),
                source.getExpiryDate().toString());
    }

    /**
     * Converts this JSON DTO back into the domain {@code Supply} object.
     * Validates strings and dates to ensure a corrupted data file does not break the application.
     *
     * @return The validated Supply object.
     * @throws CommandException If any stored field is null, blank, or contains an invalid date format.
     */
    public Supply toModelType() throws CommandException {
        if (name == null || name.isBlank()) {
            throw new CommandException(String.format(MISSING_FIELD_MSG, "name"));
        }
        if (expiryDate == null || expiryDate.isBlank()) {
            throw new CommandException(String.format(MISSING_FIELD_MSG, "expiryDate"));
        }

        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(expiryDate);
        } catch (DateTimeParseException e) {
            throw new CommandException("Invalid expiry date in data file: \"" + expiryDate + "\"");
        }

        return new Supply(name.trim(), quantity, parsedDate);
    }
}