package edu.univ.erp.ui.AdminDasboard;

import edu.univ.erp.service.AdminService;
import edu.univ.erp.domain.ServiceResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageUsersWindow extends JFrame {

    private final AdminService adminService = new AdminService();

    private final DefaultTableModel studentModel = new DefaultTableModel(
            new String[]{"Student ID", "Roll No", "Name", "Program", "Year", "Username", "Courses"}, 0) {

        @Override
        public boolean isCellEditable(int row, int column) {
            // Column 0 → Student ID (NOT editable)
            // Everything else editable ONLY if you want
            return column != 0;
        }
    };


    private final DefaultTableModel instructorModel = new DefaultTableModel(
            new String[]{"Instructor ID", "Department", "Username", "Courses"}, 0) {

        @Override
        public boolean isCellEditable(int row, int column) {
            // Column 0 → Instructor ID (NOT editable)
            return column != 0;
        }
    };
    private JTable studentTable;
    private JTable instructorTable;

    public ManageUsersWindow() {

        setTitle("Admin - Manage Users");
        setSize(1000, 730);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ================================
        // MAIN PANEL WITH TRANSPARENT BACKDROP
        // ================================
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // ================================
        // STUDENT TABLE WRAPPER
        // ================================
        studentTable = new JTable(studentModel);
        JScrollPane studentScroll = new JScrollPane(studentTable);
        studentScroll.setPreferredSize(new Dimension(950, 250));

        JLabel studentLabel = new JLabel("Students");
        studentLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        mainPanel.add(studentLabel);
        mainPanel.add(studentScroll);

        // ================================
        // STUDENT BUTTON STRIP
        // ================================
        JPanel studentButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton addStudentBtn = new JButton("Add Student");
        JButton toggleReg = new JButton("Toggle Registration Lock");
        JButton toggleGrade = new JButton("Toggle Grade Lock");

        studentButtons.add(addStudentBtn);
        studentButtons.add(toggleReg);
        studentButtons.add(toggleGrade);

        mainPanel.add(studentButtons);

        // ================================
        // INSTRUCTOR TABLE WRAPPER
        // ================================
        instructorTable = new JTable(instructorModel);
        JScrollPane instructorScroll = new JScrollPane(instructorTable);
        instructorScroll.setPreferredSize(new Dimension(950, 250));

        JLabel instructorLabel = new JLabel("Instructors");
        instructorLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(instructorLabel);
        mainPanel.add(instructorScroll);

        // ================================
        // INSTRUCTOR BUTTON STRIP
        // ================================
        JPanel instructorButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton addInstructorBtn = new JButton("Add Instructor");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");

        instructorButtons.add(addInstructorBtn);
        instructorButtons.add(editBtn);
        instructorButtons.add(deleteBtn);

        mainPanel.add(instructorButtons);

        // ADD MAIN PANEL TO WINDOW
        add(new JScrollPane(mainPanel));

        // ================================
        // BUTTON ACTIONS
        // ================================
        addStudentBtn.addActionListener(e -> openAddStudentDialog());
        addInstructorBtn.addActionListener(e -> openAddInstructorDialog());
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());

        toggleReg.addActionListener(e -> {
            ServiceResult r = adminService.toggleRegistrationLock();
            JOptionPane.showMessageDialog(this, r.getMessage());
        });

        toggleGrade.addActionListener(e -> {
            ServiceResult r = adminService.toggleGradeLock();
            JOptionPane.showMessageDialog(this, r.getMessage());
        });

        reloadTables();
    }

    // ================================
    // TABLE REFRESH
    // ================================
    private void reloadTables() {
        studentModel.setRowCount(0);
        instructorModel.setRowCount(0);

        List<String[]> studs = adminService.listStudents();
        for (String[] s : studs) studentModel.addRow(s);

        List<String[]> inst = adminService.listInstructors();
        for (String[] r : inst) instructorModel.addRow(r);
    }

    // ================================
    // STUDENT / INSTRUCTOR DIALOGS
    // (same as your old ones)
    // ================================
    private void openAddStudentDialog() { /* unchanged logic */ }
    private void openAddInstructorDialog() { /* unchanged logic */ }
    private void editSelected() { /* unchanged logic */ }
    private void deleteSelected() { /* unchanged logic */ }
}
