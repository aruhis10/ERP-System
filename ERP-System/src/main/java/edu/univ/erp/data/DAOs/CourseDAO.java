package edu.univ.erp.data.DAOs;

import edu.univ.erp.data.DatabaseManager;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.CourseSectionRow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for courses and sections in the ERP DB.
 */
public class CourseDAO {

    // ============================================================
    // ADMIN MODULE METHODS (NEW)
    // ============================================================

    /**
     * Insert new course — Admin Only
     */
    public void insertCourse(String code, String title, int credits) throws SQLException {
        String sql = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, title);
            ps.setInt(3, credits);
            ps.executeUpdate();
        }
    }

    /**
     * Delete course — Admin Only
     */
    public void deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
    }

    // ============================================================
    // EXISTING FUNCTIONS (KEPT AS IS)
    // ============================================================

    /**
     * Find a Course by course code.
     */
    public Course findByCode(String code) {
        String sql = "SELECT course_id, code, title, credits FROM courses WHERE code = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Course course = new Course();
                    course.setCourseId(rs.getInt("course_id"));
                    course.setCode(rs.getString("code"));
                    course.setTitle(rs.getString("title"));
                    course.setCredits(rs.getDouble("credits"));
                    return course;
                }
            }

        } catch (SQLException e) {
            System.err.println("DB error in findByCode(): " + e.getMessage());
        }

        return null;
    }

    /**
     * Find a Course by ID.
     */
    public Course findById(int courseId) {
        String sql = "SELECT course_id, code, title, credits FROM courses WHERE course_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Course course = new Course();
                    course.setCourseId(rs.getInt("course_id"));
                    course.setCode(rs.getString("code"));
                    course.setTitle(rs.getString("title"));
                    course.setCredits(rs.getDouble("credits"));
                    return course;
                }
            }

        } catch (SQLException e) {
            System.err.println("DB error in findById(): " + e.getMessage());
        }

        return null;
    }

    /**
     * Get available seats in a section.
     */
    public int getAvailableSeats(int sectionId) {

        String sql =
                "SELECT (s.capacity - (SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id)) AS available_seats " +
                        "FROM sections s WHERE s.section_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("available_seats");
                }
            }

        } catch (SQLException e) {
            System.err.println("DB error in getAvailableSeats(): " + e.getMessage());
        }

        return 0;
    }

    /**
     * COURSE CATALOG — Returns CourseSectionRow DTOs.
     */
    public List<CourseSectionRow> getAllCourseSections() {

        String sql =
                "SELECT s.section_id, s.section_name, c.code, c.title, c.credits, " +
                        "       s.day_time, s.room, s.capacity, " +
                        "       (SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id) AS enrolled_count, " +
                        "       COALESCE(u.username, 'TBD') AS instructor_name " +
                        "FROM sections s " +
                        "JOIN courses c ON s.course_id = c.course_id " +
                        "LEFT JOIN university_auth_db.users_auth u ON s.instructor_id = u.user_id ";

        List<CourseSectionRow> sections = new ArrayList<>();

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                CourseSectionRow row = new CourseSectionRow(
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getDouble("credits"),
                        rs.getString("day_time") + " / " + rs.getString("room"),
                        rs.getInt("capacity"),
                        rs.getInt("enrolled_count"),
                        rs.getString("instructor_name"),
                        rs.getInt("section_id"),
                        rs.getString("section_name")
                );

                sections.add(row);
            }

        } catch (SQLException e) {
            System.err.println("DB error in getAllCourseSections(): " + e.getMessage());
        }

        return sections;
    }

    /**
     * Get credits for a specific course code.
     */
    public int getCreditsFor(String courseCode) {

        String sql = "SELECT credits FROM courses WHERE code = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, courseCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("credits");
                }
            }

        } catch (SQLException e) {
            System.err.println("DB error in getCreditsFor(): " + e.getMessage());
        }

        return 0;
    }

    /**
     * Get section_name for a specific section_id.
     */
    public String getSectionName(int sectionId) {

        String sql = "SELECT section_name FROM sections WHERE section_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("section_name");
                }
            }

        } catch (SQLException e) {
            System.err.println("DB error in getSectionName(): " + e.getMessage());
        }

        return null;
    }
    public List<String[]> getFullCourseSectionRows() {

        String sql = """
        SELECT 
            c.course_id,
            c.code,
            c.title,
            c.credits,
            s.section_id,
            s.section_name,
            COALESCE(u.username, 'UNASSIGNED') AS instructor,
            s.day_time,
            s.room,
            s.capacity,
            (SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id) AS enrolled
        FROM courses c
        LEFT JOIN sections s ON s.course_id = c.course_id
        LEFT JOIN university_auth_db.users_auth u ON u.user_id = s.instructor_id
        ORDER BY c.course_id, s.section_id
        """;

        List<String[]> rows = new ArrayList<>();

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                rows.add(new String[]{
                        rs.getString("course_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getString("credits"),
                        rs.getString("section_id"),
                        rs.getString("section_name"),
                        rs.getString("instructor"),
                        rs.getString("day_time"),
                        rs.getString("room"),
                        rs.getString("capacity"),
                        rs.getString("enrolled")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }
    public Integer getSectionId(int courseId, String sectionName) throws SQLException {
        String sql = """
        SELECT section_id 
        FROM sections 
        WHERE course_id = ? AND section_name = ?
    """;

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.setString(2, sectionName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("section_id");
            }
        }
        return null;
    }
    public void deleteGradesForSection(int sectionId) throws SQLException {
        String sql = """
        DELETE g FROM grades g
        JOIN enrollments e ON g.enrollment_id = e.enrollment_id
        WHERE e.section_id = ?
    """;

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ps.executeUpdate();
        }
    }
    public void deleteEnrollmentsForSection(int sectionId) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE section_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ps.executeUpdate();
        }
    }
    public void deleteSection(int sectionId) throws SQLException {
        String sql = "DELETE FROM sections WHERE section_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ps.executeUpdate();
        }
    }

}
