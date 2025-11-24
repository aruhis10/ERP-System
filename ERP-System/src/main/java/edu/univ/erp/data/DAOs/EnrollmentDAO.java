package edu.univ.erp.data.DAOs;

import edu.univ.erp.data.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing student enrollments.
 */
public class EnrollmentDAO {

    /**
     * Checks if a student is already registered for a given section.
     */
    public boolean isStudentAlreadyEnrolled(int studentId, int sectionId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND section_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error checking enrollment status: " + e.getMessage());
        }
        return false;
    }

    /**
     * Adds a new enrollment record for a student into a section.
     */
    public void addEnrollment(int studentId, int sectionId) throws SQLException {

        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'ACTIVE')";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Database error adding enrollment: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Drops a student from a section, deleting grades first.
     */
    public void dropEnrollment(int studentId, int sectionId) throws SQLException {

        // STEP 1 — Get enrollment_id
        String getIdSql = "SELECT enrollment_id FROM enrollments WHERE student_id = ? AND section_id = ?";
        int enrollmentId = -1;

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(getIdSql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    enrollmentId = rs.getInt(1);
                }
            }
        }

        if (enrollmentId == -1) {
            throw new SQLException("Enrollment record not found for drop.");
        }

        // STEP 2 — Delete dependent grade components
        String deleteGradesSql = "DELETE FROM grades WHERE enrollment_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteGradesSql)) {

            stmt.setInt(1, enrollmentId);
            stmt.executeUpdate();
        }

        // STEP 3 — Delete the actual enrollment record
        String deleteEnrollmentSql = "DELETE FROM enrollments WHERE enrollment_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteEnrollmentSql)) {

            stmt.setInt(1, enrollmentId);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves essential details for the student's timetable view.
     * Returns: code, title, day_time, room
     */
    public List<Object[]> getStudentTimetableRecords(int studentId) {

        String sql =
                "SELECT c.code, c.title, s.day_time, s.room " +
                        "FROM enrollments e " +
                        "JOIN sections s ON e.section_id = s.section_id " +
                        "JOIN courses c ON s.course_id = c.course_id " +
                        "WHERE e.student_id = ?";

        List<Object[]> records = new ArrayList<>();

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    records.add(new Object[]{
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getString("day_time"),
                            rs.getString("room")
                    });
                }
            }

        } catch (SQLException e) {
            System.err.println("DB Error retrieving timetable: " + e.getMessage());
        }

        return records;
    }

    /**
     * Retrieves the section_id for a specific student + courseCode.
     * This is REQUIRED for CourseRegistrationWindow to work.
     */
    public int getSectionIdForStudentAndCourse(int studentId, String courseCode) {

        String sql =
                "SELECT e.section_id " +
                        "FROM enrollments e " +
                        "JOIN sections s ON e.section_id = s.section_id " +
                        "JOIN courses c ON s.course_id = c.course_id " +
                        "WHERE e.student_id = ? AND c.code = ? " +
                        "LIMIT 1";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setString(2, courseCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("section_id");
                }
            }

        } catch (SQLException e) {
            System.err.println("DB Error fetching section ID: " + e.getMessage());
        }

        return -1;
    }
}
