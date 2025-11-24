package edu.univ.erp.data.DAOs;

import edu.univ.erp.data.DatabaseManager;
import edu.univ.erp.domain.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    // Inside StudentDAO.java

    /**
     * Retrieves all grade records for a student (Stage 4.4).
     * @param studentId The user_id of the student.
     * @return A list of grade records (e.g., Course, Component, Score).
     */
    // Inside StudentDAO.java

    public List<Object[]> getStudentGradeRecords(int studentId) {
        String sql = "SELECT c.code, g.component, g.score " +
                "FROM grades g " +
                "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
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
                            rs.getString("component"),
                            rs.getDouble("score")
                    });
                }
            }
        } catch (SQLException e) {
            System.err.println("DB Error retrieving grades: " + e.getMessage());
        }
        return records;
    }
}

