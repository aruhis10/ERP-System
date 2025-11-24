package edu.univ.erp.ui.AdminDasboard;

import edu.univ.erp.service.CourseService;
import edu.univ.erp.domain.ServiceResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CourseManagementWindow extends JFrame {

    private final CourseService courseService = new CourseService();

    private final DefaultTableModel courseModel = new DefaultTableModel(
            new String[]{
                    "Course ID", "Code", "Title", "Credits",
                    "Section ID", "Section Name",
                    "Instructor", "Day/Time", "Room",
                    "Capacity", "Enrolled"
            }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };

    private JTable courseTable;

    public CourseManagementWindow() {

        setTitle("Admin - Course Management");
        setSize(950, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // MAIN PANEL (NO OUTER SCROLL)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // TITLE
        JLabel title = new JLabel("Course Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);

        mainPanel.add(Box.createVerticalStrut(15));

        // TABLE
        courseTable = new JTable(courseModel);
        JScrollPane tableScroll = new JScrollPane(courseTable);
        tableScroll.setPreferredSize(new Dimension(900, 350));
        mainPanel.add(tableScroll);

        mainPanel.add(Box.createVerticalStrut(15));

        // BUTTON ROW #1 – Courses
        JPanel courseButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addCourseBtn = new JButton("Add Course");
        JButton deleteSectionBtn = new JButton("Delete Section");   // <-- REPLACED Delete Course

        courseButtons.add(addCourseBtn);
        courseButtons.add(deleteSectionBtn);
        mainPanel.add(courseButtons);

        // BUTTON ROW #2 – Sections
        JPanel sectionButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addSectionBtn = new JButton("Add Section");
        JButton assignInstructorBtn = new JButton("Assign Instructor");
        JButton unassignInstructorBtn = new JButton("Unassign Instructor");

        sectionButtons.add(addSectionBtn);
        sectionButtons.add(assignInstructorBtn);
        sectionButtons.add(unassignInstructorBtn);
        mainPanel.add(sectionButtons);

        mainPanel.add(Box.createVerticalStrut(10));

        // OUTPUT LOG AREA
        JTextArea output = new JTextArea();
        output.setEditable(false);
        JScrollPane logScroll = new JScrollPane(output);
        logScroll.setPreferredSize(new Dimension(900, 150));

        mainPanel.add(logScroll);

        add(mainPanel);

        // ACTION BINDINGS
        addCourseBtn.addActionListener(e -> addCourseDialog(output));
        deleteSectionBtn.addActionListener(e -> deleteSectionDialog(output));
        addSectionBtn.addActionListener(e -> addSectionDialog(output));
        assignInstructorBtn.addActionListener(e -> assignInstructorDialog(output));
        unassignInstructorBtn.addActionListener(e -> unassignInstructorDialog(output));

        reloadCoursesTable();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // ===================================================================
    // Reload table
    // ===================================================================
    private void reloadCoursesTable() {
        courseModel.setRowCount(0);

        var rows = courseService.getFullCourseSectionData();
        for (String[] r : rows) courseModel.addRow(r);
    }

    // ===================================================================
    // Add Course
    // ===================================================================
    private void addCourseDialog(JTextArea out) {
        JTextField code = new JTextField();
        JTextField title = new JTextField();
        JTextField credits = new JTextField();

        Object[] f = {
                "Course Code", code,
                "Title", title,
                "Credits", credits
        };

        if (JOptionPane.showConfirmDialog(this, f, "Add Course", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {

            try {
                int c = Integer.parseInt(credits.getText());
                out.append(courseService.addCourse(code.getText(), title.getText(), c).getMessage() + "\n");
                reloadCoursesTable();

            } catch (Exception ex) {
                out.append("Invalid credits.\n");
            }
        }
    }

    // ===================================================================
    // Delete Section (courseId + sectionName)
    // ===================================================================
    private void deleteSectionDialog(JTextArea out) {
        JTextField cid = new JTextField();
        JTextField name = new JTextField();

        Object[] f = {
                "Course ID", cid,
                "Section Name", name
        };

        if (JOptionPane.showConfirmDialog(this, f, "Delete Section", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {

            try {
                int courseId = Integer.parseInt(cid.getText().trim());
                String sectionName = name.getText().trim();

                ServiceResult r = courseService.deleteSection(courseId, sectionName);
                out.append(r.getMessage() + "\n");

                reloadCoursesTable();

            } catch (Exception ex) {
                out.append("Invalid input.\n");
            }
        }
    }

    // ===================================================================
    // Add Section
    // ===================================================================
    private void addSectionDialog(JTextArea out) {

        JTextField courseId = new JTextField();
        JTextField name = new JTextField();
        JTextField dt = new JTextField();
        JTextField room = new JTextField();
        JTextField cap = new JTextField();
        JTextField sem = new JTextField();
        JTextField year = new JTextField();

        Object[] f = {
                "Course ID", courseId,
                "Section Name", name,
                "Day/Time", dt,
                "Room", room,
                "Capacity", cap,
                "Semester", sem,
                "Year", year
        };

        if (JOptionPane.showConfirmDialog(this, f, "Add Section", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {

            try {
                int cid = Integer.parseInt(courseId.getText());
                int capacity = Integer.parseInt(cap.getText());
                int yr = Integer.parseInt(year.getText());

                out.append(courseService.addSection(
                        cid, name.getText(), dt.getText(), room.getText(),
                        capacity, sem.getText(), yr
                ).getMessage() + "\n");

                reloadCoursesTable();

            } catch (Exception ex) {
                out.append("Invalid input.\n");
            }
        }
    }

    // ===================================================================
    // Assign Instructor
    // ===================================================================
    private void assignInstructorDialog(JTextArea out) {

        JTextField courseId = new JTextField();
        JTextField sectionName = new JTextField();
        JTextField instructorId = new JTextField();

        Object[] f = {
                "Course ID", courseId,
                "Section Name", sectionName,
                "Instructor ID", instructorId
        };

        if (JOptionPane.showConfirmDialog(this, f, "Assign Instructor", JOptionPane.OK_CANCEL_OPTION)
                == JOptionPane.OK_OPTION) {

            try {
                int cid = Integer.parseInt(courseId.getText().trim());
                String secName = sectionName.getText().trim();
                int instId = Integer.parseInt(instructorId.getText().trim());

                // NEW: resolve sectionId using name + course
                Integer secId = courseService.getSectionId(cid, secName);

                if (secId == null) {
                    out.append("Section not found.\n");
                    return;
                }

                ServiceResult r = courseService.assignInstructor(secId, instId);
                out.append(r.getMessage() + "\n");
                reloadCoursesTable();

            } catch (Exception ex) {
                out.append("Invalid input.\n");
            }
        }
    }


    // ===================================================================
    // Unassign Instructor
    // ===================================================================
    private void unassignInstructorDialog(JTextArea out) {
        String s = JOptionPane.showInputDialog(this, "Section ID:");
        if (s == null) return;

        try {
            out.append(courseService.unassignInstructor(Integer.parseInt(s)).getMessage() + "\n");
            reloadCoursesTable();
        } catch (Exception ex) {
            out.append("Invalid Section ID.\n");
        }
    }
}
