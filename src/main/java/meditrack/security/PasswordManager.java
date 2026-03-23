package meditrack.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * A stateless utility component dedicated to application security.
 * Handles hashing and verification of the master application password.
 */
public class PasswordManager {

    // A cost factor of 12 provides a strong balance of security and performance
    private static final int WORK_FACTOR = 12;

    /**
     * Takes a plain-text password and returns a BCrypt hash string suitable for storage.
     * * @param plainText The plain-text password entered by the user.
     * @return A BCrypt hash string.
     */
    public static String hashPassword(String plainText) {
        // Generate a salt with the specified work factor and hash the password
        String salt = BCrypt.gensalt(WORK_FACTOR);
        return BCrypt.hashpw(plainText, salt);
    }

    /**
     * Compares the plain text password entered by the user against the stored BCrypt hash.
     * * @param plainTextPassword The password entered in the UI.
     * @param storedHash The BCrypt hash retrieved from Storage.
     * @return true if the password matches the hash, false otherwise.
     */
    public static boolean checkPassword(String plainTextPassword, String storedHash) {
        if (storedHash == null || !storedHash.startsWith("$2a$")) {
            return false; // Invalid hash format
        }
        return BCrypt.checkpw(plainTextPassword, storedHash);
    }
}