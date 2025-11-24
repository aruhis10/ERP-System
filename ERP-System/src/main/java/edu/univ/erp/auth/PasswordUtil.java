package edu.univ.erp.auth;

/**
 * Compatibility shim.
 * Ensures any old imports of edu.univ.erp.auth.PasswordUtil
 * do not break the application.
 *
 * All functionality is delegated to edu.univ.erp.util.PasswordUtil.
 */
public class PasswordUtil {

    public static String hashPassword(String p) {
        return edu.univ.erp.util.PasswordUtil.hashPassword(p);
    }

    public static boolean checkPassword(String p, String h) {
        return edu.univ.erp.util.PasswordUtil.checkPassword(p, h);
    }
}
