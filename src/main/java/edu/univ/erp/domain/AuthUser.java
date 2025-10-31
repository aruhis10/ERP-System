package edu.univ.erp.domain;

// Represents the user identity and role after a successful login,
// linking the Auth DB (users_auth) record to the session.
public class AuthUser {
    private int userId;
    private String username;
    private String role; // e.g., "Admin", "Instructor", "Student"

    // This field is used internally by the LoginService/DAO during authentication
    // to temporarily hold the stored hash for comparison.
    private String passwordHash;

    // Constructor
    public AuthUser() {}

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
