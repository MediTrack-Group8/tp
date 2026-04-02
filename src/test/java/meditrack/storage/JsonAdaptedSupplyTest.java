package meditrack.storage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import meditrack.logic.commands.exceptions.CommandException;
import meditrack.model.Supply;

/**
 * JUnit tests for JsonAdaptedSupply to ensure strict JSON date and string validation.
 */
public class JsonAdaptedSupplyTest {

    private static final String VALID_NAME = "Bandages";
    private static final int VALID_QUANTITY = 50;
    private static final String VALID_EXPIRY = "2027-12-31";

    @Test
    public void toModelType_validSupplyDetails_returnsSupply() throws CommandException {
        JsonAdaptedSupply supplyJson = new JsonAdaptedSupply(VALID_NAME, VALID_QUANTITY, VALID_EXPIRY);
        Supply supply = supplyJson.toModelType();

        assertEquals(VALID_NAME, supply.getName());
        assertEquals(VALID_QUANTITY, supply.getQuantity());
        assertEquals(LocalDate.parse(VALID_EXPIRY), supply.getExpiryDate());
    }

    @Test
    public void toModelType_nullName_throwsCommandException() {
        JsonAdaptedSupply supplyJson = new JsonAdaptedSupply(null, VALID_QUANTITY, VALID_EXPIRY);
        assertThrows(CommandException.class, supplyJson::toModelType);
    }

    @Test
    public void toModelType_invalidDatePattern_throwsCommandException() {
        JsonAdaptedSupply supplyJson = new JsonAdaptedSupply(VALID_NAME, VALID_QUANTITY, "31-12-2027");
        assertThrows(CommandException.class, supplyJson::toModelType);
    }

    @Test
    public void fromModelType_validSupply_success() {
        Supply s = new Supply("Aspirin", 100, LocalDate.of(2028, 1, 1));
        assertDoesNotThrow(() -> JsonAdaptedSupply.fromModelType(s));
    }
}