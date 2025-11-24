package edu.univ.erp.data.DAOs;

import edu.univ.erp.data.DatabaseManager;
import edu.univ.erp.domain.AuthUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for authentication users in the Auth DB.
 * Handles read operations for user authentication data.
 */
public class AuthUserDAO {

    /**
     * Finds an AuthUser by username in the Auth DB.
     * @param username The username to search for.
     * @return AuthUser object if found, null otherwise.
     */
    public AuthUser findByUsername(String username) {
        String sql =
                "SELECT user_id, username, role, password_hash, status, " +
                        "       security_question, security_answer " +
                        "FROM users_auth WHERE username = ?";

        try (Connection conn = DatabaseManager.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    AuthUser user = new AuthUser();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(rs.getString("role"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setStatus(rs.getString("status"));

                    // NEW — now valid because SQL includes these columns
                    user.setSecurityQuestion(rs.getString("security_question"));
                    user.setSecurityAnswer(rs.getString("security_answer"));

                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Auth DB Error during user lookup by username: " + e.getMessage());
        }
        return null;
    }

    /**
     * Finds an AuthUser by user ID in the Auth DB.
     * @param userId The user ID to search for.
     * @return AuthUser object if found, null otherwise.
     */
    public AuthUser findById(int userId) {
        String sql =
                "SELECT user_id, username, role, password_hash, status, " +
                        "security_question, security_answer " +
                        "FROM users_auth WHERE user_id = ?";


        try (Connection conn = DatabaseManager.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    AuthUser user = new AuthUser();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(rs.getString("role"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setStatus(rs.getString("status"));

                    // NEW
                    user.setSecurityQuestion(rs.getString("security_question"));
                    user.setSecurityAnswer(rs.getString("security_answer"));

                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Auth DB Error during user lookup by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates the password hash for a user.
     */
    public boolean updatePassword(int userId, String newHash) {
        String sql = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("DB Error updating password: " + e.getMessage());
            return false;
        }
    }

}
