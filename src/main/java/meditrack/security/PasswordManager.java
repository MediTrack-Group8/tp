package meditrack.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * A stateless utility component dedicated to application security.
 * Handles cryptographic hashing and verification of passwords using the BCrypt algorithm.
 * This ensures that plain-text passwords are never stored or verified directly in memory.
 */
public class PasswordManager {

    // A cost factor of 12 provides an enterprise-standard balance of security and performance
    private static final int WORK_FACTOR = 12;

    /**
     * Cryptographically hashes a plain-text password using BCrypt and a generated salt.
     *
     * @param plainText The plain-text password provided by the user.
     * @return A secure BCrypt hash string containing both the salt and the hashed password.
     */
    public static String hashPassword(String plainText) {
        // Generate a salt with the specified work factor and hash the password
        String salt = BCrypt.gensalt(WORK_FACTOR);
        return BCrypt.hashpw(plainText, salt);
    }

    /**
     * Verifies a plain-text password against a previously stored BCrypt hash.
     *
     * @param plainTextPassword The plain-text password attempting to authenticate.
     * @param storedHash        The secure BCrypt hash retrieved from memory or storage.
     * @return {@code true} if the password matches the hash, {@code false} otherwise.
     */
    public static boolean checkPassword(String plainTextPassword, String storedHash) {
        if (storedHash == null || !storedHash.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(plainTextPassword, storedHash);
    }
}