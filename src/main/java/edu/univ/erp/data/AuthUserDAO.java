package edu.univ.erp.data;

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
     * @throws SQLException If a database error occurs.
     */
    public static AuthUser findByUsername(String username) throws SQLException {
        String sql = "SELECT user_id, username, role, password_hash FROM users_auth WHERE username = ?";
        
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
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Finds an AuthUser by user ID in the Auth DB.
     * @param userId The user ID to search for.
     * @return AuthUser object if found, null otherwise.
     * @throws SQLException If a database error occurs.
     */
    public static AuthUser findById(int userId) throws SQLException {
        String sql = "SELECT user_id, username, role, password_hash FROM users_auth WHERE user_id = ?";
        
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
                    return user;
                }
            }
        }
        return null;
    }
}

