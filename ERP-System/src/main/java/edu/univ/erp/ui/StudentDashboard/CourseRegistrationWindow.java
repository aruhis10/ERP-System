package edu.univ.erp.ui.StudentDashboard;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.domain.CourseSectionRow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CourseRegistrationWindow extends JFrame {

    private final StudentService studentService;
    private final int studentId;

    private JTable tblAdded;
    private JTable tblCatalog;

    public CourseRegistrationWindow() {

        this.studentService = new StudentService();
        this.studentId = SessionManager.getInstance().getCurrentUser().getUserId();

        setTitle("Course Registration");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //---------------------------------------------------------
        // MAIN PANEL
        //---------------------------------------------------------
        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //---------------------------------------------------------
        // TITLE
        //---------------------------------------------------------
        JLabel title = new JLabel("Course Registration");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(title);
        main.add(Box.createVerticalStrut(25));

        //---------------------------------------------------------
        // TABLE 1 — REGISTERED COURSES
        //---------------------------------------------------------
        JLabel lblAdded = new JLabel("Your Registered Courses");
        lblAdded.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblAdded.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(lblAdded);
        main.add(Box.createVerticalStrut(10));

        tblAdded = new JTable();
        tblAdded.setRowHeight(26);

        JScrollPane spAdded = new JScrollPane(tblAdded);
        spAdded.setPreferredSize(new Dimension(900, 200));
        spAdded.setAlignmentX(Component.LEFT_ALIGNMENT);

        main.add(spAdded);
        main.add(Box.createVerticalStrut(10));

        JButton btnDrop = new JButton("Drop Selected Course");
        btnDrop.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnDrop.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDrop.addActionListener(e -> dropSelected());
        main.add(btnDrop);

        main.add(Box.createVerticalStrut(35));

        //---------------------------------------------------------
        // TABLE 2 — FULL CATALOG
        //---------------------------------------------------------
        JLabel lblCatalog = new JLabel("All Available Courses");
        lblCatalog.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblCatalog.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(lblCatalog);
        main.add(Box.createVerticalStrut(10));

        tblCatalog = new JTable();
        tblCatalog.setRowHeight(26);

        JScrollPane spCatalog = new JScrollPane(tblCatalog);
        spCatalog.setPreferredSize(new Dimension(900, 260));
        spCatalog.setAlignmentX(Component.LEFT_ALIGNMENT);

        main.add(spCatalog);
        main.add(Box.createVerticalStrut(10));

        JButton btnAdd = new JButton("Add Selected Course");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAdd.addActionListener(e -> addSelected());
        main.add(btnAdd);

        //---------------------------------------------------------
        // LOAD TABLES
        //---------------------------------------------------------
        refreshTables();

        add(main);
    }

    // =========================================================
    // REFRESH BOTH TABLES
    // =========================================================
    private void refreshTables() {
        loadAddedCourses();
        loadCatalog();
    }

    // =========================================================
    // LOAD REGISTERED COURSES
    // =========================================================
    private void loadAddedCourses() {

        String[] cols = {"Course Code", "Course Name", "Credits", "Section"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        // timetable rows: {code, title, schedule, room}
        List<Object[]> added = studentService.getTimetable(studentId);

        for (Object[] r : added) {

            String code = (String) r[0];
            String name = (String) r[1];

            int credits = studentService.getCreditsForCourse(code);
            int sectionId = studentService.getSectionIdForStudentCourse(studentId, code);
            String sectionName = studentService.getSectionNameForSection(sectionId);

            model.addRow(new Object[]{
                    code,
                    name,
                    credits,
                    sectionName != null ? sectionName : sectionId
            });
        }

        tblAdded.setModel(model);
    }

    // =========================================================
    // LOAD FULL COURSE CATALOG
    // =========================================================
    private void loadCatalog() {

        String[] cols = {"Course Code", "Course Name", "Credits", "Section", "Section ID"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        List<CourseSectionRow> catalog = studentService.getCourseCatalog();

        for (CourseSectionRow r : catalog) {
            model.addRow(new Object[]{
                    r.getCode(),        // FIXED
                    r.getTitle(),       // FIXED
                    r.getCredits(),     // already double, correct
                    r.getSectionName() != null ? r.getSectionName() : "S" + r.getSectionId(),
                    r.getSectionId()    // Hidden column for sectionId
            });
        }

        tblCatalog.setModel(model);
        // Hide the Section ID column
        tblCatalog.getColumnModel().getColumn(4).setMinWidth(0);
        tblCatalog.getColumnModel().getColumn(4).setMaxWidth(0);
        tblCatalog.getColumnModel().getColumn(4).setWidth(0);
    }


    // =========================================================
    // DROP SELECTED COURSE
    // =========================================================
    private void dropSelected() {

        int row = tblAdded.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to drop.");
            return;
        }

        String code = (String) tblAdded.getValueAt(row, 0);
        int sectionId = studentService.getSectionIdForStudentCourse(studentId, code);

        var res = studentService.drop(sectionId);

        JOptionPane.showMessageDialog(this, res.getMessage());
        refreshTables();
    }

    // =========================================================
    // ADD SELECTED COURSE
    // =========================================================
    private void addSelected() {

        int row = tblCatalog.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a course to add.");
            return;
        }

        String code = (String) tblCatalog.getValueAt(row, 0);
        int sectionId = (Integer) tblCatalog.getValueAt(row, 4);  // Get sectionId from hidden column

        var res = studentService.register(sectionId);

        JOptionPane.showMessageDialog(this, res.getMessage());
        refreshTables();
    }
}
