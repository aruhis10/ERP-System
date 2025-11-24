package edu.univ.erp.domain;

/**
 * Holds core user data loaded into memory after successful login (UserId, Username, Role).
 */
public class SessionUser {
    private final int userId;
    private final String username;
    private final String role;

    public SessionUser(int userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    // This method resolves the 'Cannot resolve method getUserId' error
    public int getUserId() {
        return userId;
    }

    public String getUsername() { return username;}

    public String getRole() {
        return role;
    }
}