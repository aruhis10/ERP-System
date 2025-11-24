package edu.univ.erp.data.DAOs;

import edu.univ.erp.data.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    // ========================================================
    // CREATE STUDENT / INSTRUCTOR IN ERP DB
    // ========================================================

    public int createStudentAndGetId(String rollNo, String fullName, String program, int year) throws SQLException {
        String sql = "INSERT INTO students (roll_no, full_name, program, year) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, rollNo);
            ps.setString(2, fullName);
            ps.setString(3, program);
            ps.setInt(4, year);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to create student");
    }

    public int createInstructorAndGetId(String department) throws SQLException {
        String sql = "INSERT INTO instructors (department) VALUES (?)";
        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, department);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to create instructor");
    }

    // ========================================================
    // PEEK NEXT STUDENT ID (for rollNo + username generation)
    // ========================================================
    public int peekNextStudentId() throws SQLException {
        String sql = "SELECT COALESCE(MAX(student_id), 0) + 1 AS nextId FROM students";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt("nextId");
        }
        return 1;
    }

    // ========================================================
    // FETCH PROFILES
    // ========================================================

    public StudentProfile fetchStudentProfile(int studentId) throws SQLException {
        String sql = "SELECT roll_no, full_name, program, year FROM students WHERE student_id = ?";
        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new StudentProfile(
                            rs.getString("roll_no"),
                            rs.getString("full_name"),
                            rs.getString("program"),
                            rs.getInt("year")
                    );
                }
            }
        }
        return null;
    }

    public InstructorProfile fetchInstructorProfile(int instructorId) throws SQLException {
        String sql = "SELECT department FROM instructors WHERE instructor_id = ?";
        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new InstructorProfile(rs.getString("department"));
                }
            }
        }
        return null;
    }

    // ========================================================
    // MARK STUDENT SOFT-DELETED
    // ========================================================

    public void markStudentDeleted(int studentId) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.executeUpdate();
        }
    }


    // ========================================================
    // AUTH DB OPERATIONS
    // ========================================================

    public void createAuthUser(int userId, String username, String role,
                               String passwordHash, String securityQ, String securityA) throws SQLException {

        String sql = """
            INSERT INTO users_auth (user_id, username, role, password_hash, status, security_question, security_answer)
            VALUES (?, ?, ?, ?, 'ACTIVE', ?, ?)
            """;

        try (Connection conn = DatabaseManager.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, username);
            ps.setString(3, role);
            ps.setString(4, passwordHash);
            ps.setString(5, securityQ);
            ps.setString(6, securityA);

            ps.executeUpdate();
        }
    }

    public void setAuthUserDeleted(int userId) throws SQLException {
        String sql = "UPDATE users_auth SET status = 'DELETED' WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public String fetchAuthUsername(int userId) throws SQLException {
        String sql = "SELECT username FROM users_auth WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("username");
            }
        }
        return null;
    }

    // ========================================================
    // ARCHIVE STUDENT (NO JSON)
    // ========================================================

    public int archiveStudentBasic(int studentId, String rollNo, String fullName,
                                   String program, int year, String adminUser) throws SQLException {

        String sql = """
            INSERT INTO admin_deleted_students
            (student_id, user_id, roll_no, full_name, program, year, deleted_by)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseManager.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, studentId);
            ps.setInt(2, studentId);
            ps.setString(3, rollNo);
            ps.setString(4, fullName);
            ps.setString(5, program);
            ps.setInt(6, year);
            ps.setString(7, adminUser);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        throw new SQLException("Failed to archive student basic row");
    }

    public void archiveStudentEnrollments(int archiveId, int studentId) throws SQLException {
        String fetchSql = "SELECT enrollment_id, section_id, status FROM enrollments WHERE student_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(fetchSql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    insertArchivedEnrollment(archiveId,
                            rs.getInt("enrollment_id"),
                            rs.getInt("section_id"),
                            rs.getString("status"));
                }
            }
        }
    }

    private void insertArchivedEnrollment(int archiveId, int eid, int sid, String status) throws SQLException {
        String sql = """
            INSERT INTO admin_deleted_student_enrollments
            (archive_id, enrollment_id, section_id, status)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseManager.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, archiveId);
            ps.setInt(2, eid);
            ps.setInt(3, sid);
            ps.setString(4, status);

            ps.executeUpdate();
        }
    }

    public void archiveStudentGrades(int archiveId, int studentId) throws SQLException {
        String fetchSql = """
            SELECT g.grade_id, g.enrollment_id, g.component, g.score, g.total, g.percentage, g.final
            FROM grades g
            JOIN enrollments e ON g.enrollment_id = e.enrollment_id
            WHERE e.student_id = ?
            """;

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(fetchSql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    insertArchivedGrade(archiveId,
                            rs.getInt("grade_id"),
                            rs.getInt("enrollment_id"),
                            rs.getString("component"),
                            rs.getBigDecimal("score"),
                            rs.getDouble("total"),
                            rs.getDouble("percentage"),
                            rs.getDouble("final"));
                }
            }
        }
    }

    private void insertArchivedGrade(int archiveId, int gradeId, int enrollmentId,
                                     String component, java.math.BigDecimal score,
                                     double total, double percent, double fin) throws SQLException {

        String sql = """
            INSERT INTO admin_deleted_student_grades
            (archive_id, grade_id, enrollment_id, component, score, total, percentage, final)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseManager.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, archiveId);
            ps.setInt(2, gradeId);
            ps.setInt(3, enrollmentId);
            ps.setString(4, component);
            ps.setBigDecimal(5, score);
            ps.setDouble(6, total);
            ps.setDouble(7, percent);
            ps.setDouble(8, fin);

            ps.executeUpdate();
        }
    }

    // ========================================================
    // ARCHIVE INSTRUCTOR
    // ========================================================

    public void archiveInstructor(int instructorId, String department, String adminUser) throws SQLException {
        String sql = """
            INSERT INTO admin_deleted_instructors
            (instructor_id, user_id, department, deleted_by)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = DatabaseManager.getAuthConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ps.setInt(2, instructorId);
            ps.setString(3, department);
            ps.setString(4, adminUser);

            ps.executeUpdate();
        }
    }

    public void setInstructorAssignmentsNull(int instructorId) throws SQLException {
        String sql = "UPDATE sections SET instructor_id = NULL WHERE instructor_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ps.executeUpdate();
        }
    }

    public void deleteInstructorRecord(int instructorId) throws SQLException {
        String sql = "DELETE FROM instructors WHERE instructor_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ps.executeUpdate();
        }
    }

    // ========================================================
    // LIST STUDENTS (FOR UI TABLE)
    // ========================================================
    public List<String[]> listStudents() {
        List<String[]> rows = new ArrayList<>();

        String sql = """
        SELECT student_id, roll_no, full_name, program, year
        FROM students
        ORDER BY student_id ASC
    """;

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("student_id");
                String roll = rs.getString("roll_no");
                String fullName = rs.getString("full_name");
                String program = rs.getString("program");
                int year = rs.getInt("year");

                String username = fullName.replaceAll("\\s+","") + roll;

                String courses = getStudentCourses(id);

                rows.add(new String[]{
                        String.valueOf(id),
                        roll,
                        fullName,
                        program,
                        String.valueOf(year),
                        username,
                        courses
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    // ========================================================
    // LIST INSTRUCTORS
    // ========================================================
    public List<String[]> listInstructors() {
        List<String[]> rows = new ArrayList<>();

        String sql = """
        SELECT instructor_id, department
        FROM instructors
        ORDER BY instructor_id ASC
    """;

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("instructor_id");
                String dept = rs.getString("department");

                String username = fetchAuthUsername(id);
                String courses = getInstructorCourses(id);

                rows.add(new String[]{
                        String.valueOf(id),
                        dept,
                        username,
                        courses
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

    // ========================================================
    // UPDATE STUDENT
    // ========================================================
    public void updateStudent(int studentId, String fullName, String program, int year) throws SQLException {
        String sql = "UPDATE students SET full_name = ?, program = ?, year = ? WHERE student_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, program);
            ps.setInt(3, year);
            ps.setInt(4, studentId);

            ps.executeUpdate();
        }
    }

    // ========================================================
    // UPDATE INSTRUCTOR
    // ========================================================
    public void updateInstructor(int instructorId, String department) throws SQLException {
        String sql = "UPDATE instructors SET department = ? WHERE instructor_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, department);
            ps.setInt(2, instructorId);
            ps.executeUpdate();
        }
    }
    // ========================================================
// FETCH COURSES FOR STUDENT
// ========================================================
    public String getStudentCourses(int studentId) throws SQLException {
        String sql = """
        SELECT c.code, s.section_name
        FROM enrollments e
        JOIN sections s ON e.section_id = s.section_id
        JOIN courses c ON s.course_id = c.course_id
        WHERE e.student_id = ?
    """;

        StringBuilder sb = new StringBuilder();

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(", ");
                    sb.append(rs.getString("code"))
                            .append(" (")
                            .append(rs.getString("section_name"))
                            .append(")");
                    first = false;
                }
            }
        }
        return sb.length() == 0 ? "None" : sb.toString();
    }

    // ========================================================
// FETCH COURSES FOR INSTRUCTOR
// ========================================================
    public String getInstructorCourses(int instructorId) throws SQLException {
        String sql = """
        SELECT c.code, s.section_name
        FROM sections s
        JOIN courses c ON s.course_id = c.course_id
        WHERE s.instructor_id = ?
    """;

        StringBuilder sb = new StringBuilder();

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(", ");
                    sb.append(rs.getString("code"))
                            .append("-")
                            .append(rs.getString("section_name"));
                    first = false;
                }
            }
        }
        return sb.length() == 0 ? "None" : sb.toString();
    }


    // ========================================================
    // DTOs
    // ========================================================
    public static class StudentProfile {
        public final String rollNo;
        public final String fullName;
        public final String program;
        public final int year;

        public StudentProfile(String rn, String fn, String pr, int yr) {
            this.rollNo = rn;
            this.fullName = fn;
            this.program = pr;
            this.year = yr;
        }
    }

    public static class InstructorProfile {
        public final String department;

        public InstructorProfile(String dept) {
            this.department = dept;
        }
    }
}
