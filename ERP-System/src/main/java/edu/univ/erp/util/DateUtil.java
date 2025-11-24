package edu.univ.erp.util;

/**
 * Utility class for date and time operations, including checking deadlines.
 * NOTE: The actual logic for retrieving the deadline from a settings table
 * or a specific section must be implemented later.
 */
public class DateUtil {

    // Placeholder method for checking the registration deadline
    public static boolean isPastRegistrationDeadline(int sectionId) {
        // TODO: Implement logic to check if current date is past the registration deadline for this section.
        // For now, return false so registration is always allowed.
        return false;
    }

    // Placeholder method for checking the drop deadline
    public static boolean isPastDropDeadline(int sectionId) {
        // TODO: Implement logic to check if current date is past the stated drop deadline for this section.
        // For now, return false so dropping is always allowed.
        return false;
    }
}