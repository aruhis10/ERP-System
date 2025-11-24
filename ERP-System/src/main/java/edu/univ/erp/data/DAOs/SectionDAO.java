package edu.univ.erp.data.DAOs;

import edu.univ.erp.data.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SectionDAO {

    // Insert a new section
    public void insertSection(int courseId, String name, String dayTime,
                              String room, int capacity, String semester, int year) throws SQLException {

        String sql = "INSERT INTO sections (section_name, course_id, instructor_id, day_time, room, capacity, semester, year) " +
                "VALUES (?, ?, NULL, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, courseId);
            ps.setString(3, dayTime);
            ps.setString(4, room);
            ps.setInt(5, capacity);
            ps.setString(6, semester);
            ps.setInt(7, year);

            ps.executeUpdate();
        }
    }

    // Assign instructor
    public void assignInstructor(int sectionId, int instructorId) throws SQLException {
        String sql = "UPDATE sections SET instructor_id = ? WHERE section_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ps.setInt(2, sectionId);
            ps.executeUpdate();
        }
    }

    // Unassign instructor
    public void unassignInstructor(int sectionId) throws SQLException {
        String sql = "UPDATE sections SET instructor_id = NULL WHERE section_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ps.executeUpdate();
        }
    }

    // Delete all sections for a course
    public void deleteSectionsByCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM sections WHERE course_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
    }
}
