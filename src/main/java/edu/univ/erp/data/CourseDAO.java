package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for courses in the ERP DB.
 * Handles read operations for course data.
 */
public class CourseDAO {

    /**
     * Finds a Course by course code in the ERP DB.
     * @param code The course code to search for (e.g., "CS101").
     * @return Course object if found, null otherwise.
     * @throws SQLException If a database error occurs.
     */
    public static Course findByCode(String code) throws SQLException {
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
                    course.setCredits(rs.getInt("credits"));
                    return course;
                }
            }
        }
        return null;
    }

    /**
     * Finds a Course by course ID in the ERP DB.
     * @param courseId The course ID to search for.
     * @return Course object if found, null otherwise.
     * @throws SQLException If a database error occurs.
     */
    public static Course findById(int courseId) throws SQLException {
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
                    course.setCredits(rs.getInt("credits"));
                    return course;
                }
            }
        }
        return null;
    }
}

