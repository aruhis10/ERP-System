package edu.univ.erp.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    /**
     * Hashes a plaintext password using BCrypt for secure storage.
     * BCrypt handles per-user salting automatically.
     */
    public static String hashPassword(String plaintextPassword) {
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }

    /**
     * Validates a raw password against a BCrypt hash.
     */
    public static boolean checkPassword(String plaintextPassword, String storedHash) {
        if (storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        return BCrypt.checkpw(plaintextPassword, storedHash);
    }
}
