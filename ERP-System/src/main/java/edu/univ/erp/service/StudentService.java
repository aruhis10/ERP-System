package edu.univ.erp.service;

import edu.univ.erp.access.AccessChecker;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.DAOs.CourseDAO;
import edu.univ.erp.data.DAOs.EnrollmentDAO;
import edu.univ.erp.data.DAOs.StudentDAO;
import edu.univ.erp.data.DAOs.SettingsDAO;
import edu.univ.erp.domain.AuthUser;
import edu.univ.erp.domain.CourseSectionRow;
import edu.univ.erp.domain.ServiceResult;
import edu.univ.erp.util.DateUtil;

import java.util.List;
import java.util.ArrayList;

public class StudentService {

    private final EnrollmentDAO enrollmentDAO;
    private final CourseDAO courseDAO;
    private final StudentDAO studentDAO;
    private final AccessChecker accessChecker;
    private final SettingsDAO settingsDAO = new SettingsDAO();  // NEW: admin lock enforcement

    public StudentService() {
        this.enrollmentDAO = new EnrollmentDAO();
        this.courseDAO = new CourseDAO();
        this.studentDAO = new StudentDAO();
        this.accessChecker = new AccessChecker();
    }

    /**
     * Retrieves all course sections available for the student catalog view.
     * Viewing is permitted during maintenance mode.
     */
    public List<CourseSectionRow> getCourseCatalog() {
        try {
            return courseDAO.getAllCourseSections();
        } catch (Exception e) {
            System.err.println("Error fetching course catalog: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Handles the registration of a student into a specific course section.
     * Must check maintenance mode, admin lock, duplicate, capacity, deadlines.
     */
    public ServiceResult register(int sectionId) {

        AuthUser currentUser = SessionManager.getInstance().getCurrentUser();
        int studentId = currentUser.getUserId();

        // 1. Maintenance mode
        if (accessChecker.isMaintenanceModeOn()) {
            return new ServiceResult(false, "System is in Maintenance Mode. Cannot register.");
        }

        // 2. Admin Registration Lock
        String regLock = settingsDAO.getSettingValue("registration_lock");
        if ("ON".equalsIgnoreCase(regLock)) {
            return new ServiceResult(false, "Registration is locked by admin.");
        }

        // 3. Registration deadline
        if (DateUtil.isPastRegistrationDeadline(sectionId)) {
            return new ServiceResult(false, "Registration deadline passed.");
        }

        // 4. Duplicate check
        if (enrollmentDAO.isStudentAlreadyEnrolled(studentId, sectionId)) {
            return new ServiceResult(false, "Already registered for this section.");
        }

        // 5. Capacity
        if (courseDAO.getAvailableSeats(sectionId) <= 0) {
            return new ServiceResult(false, "Section is full.");
        }

        try {
            enrollmentDAO.addEnrollment(studentId, sectionId);
            return new ServiceResult(true, "Registered successfully.");
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            return new ServiceResult(false, "Unexpected registration error.");
        }
    }

    /**
     * Handles the drop action for a student.
     */
    public ServiceResult drop(int sectionId) {

        // 1. Maintenance mode
        if (accessChecker.isMaintenanceModeOn()) {
            return new ServiceResult(false, "Maintenance Mode. Cannot drop.");
        }

        // 2. Admin Registration Lock
        String regLock = settingsDAO.getSettingValue("registration_lock");
        if ("ON".equalsIgnoreCase(regLock)) {
            return new ServiceResult(false, "Dropping courses is locked by admin.");
        }

        // 3. Drop deadline
        if (DateUtil.isPastDropDeadline(sectionId)) {
            return new ServiceResult(false, "Drop deadline passed.");
        }

        AuthUser currentUser = SessionManager.getInstance().getCurrentUser();
        int studentId = currentUser.getUserId();

        try {
            if (!enrollmentDAO.isStudentAlreadyEnrolled(studentId, sectionId)) {
                return new ServiceResult(false, "Not enrolled in this section.");
            }

            enrollmentDAO.dropEnrollment(studentId, sectionId);
            return new ServiceResult(true, "Dropped successfully.");
        } catch (Exception e) {
            System.err.println("Drop error: " + e.getMessage());
            return new ServiceResult(false, "Unexpected drop error.");
        }
    }

    /**
     * Retrieves timetable records.
     * Allowed in maintenance mode.
     */
    public List<Object[]> getTimetable(int studentId) {
        return enrollmentDAO.getStudentTimetableRecords(studentId);
    }

    /**
     * Retrieves grades.
     * Allowed in maintenance mode.
     */
    public List<Object[]> getGrades(int studentId) {
        return studentDAO.getStudentGradeRecords(studentId);
    }

    public int getCreditsForCourse(String code) {
        return courseDAO.getCreditsFor(code);
    }

    public int getSectionIdForStudentCourse(int studentId, String courseCode) {
        return enrollmentDAO.getSectionIdForStudentAndCourse(studentId, courseCode);
    }

    public String getSectionNameForSection(int sectionId) {
        return courseDAO.getSectionName(sectionId);
    }
}
