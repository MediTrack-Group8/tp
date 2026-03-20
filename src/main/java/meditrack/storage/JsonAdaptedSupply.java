package meditrack.storage;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Jackson-friendly version of a Supply item.
 * Used to serialize and deserialize Supply objects to and from JSON format.
 */
public class JsonAdaptedSupply {
    public final String name;
    public final int quantity;
    public final String expiryDate;
    /**
     * Constructs a {@code JsonAdaptedSupply} with the given supply details.
     *
     * @param name The name of the medical supply.
     * @param quantity The current inventory quantity of the supply.
     * @param expiryDate The expiration date of the supply as a String.
     */
    @JsonCreator
    public JsonAdaptedSupply(@JsonProperty("name") String name,
                             @JsonProperty("quantity") int quantity,
                             @JsonProperty("expiryDate") String expiryDate) {
        this.name = name;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }
}