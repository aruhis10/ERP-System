package edu.univ.erp.data;

import edu.univ.erp.domain.Instructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for instructor profiles in the ERP DB.
 * Handles read operations for instructor data.
 */
public class InstructorDAO {

    /**
     * Finds an Instructor by instructor ID in the ERP DB.
     * @param instructorId The instructor ID to search for (matches user_id from Auth DB).
     * @return Instructor object if found, null otherwise.
     * @throws SQLException If a database error occurs.
     */
    public static Instructor findById(int instructorId) throws SQLException {
        String sql = "SELECT instructor_id, department FROM instructors WHERE instructor_id = ?";
        
        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, instructorId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Instructor instructor = new Instructor();
                    instructor.setInstructorId(rs.getInt("instructor_id"));
                    instructor.setDepartment(rs.getString("department"));
                    return instructor;
                }
            }
        }
        return null;
    }
}

