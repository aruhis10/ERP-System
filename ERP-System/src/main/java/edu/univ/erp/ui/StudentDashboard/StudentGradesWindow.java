package edu.univ.erp.ui.StudentDashboard;

import edu.univ.erp.data.DAOs.GradesDAO;
import edu.univ.erp.data.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * NEW StudentGradesWindow
 * - Accepts studentId
 * - Fetches ALL enrollments for the student
 * - Shows grades for ALL sections/courses
 * - Displays score/total (percentage) exactly like Instructor Gradebook
 */
public class StudentGradesWindow extends JFrame {

    private final int studentId;       // We now receive studentId
    private final GradesDAO dao = new GradesDAO();

    private JTable table;
    private DefaultTableModel model;

    public StudentGradesWindow(int studentId) {
        this.studentId = studentId;

        setTitle("My Grades");
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel();
        table = new JTable(model);
        table.setRowHeight(26);

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        loadAllGradesForStudent();

        setVisible(true);
    }

    /**
     * Loads ALL grades for ALL enrollments for this student.
     * A student can have 1 section per course (your rule).
     */
    private void loadAllGradesForStudent() {
        model.setRowCount(0);
        model.setColumnCount(0);

        // --- Build fixed columns ---
        model.addColumn("Course");
        model.addColumn("Component");
        model.addColumn("Score / Total (Percentage)");

        // --- Get all enrollment + course pairs for this student ---
        Map<Integer, String> enrollMap = getEnrollmentsForStudent(studentId);

        if (enrollMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You are not enrolled in any courses.");
            return;
        }

        // --- For each enrollment load grades ---
        for (var entry : enrollMap.entrySet()) {

            int enrollmentId = entry.getKey();
            String courseLabel = entry.getValue();

            int sectionId = findSectionForEnrollment(enrollmentId);
            if (sectionId == -1) continue;

            LinkedHashSet<String> comps = dao.getComponentsForSection(sectionId);
            LinkedHashMap<String, LinkedHashMap<Integer, GradesDAO.GradeCell>> allGrades =
                    dao.getGradesForSection(sectionId);

            // For each component
            for (String comp : comps) {
                GradesDAO.GradeCell cell =
                        allGrades.getOrDefault(comp, new LinkedHashMap<>()).get(enrollmentId);

                String display = null;
                if (cell != null) {
                    if (cell.score != null && cell.total != null) {
                        display = String.format("%.2f / %.2f (%.2f%%)",
                                cell.score, cell.total,
                                cell.percentage == null
                                        ? (cell.score / cell.total) * 100.0
                                        : cell.percentage);
                    }
                }

                model.addRow(new Object[]{courseLabel, comp, display});
            }

            // Add final grade row
            Double finalVal = dao.getFinalForEnrollment(enrollmentId);
            model.addRow(new Object[]{courseLabel, "Final", finalVal == null ? null : String.format("%.2f", finalVal)});
        }
    }

    /**
     * Fetches ALL enrollments for the student AND builds course label
     * Example: CS101 - Data Structures (S4)
     */
    private Map<Integer, String> getEnrollmentsForStudent(int studentId) {
        Map<Integer, String> map = new LinkedHashMap<>();

        String sql =
                "SELECT e.enrollment_id, s.section_id, c.code, c.title " +
                        "FROM enrollments e " +
                        "JOIN sections s ON e.section_id = s.section_id " +
                        "JOIN courses c ON s.course_id = c.course_id " +
                        "WHERE e.student_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int eid = rs.getInt("enrollment_id");
                    int sec = rs.getInt("section_id");
                    String code = rs.getString("code");
                    String title = rs.getString("title");
                    String label = code + " - " + title + " (S" + sec + ")";
                    map.put(eid, label);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * Gets section id from enrollment id
     */
    private int findSectionForEnrollment(int eid) {
        String sql = "SELECT section_id FROM enrollments WHERE enrollment_id = ?";
        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
