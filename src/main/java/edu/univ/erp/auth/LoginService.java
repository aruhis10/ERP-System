package edu.univ.erp.auth;

import edu.univ.erp.data.AuthUserDAO;
import edu.univ.erp.data.StudentDAO;
import edu.univ.erp.data.InstructorDAO;
import edu.univ.erp.domain.AuthUser;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.Instructor;
import java.sql.SQLException;

/**
 * Service class that orchestrates the login flow:
 * 1. Look up user in Auth DB
 * 2. Verify password hash
 * 3. Load profile from ERP DB (if applicable)
 * 4. Establish session
 */
public class LoginService {
    
    /**
     * Attempts to authenticate a user and establish a session.
     * 
     * @param username The username to authenticate.
     * @param plaintextPassword The plaintext password to verify.
     * @return LoginResult containing success status and error message if failed.
     */
    public LoginResult login(String username, String plaintextPassword) {
        try {
            // Step 1: Look up user in Auth DB
            AuthUser user = AuthUserDAO.findByUsername(username);
            if (user == null) {
                return LoginResult.failure("Invalid username or password.");
            }
            
            // Step 2: Verify password hash
            String storedHash = user.getPasswordHash();
            if (!PasswordUtil.checkPassword(plaintextPassword, storedHash)) {
                return LoginResult.failure("Invalid username or password.");
            }
            
            // Step 3: Load profile from ERP DB based on role
            Student studentProfile = null;
            Instructor instructorProfile = null;
            
            String role = user.getRole();
            if ("Student".equals(role)) {
                studentProfile = StudentDAO.findById(user.getUserId());
            } else if ("Instructor".equals(role)) {
                instructorProfile = InstructorDAO.findById(user.getUserId());
            }
            // Admin role doesn't have a profile in ERP DB
            
            // Step 4: Establish session
            SessionManager.getInstance().establishSession(user, studentProfile, instructorProfile);
            
            return LoginResult.success("Login successful!");
            
        } catch (SQLException e) {
            return LoginResult.failure("Database error: " + e.getMessage());
        } catch (Exception e) {
            return LoginResult.failure("Unexpected error: " + e.getMessage());
        }
    }
    
    /**
     * Logs out the current user by clearing the session.
     */
    public void logout() {
        SessionManager.getInstance().clearSession();
    }
    
    /**
     * Result class for login operations.
     */
    public static class LoginResult {
        private final boolean success;
        private final String message;
        
        private LoginResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public static LoginResult success(String message) {
            return new LoginResult(true, message);
        }
        
        public static LoginResult failure(String message) {
            return new LoginResult(false, message);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
}

