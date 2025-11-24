package edu.univ.erp.data.DAOs;

import edu.univ.erp.data.DatabaseManager;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.InstructorSectionRow;
import edu.univ.erp.domain.StudentGradeEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {

    /**
     * Load instructor profile using instructor_id
     */
    public Instructor findById(int instructorId) {

        String sql = "SELECT instructor_id, department FROM instructors WHERE instructor_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    Instructor inst = new Instructor();
                    inst.setInstructorId(rs.getInt("instructor_id"));
                    inst.setDepartment(rs.getString("department"));
                    return inst;
                }
            }

        } catch (Exception e) {
            System.err.println("DB Error retrieving instructor profile: " + e.getMessage());
        }
        return null;
    }


    /**
     * Get all sections assigned to instructor
     */
    public List<InstructorSectionRow> getAssignedSections(int instructorId) {

        List<InstructorSectionRow> list = new ArrayList<>();

        String sql = """
            
                SELECT s.section_id, s.section_name, c.code, c.title, c.credits,
                   s.day_time, s.room, s.capacity,
                   (SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id) AS enrolled_count
            FROM sections s
            JOIN courses c ON s.course_id = c.course_id
            WHERE s.instructor_id = ?
            """;

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    list.add(new InstructorSectionRow(
                            rs.getInt("section_id"),
                            rs.getString("section_name"),
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getDouble("credits"),
                            rs.getString("day_time") + " / " + rs.getString("room"),
                            rs.getInt("capacity"),
                            rs.getInt("enrolled_count")
                    ));
                }
            }

        } catch (Exception e) {
            System.err.println("DB Error retrieving instructor sections: " + e.getMessage());
        }

        return list;
    }


    /**
     * Verify instructor teaches section
     */
    public boolean isInstructorAssignedToSection(int instructorId, int sectionId) {

        String sql = "SELECT COUNT(*) FROM sections WHERE instructor_id = ? AND section_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ps.setInt(2, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            System.err.println("DB Error checking section assignment: " + e.getMessage());
        }

        return false;
    }


    /**
     * Roster + student names + final grades
     */
    public List<StudentGradeEntry> getSectionRosterWithGrades(int sectionId) {

        List<StudentGradeEntry> list = new ArrayList<>();

        String sql =
                """
                SELECT 
                    e.enrollment_id,
                    s.roll_no,
                    s.full_name,
                    (
                        SELECT g.final
                        FROM grades g
                        WHERE g.enrollment_id = e.enrollment_id
                          AND g.component = '__FINAL__'
                        LIMIT 1
                    ) AS final_grade
                FROM enrollments e
                JOIN students s ON e.student_id = s.student_id
                WHERE e.section_id = ?
                ORDER BY s.roll_no
                """;


        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Double finalVal = rs.getObject("final_grade") == null
                            ? null
                            : rs.getDouble("final_grade");

                    list.add(new StudentGradeEntry(
                            rs.getInt("enrollment_id"),
                            rs.getInt("student_id"),
                            rs.getString("roll_no"),
                            rs.getString("student_name"),
                            finalVal
                    ));
                }
            }

        } catch (Exception e) {
            System.err.println("DB Error retrieving section roster: " + e.getMessage());
        }

        return list;
    }

    public boolean isInstructorOfEnrollmentSection(
                int instructorId
                , int enrollmentId
                ) {
        String sql = """
        SELECT
                COUNT(*) 
        FROM enrollments e
        JOIN
                sections s ON e.section_id = s.section_id
        WHERE e.enrollment_id = ? AND s.instructor_id = ?
        """;

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setInt(2, instructorId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;   // true if instructor owns this enrollment's section
                }
            }
        } catch (Exception e) {
            System.err.println("DB Error checking instructor section access: " + e.getMessage());
        }

        return false;
    }
                public void saveGrade(
                int
                enrollmentId, String componentName, double score) {
        String updateSql = """
        
                UPDATE grades
        SET score = ?
        WHERE
                enrollment_id = ? AND
                component = ?
        """;

        String insertSql = """
        INSERT INTO grades (enrollment_id, component, score)
        VALUES (?, ?, ?)
        """;

        try (Connection conn = DatabaseManager.getERPConnection()) {

            // Try update first
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setDouble(1, score);
                ps.setInt(2, enrollmentId);
                ps.setString(3, componentName);

                int rows = ps.executeUpdate();
                if (rows > 0) {
                    System.out.println("Updated score for " + componentName);
                    return; // finished
                }
            }

            // If no update happened → insert new row
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, enrollmentId);
                ps.setString(2, componentName);
                ps.setDouble(3, score);
                ps.executeUpdate();
                System.out.println("Inserted new grade component " + componentName);
            }

        } catch (Exception e) {
            System.err.println("DB Error saving grade: " + e.getMessage());
        }
    }

    public void saveFinalGrade(int enrollmentId, double finalScoreValue) {

        String findSql = """
        SELECT grade_id 
        FROM grades
        WHERE enrollment_id = ? AND component = '__FINAL__'
        LIMIT 1
        """;

        String insertSql = """
        INSERT INTO grades (enrollment_id, component, final)
        VALUES (?, '__FINAL__', ?)
        """;

        String updateSql = """
        UPDATE grades
        SET final = ?
        WHERE grade_id = ?
        """;

        try (Connection conn = DatabaseManager.getERPConnection()) {
            conn.setAutoCommit(false);

            int gradeId = -1;

            // 1. Check if final already exists
            try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                ps.setInt(1, enrollmentId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        gradeId = rs.getInt("grade_id");
                    }
                }
            }

            // 2A. Insert new final record
            if (gradeId == -1) {
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setInt(1, enrollmentId);
                    ps.setDouble(2, finalScoreValue);
                    ps.executeUpdate();
                }
            }

            // 2B. Update existing final record
            else {
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setDouble(1, finalScoreValue);
                    ps.setInt(2, gradeId);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            System.out.println("InstructorDAO: Final grade saved for enrollment " + enrollmentId);

        } catch (Exception e) {
            System.err.println("DB Error saving final grade: " + e.getMessage());
        }
    }

}
