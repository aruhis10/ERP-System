package edu.univ.erp.access;

import edu.univ.erp.data.DAOs.SettingsDAO;
import edu.univ.erp.data.DAOs.InstructorDAO;

public class AccessChecker {

    private final SettingsDAO settingsDAO;
    private final InstructorDAO instructorDAO;

    public AccessChecker() {
        this.settingsDAO = new SettingsDAO();
        this.instructorDAO = new InstructorDAO();
    }

    // FIXED: correct key + correct ON/OFF logic
    public boolean isMaintenanceModeOn() {
        String val = settingsDAO.getSettingValue("maintenance_mode");
        return "ON".equalsIgnoreCase(val);
    }

    public boolean isInstructorOfSection(int instructorId, int sectionId) {
        return instructorDAO.isInstructorAssignedToSection(instructorId, sectionId);
    }

    public boolean isInstructorOfEnrollment(int instructorId, int enrollmentId) {
        return instructorDAO.isInstructorOfEnrollmentSection(instructorId, enrollmentId);
    }
}
