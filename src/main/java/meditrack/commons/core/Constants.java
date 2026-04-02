package meditrack.commons.core;

/**
 * Contains application-wide constants to prevent the use of magic numbers.
 * Centralizing these values ensures consistency and makes future configuration changes easy.
 */
public class Constants {

    /** The threshold in days before a supply is flagged as expiring soon. */
    public static final int EXPIRY_THRESHOLD_DAYS = 30;

    /** The quantity threshold below which a supply is flagged as low stock. */
    public static final int LOW_STOCK_THRESHOLD_QUANTITY = 50;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Constants() {}
}