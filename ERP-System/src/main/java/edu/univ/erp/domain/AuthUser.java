package edu.univ.erp.domain;

import edu.univ.erp.util.PasswordUtil;

public class AuthUser {

    private int userId;
    private String username;
    private String role;
    private String passwordHash;
    private String status;

    private String securityQuestion;
    private String securityAnswer;

    public AuthUser() {}

    // Basic getters/setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSecurityQuestion() { return securityQuestion; }
    public void setSecurityQuestion(String securityQuestion) { this.securityQuestion = securityQuestion; }

    public String getSecurityAnswer() { return securityAnswer; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }

    // FIRST-TIME LOGIN CHECK
    public boolean isUsingTemporaryPassword() {
        return passwordHash != null &&
                PasswordUtil.checkPassword(username + "pass", passwordHash);
    }
}
