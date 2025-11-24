package edu.univ.erp.auth;

import edu.univ.erp.data.DAOs.AuthUserDAO;
import edu.univ.erp.domain.AuthUser;
import edu.univ.erp.domain.ServiceResult;
import edu.univ.erp.util.PasswordUtil;

public class AuthService {

    private final AuthUserDAO authUserDAO = new AuthUserDAO();

    /**
     * Handles security-question-based password reset
     */
    public ServiceResult resetPassword(String username, String answer, String newPassword) {

        AuthUser user = authUserDAO.findByUsername(username);

        if (user == null)
            return ServiceResult.failure("User not found.");

        // Security answer check (case-insensitive)
        if (user.getSecurityAnswer() == null ||
                !user.getSecurityAnswer().equalsIgnoreCase(answer)) {
            return ServiceResult.failure("Incorrect security answer.");
        }

        // Hash new password
        String newHash = PasswordUtil.hashPassword(newPassword);

        boolean ok = authUserDAO.updatePassword(user.getUserId(), newHash);

        if (!ok) return ServiceResult.failure("Failed to update password.");

        return ServiceResult.success("Password updated successfully.");
    }


    public ServiceResult setNewPasswordDirect(String username, String newPassword) {
        AuthUser user = authUserDAO.findByUsername(username);

        if (user == null) {
            return ServiceResult.failure("User not found.");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);

        boolean ok = authUserDAO.updatePassword(user.getUserId(), newHash);

        if (!ok) return ServiceResult.failure("Password update failed.");

        return ServiceResult.success("Password updated successfully. Please login again.");
    }


}
