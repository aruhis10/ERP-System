package edu.univ.erp.data;

import edu.univ.erp.domain.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for student profiles in the ERP DB.
 * Handles read operations for student data.
 */
public class StudentDAO {

    /**
     * Finds a Student by student ID in the ERP DB.
     * @param studentId The student ID to search for (matches user_id from Auth DB).
     * @return Student object if found, null otherwise.
     * @throws SQLException If a database error occurs.
     */
    public static Student findById(int studentId) throws SQLException {
        String sql = "SELECT student_id, roll_no, program, year FROM students WHERE student_id = ?";
        
        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, studentId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student();
                    student.setStudentId(rs.getInt("student_id"));
                    student.setRollNo(rs.getString("roll_no"));
                    student.setProgram(rs.getString("program"));
                    student.setYear(rs.getInt("year"));
                    return student;
                }
            }
        }
        return null;
    }
}

