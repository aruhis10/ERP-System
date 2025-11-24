package edu.univ.erp.service;

import edu.univ.erp.data.DAOs.AdminDAO;
import edu.univ.erp.data.DAOs.SettingsDAO;
import edu.univ.erp.domain.ServiceResult;
import edu.univ.erp.util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;

public class AdminService {

    private final AdminDAO adminDAO = new AdminDAO();
    private final SettingsDAO settingsDAO = new SettingsDAO();

    // ========================================================
    // CREATE STUDENT (UI → AdminService → AdminDAO)
    // ========================================================
    public ServiceResult createStudent(String fullName,
                                       String program,
                                       int year,
                                       String secQ,
                                       String secA) {

        try {
            // generate student_id BEFORE creation (by peeking)
            int nextId = adminDAO.peekNextStudentId();

            // rollNo = year + studentId
            String rollNo = year + String.valueOf(nextId);

            // username = fullname(without spaces) + rollNo
            String username = fullName.replaceAll("\\s+", "") + rollNo;

            // create ERP student row, returns the SAME ID
            int studentId = adminDAO.createStudentAndGetId(rollNo, fullName, program, year);

            // generate temp password
            String tempPass = username + "pass";
            String hash = PasswordUtil.hashPassword(tempPass);

            // create auth user with user_id == studentId
            adminDAO.createAuthUser(studentId, username, "STUDENT", hash, secQ, secA);

            return ServiceResult.success("""
                    Student created successfully.
                    Username: %s
                    Temporary Password: %s
                    """.formatted(username, tempPass));

        } catch (Exception e) {
            return ServiceResult.failure("Failed to create student: " + e.getMessage());
        }
    }

    // ========================================================
    // CREATE INSTRUCTOR
    // ========================================================
    public ServiceResult createInstructor(String fullName,
                                          String department,
                                          String secQ,
                                          String secA) {
        try {
            // username = fullName without spaces
            String username = fullName.replaceAll("\\s+", "");

            // create instructor row, returns instructorId
            int instructorId = adminDAO.createInstructorAndGetId(department);

            // temp password
            String tempPass = username + "pass";
            String hash = PasswordUtil.hashPassword(tempPass);

            // create auth user with user_id == instructorId
            adminDAO.createAuthUser(instructorId, username, "INSTRUCTOR", hash, secQ, secA);

            return ServiceResult.success("""
                    Instructor created successfully.
                    Username: %s
                    Temporary Password: %s
                    """.formatted(username, tempPass));

        } catch (Exception e) {
            return ServiceResult.failure("Failed to create instructor: " + e.getMessage());
        }
    }

    // ========================================================
    // LIST STUDENTS
    // ========================================================
    public List<String[]> listStudents() {
        return adminDAO.listStudents();
    }

    // ========================================================
    // LIST INSTRUCTORS
    // ========================================================
    public List<String[]> listInstructors() {
        return adminDAO.listInstructors();
    }

    // ========================================================
    // EDIT STUDENT
    // ========================================================
    public ServiceResult editStudent(int studentId,
                                     String fullName,
                                     String program,
                                     int year) {
        try {
            adminDAO.updateStudent(studentId, fullName, program, year);
            return ServiceResult.success("Student updated.");
        } catch (SQLException e) {
            return ServiceResult.failure("Failed to update student: " + e.getMessage());
        }
    }

    // ========================================================
    // EDIT INSTRUCTOR
    // ========================================================
    public ServiceResult editInstructor(int instructorId,
                                        String department) {
        try {
            adminDAO.updateInstructor(instructorId, department);
            return ServiceResult.success("Instructor updated.");
        } catch (SQLException e) {
            return ServiceResult.failure("Failed to update instructor: " + e.getMessage());
        }
    }

    // ========================================================
    // DELETE STUDENT (soft-delete)
    // ========================================================
    public ServiceResult deleteStudent(int studentId, String adminUser) {
        try {
            AdminDAO.StudentProfile p = adminDAO.fetchStudentProfile(studentId);
            if (p == null)
                return ServiceResult.failure("Student not found.");

            // archive the base row
            int archiveId = adminDAO.archiveStudentBasic(
                    studentId, p.rollNo, p.fullName, p.program, p.year, adminUser
            );

            // archive enrollment + grades
            adminDAO.archiveStudentEnrollments(archiveId, studentId);
            adminDAO.archiveStudentGrades(archiveId, studentId);

            // mark ERP student deleted
            adminDAO.markStudentDeleted(studentId);

            // disable login
            adminDAO.setAuthUserDeleted(studentId);

            return ServiceResult.success("Student archived and deleted.");

        } catch (Exception e) {
            return ServiceResult.failure("Delete failed: " + e.getMessage());
        }
    }

    // ========================================================
    // DELETE INSTRUCTOR (hard delete)
    // ========================================================
    public ServiceResult deleteInstructor(int instructorId, String adminUser) {
        try {
            AdminDAO.InstructorProfile p = adminDAO.fetchInstructorProfile(instructorId);
            if (p == null)
                return ServiceResult.failure("Instructor not found.");

            // archive instructor
            adminDAO.archiveInstructor(instructorId, p.department, adminUser);

            // unassign sections
            adminDAO.setInstructorAssignmentsNull(instructorId);

            // remove ERP instructor row
            adminDAO.deleteInstructorRecord(instructorId);

            // disable login
            adminDAO.setAuthUserDeleted(instructorId);

            return ServiceResult.success("Instructor archived and deleted.");

        } catch (Exception e) {
            return ServiceResult.failure("Instructor delete failed: " + e.getMessage());
        }
    }

    // ========================================================
    // SETTINGS / LOCKS
    // ========================================================
    public ServiceResult toggleRegistrationLock() {
        try {
            String current = settingsDAO.getSettingValue("registration_lock");
            boolean turnOn = !"ON".equalsIgnoreCase(current);

            settingsDAO.setSettingValue("registration_lock", turnOn ? "ON" : "OFF");

            return ServiceResult.success("Registration lock is now: " + (turnOn ? "ON" : "OFF"));
        } catch (Exception e) {
            return ServiceResult.failure("Error toggling registration lock: " + e.getMessage());
        }
    }


    public ServiceResult toggleGradeLock() {
        try {
            String current = settingsDAO.getSettingValue("grade_lock");
            boolean turnOn = !"ON".equalsIgnoreCase(current);

            settingsDAO.setSettingValue("grade_lock", turnOn ? "ON" : "OFF");

            return ServiceResult.success("Grade lock is now: " + (turnOn ? "ON" : "OFF"));
        } catch (Exception e) {
            return ServiceResult.failure("Error toggling grade lock: " + e.getMessage());
        }
    }


    public ServiceResult setMaintenanceMode(boolean on) {
        try {
            settingsDAO.setSettingValue("maintenance_mode", on ? "ON" : "OFF");
            return ServiceResult.success("Maintenance Mode = " + (on ? "ON" : "OFF"));
        } catch (Exception e) {
            return ServiceResult.failure("Failed to update maintenance mode: " + e.getMessage());
        }
    }
}
