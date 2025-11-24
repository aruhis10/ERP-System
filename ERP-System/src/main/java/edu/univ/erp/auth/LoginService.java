package edu.univ.erp.auth;

import edu.univ.erp.data.DAOs.AuthUserDAO;
import edu.univ.erp.data.DAOs.StudentDAO;
import edu.univ.erp.data.DAOs.InstructorDAO;
import edu.univ.erp.data.DAOs.SettingsDAO;
import edu.univ.erp.domain.AuthUser;
import edu.univ.erp.domain.Student;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.util.PasswordUtil;

/**
 * Service class that orchestrates the login flow:
 * 1. Look up user in Auth DB
 * 2. Verify password hash
 * 3. Enforce maintenance mode (admins bypass)
 * 4. Enforce first-login password reset
 * 5. Load profile from ERP DB
 * 6. Establish session
 */
public class LoginService {

    private final AuthUserDAO authUserDAO = new AuthUserDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final InstructorDAO instructorDAO = new InstructorDAO();
    private final SettingsDAO settingsDAO = new SettingsDAO();

    public LoginResult login(String username, String plaintextPassword) {
        try {
            // Step 1: lookup user
            AuthUser user = authUserDAO.findByUsername(username);

            if (user == null) {
                return LoginResult.failure("Incorrect username or password.");
            }

            // === MAINTENANCE MODE ENFORCEMENT ===
            String maintenance = settingsDAO.getSettingValue("maintenance_mode");

            // === STATUS (ALL CAPS) ENFORCEMENT ===
            if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
                return LoginResult.failure("Account inactive or deleted.");
            }

            if (!PasswordUtil.checkPassword(plaintextPassword, user.getPasswordHash())) {
                return LoginResult.failure("Incorrect username or password.");
            }



            // ============================================================
            // === FIRST-TIME LOGIN CHECK (TEMP PASSWORD ENFORCEMENT)  ===
            // ============================================================
            String tempPassword = username + "pass";
            if (PasswordUtil.checkPassword(tempPassword, user.getPasswordHash())) {
                // tell UI to open ChangePasswordWindow(username)
                return LoginResult.failure("FIRST_LOGIN");
            }


            // Step 3: load profile
            Student studentProfile = null;
            Instructor instructorProfile = null;

            String role = user.getRole().toUpperCase();

            if ("STUDENT".equals(role)) {
                studentProfile = studentDAO.findById(user.getUserId());
            } else if ("INSTRUCTOR".equals(role)) {
                instructorProfile = instructorDAO.findById(user.getUserId());
            }

            // Step 4: establish session
            SessionManager.getInstance().establishSession(user, studentProfile, instructorProfile);

            return LoginResult.success("Login successful!");

        } catch (Exception e) {
            System.err.println("Login unexpected error: " + e.getMessage());
            return LoginResult.failure("An unexpected application error occurred. Please try again.");
        }
    }

    public void logout() {
        SessionManager.getInstance().clearSession();
    }

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

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}
