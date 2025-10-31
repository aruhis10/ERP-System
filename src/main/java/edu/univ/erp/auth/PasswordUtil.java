package edu.univ.erp.auth;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    /**
     * Hashes a plaintext password using BCrypt for secure storage.
     * BCrypt handles per-user salting automatically, fulfilling the "shadow" style requirement.
     * @param plaintextPassword The password to hash.
     * @return The salted and hashed password string (CHAR(60) in the database).
     */
    public static String hashPassword(String plaintextPassword) {
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }

    /**
     * Checks a typed password against a stored BCrypt hash retrieved from the Auth DB.
     * @param plaintextPassword The password typed by the user.
     * @param storedHash The hash retrieved from the database.
     * @return true if the passwords match, false otherwise.
     */
    public static boolean checkPassword(String plaintextPassword, String storedHash) {
        if (storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        return BCrypt.checkpw(plaintextPassword, storedHash);
    }
}
