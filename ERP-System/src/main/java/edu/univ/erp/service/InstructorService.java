package edu.univ.erp.service;

import edu.univ.erp.access.AccessChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.DAOs.GradesDAO;
import edu.univ.erp.data.DAOs.InstructorDAO;
import edu.univ.erp.data.DAOs.SettingsDAO;
import edu.univ.erp.domain.StudentGradeEntry;
import edu.univ.erp.domain.InstructorSectionRow;
import edu.univ.erp.domain.AuthUser;
import edu.univ.erp.domain.ServiceResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * InstructorService - orchestrates instructor operations.
 * Delegates to InstructorDAO and GradesDAO.
 */
public class InstructorService {

    private final InstructorDAO instructorDAO = new InstructorDAO();
    private final GradesDAO gradesDAO = new GradesDAO();
    private final AccessChecker accessChecker = new AccessChecker();
    private final SettingsDAO settingsDAO = new SettingsDAO();   // NEW: admin lock enforcement

    public List<InstructorSectionRow> getMySections() {
        AuthUser current = SessionManager.getInstance().getCurrentUser();
        return instructorDAO.getAssignedSections(current.getUserId());
    }

    public List<StudentGradeEntry> getRosterForSection(int sectionId) {
        AuthUser current = SessionManager.getInstance().getCurrentUser();

        if (!accessChecker.isInstructorOfSection(current.getUserId(), sectionId)) {
            return List.of();
        }

        List<GradesDAO.StudentInfo> students = gradesDAO.getStudentsInSection(sectionId);
        return students.stream()
                .map(si -> new StudentGradeEntry(
                        si.enrollmentId, si.studentId, si.rollNo, si.fullName,
                        gradesDAO.getFinalForEnrollment(si.enrollmentId)))
                .collect(Collectors.toList());
    }

    public ServiceResult enterScore(int enrollmentId, String componentName, double score, double total) {
        AuthUser current = SessionManager.getInstance().getCurrentUser();

        // 1. Maintenance mode
        if (accessChecker.isMaintenanceModeOn()) {
            return new ServiceResult(false, "Maintenance mode on.");
        }

        // 2. Grade lock
        String gradeLock = settingsDAO.getSettingValue("grade_lock");
        if ("ON".equalsIgnoreCase(gradeLock)) {
            return new ServiceResult(false, "Grade updates are locked by admin.");
        }

        // 3. Instructor permission
        if (!accessChecker.isInstructorOfEnrollment(current.getUserId(), enrollmentId)) {
            return new ServiceResult(false, "Access denied.");
        }

        try {
            gradesDAO.insertOrUpdateGrade(enrollmentId, componentName, score, total);
            return new ServiceResult(true, "Saved.");
        } catch (Exception e) {
            e.printStackTrace();
            return new ServiceResult(false, e.getMessage());
        }
    }

    public ServiceResult computeFinalsForSection(int sectionId, Map<String, Double> weights) {
        AuthUser current = SessionManager.getInstance().getCurrentUser();

        // 1. Maintenance mode
        if (accessChecker.isMaintenanceModeOn()) {
            return new ServiceResult(false, "Maintenance mode on.");
        }

        // 2. Grade lock
        String gradeLock = settingsDAO.getSettingValue("grade_lock");
        if ("ON".equalsIgnoreCase(gradeLock)) {
            return new ServiceResult(false, "Grade updates are locked by admin.");
        }

        // 3. Instructor permission
        if (!accessChecker.isInstructorOfSection(current.getUserId(), sectionId)) {
            return new ServiceResult(false, "Access denied.");
        }

        try {
            gradesDAO.computeAndStoreFinalsForSection(sectionId, weights);
            return new ServiceResult(true, "Finals computed.");
        } catch (Exception e) {
            e.printStackTrace();
            return new ServiceResult(false, e.getMessage());
        }
    }
}
