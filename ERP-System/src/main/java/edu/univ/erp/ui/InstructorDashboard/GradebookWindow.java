package edu.univ.erp.ui.InstructorDashboard;

import edu.univ.erp.data.DAOs.GradesDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

/**
 * Instructor gradebook window.
 * - shows component columns as "score / total (xx.xx%)"
 * - Enter/Update grade dialog (roll, component, score, total)
 * - Refresh button
 * - Compute Final: asks per-component weights
 */
public class GradebookWindow extends JFrame {

    private final int sectionId;
    private final GradesDAO gradesDAO = new GradesDAO();

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;
    private LinkedHashSet<String> components;

    public GradebookWindow(int sectionId) {
        this.sectionId = sectionId;
        String sectionName = GradesDAO.getSectionName(sectionId);
        setTitle("Gradebook – " + sectionName);

        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton addGradeBtn = new JButton("Enter / Update Grade");
        addGradeBtn.addActionListener(this::handleAddOrUpdateGrade);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> reloadData());

        JButton computeBtn = new JButton("Compute Final (weights)");
        computeBtn.addActionListener(e -> handleComputeFinal());

        top.add(addGradeBtn);
        top.add(refreshBtn);
        top.add(computeBtn);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel();
        table = new JTable(model);
        table.setRowHeight(28);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        reloadData();
        setVisible(true);
    }

    private void reloadData() {
        model.setRowCount(0);
        model.setColumnCount(0);

        List<GradesDAO.StudentInfo> students = gradesDAO.getStudentsInSection(sectionId);
        components = gradesDAO.getComponentsForSection(sectionId);
        LinkedHashMap<String, LinkedHashMap<Integer, GradesDAO.GradeCell>> grades =
                gradesDAO.getGradesForSection(sectionId);

        model.addColumn("Roll No");
        model.addColumn("Full Name");
        model.addColumn("Section");
        model.addColumn("Enrollment ID"); // hidden later

        // add component columns (single column each)
        for (String comp : components) {
            model.addColumn(comp);
        }

        model.addColumn("Final");

        for (GradesDAO.StudentInfo s : students) {
            List<Object> row = new ArrayList<>();
            row.add(s.rollNo);
            row.add(s.fullName);
            String sectionName = GradesDAO.getSectionName(sectionId);
            row.add(sectionName);
            row.add(s.enrollmentId);

            for (String comp : components) {
                GradesDAO.GradeCell cell =
                        grades.getOrDefault(comp, new LinkedHashMap<>()).get(s.enrollmentId);

                if (cell == null || (cell.score == null && cell.total == null && cell.percentage == null)) {
                    row.add(null);
                } else {
                    String display;
                    if (cell.score != null && cell.total != null) {
                        double pct = cell.percentage == null ? (cell.score / cell.total) * 100.0 : cell.percentage;
                        display = String.format("%.2f / %.2f (%.2f%%)", cell.score, cell.total, pct);
                    } else if (cell.percentage != null) {
                        display = String.format("(%.2f%%)", cell.percentage);
                    } else {
                        display = null;
                    }
                    row.add(display);
                }
            }

            Double finalVal = gradesDAO.getFinalForEnrollment(s.enrollmentId);
            row.add(finalVal == null ? null : String.format("%.2f", finalVal));

            model.addRow(row.toArray());
        }

        // hide enrollment id column
        try {
            table.getColumnModel().getColumn(3).setMinWidth(0);
            table.getColumnModel().getColumn(3).setMaxWidth(0);
            table.getColumnModel().getColumn(3).setWidth(0);
        } catch (Exception ignored) {}
    }

    private void handleAddOrUpdateGrade(ActionEvent e) {
        JTextField rollField = new JTextField();
        JTextField compField = new JTextField();
        JTextField scoreField = new JTextField();
        JTextField totalField = new JTextField();

        Object[] inputs = {
                "Roll Number:", rollField,
                "Component Name:", compField,
                "Score:", scoreField,
                "Total:", totalField
        };

        int opt = JOptionPane.showConfirmDialog(
                this, inputs, "Enter / Update Grade", JOptionPane.OK_CANCEL_OPTION
        );

        if (opt != JOptionPane.OK_OPTION) return;

        try {
            String roll = rollField.getText().trim();
            String comp = compField.getText().trim();
            double score = Double.parseDouble(scoreField.getText().trim());
            double total = Double.parseDouble(totalField.getText().trim());

            int eid = gradesDAO.getEnrollmentIdForRollInSection(roll, sectionId);
            if (eid == -1) {
                JOptionPane.showMessageDialog(this, "Roll number not found in this section.");
                return;
            }

            gradesDAO.insertOrUpdateGrade(eid, comp, score, total);
            reloadData();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Score and Total must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Map<String, Double> askWeightsPerComponent() {
        if (components == null || components.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No components found. Enter at least one grade first.");
            return null;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        Map<String, JTextField> fields = new LinkedHashMap<>();

        panel.add(new JLabel("Enter weights for each component (0–100)."));
        panel.add(new JLabel("Leave blank = component ignored in weighted final."));
        panel.add(Box.createVerticalStrut(8));

        for (String comp : components) {
            JLabel label = new JLabel(comp + " Weight (%):");
            JTextField tf = new JTextField();
            fields.put(comp, tf);

            panel.add(label);
            panel.add(tf);
            tf.setPreferredSize(new Dimension(52, 28));
            panel.add(Box.createVerticalStrut(5));

        }

        int option = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Component Weights",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION)
            return null;

        Map<String, Double> weightMap = new LinkedHashMap<>();

        try {
            for (String comp : components) {
                String text = fields.get(comp).getText().trim();
                if (text.isEmpty()) continue;

                double w = Double.parseDouble(text);
                if (w < 0 || w > 100) throw new IllegalArgumentException("Weight must be 0–100");
                weightMap.put(comp, w);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid weight value: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return weightMap;
    }

    private void handleComputeFinal() {
        Map<String, Double> weights = askWeightsPerComponent();
        if (weights == null && components != null && !components.isEmpty()) {
            // user cancelled or input invalid; abort
            return;
        }

        try {
            gradesDAO.computeAndStoreFinalsForSection(sectionId, weights);
            reloadData();
            JOptionPane.showMessageDialog(this, "Finals computed and stored.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error computing finals: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
