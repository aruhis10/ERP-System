package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Reusable component for displaying sortable data tables throughout the ERP system (Stage 3.3).
 */
public class ERPSortableTable extends JScrollPane {

    private JTable table;
    private DefaultTableModel model;

    /**
     * Creates a new table panel with the specified column headers.
     * @param columnHeaders An array of Strings for the table column names.
     */
    public ERPSortableTable(String[] columnHeaders) {
        // Set up the table model with zero rows initially
        model = new DefaultTableModel(columnHeaders, 0) {
            // Makes the cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        // --- Enable Sorting (Crucial Feature) ---
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Aesthetic setup
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // JScrollPane setup: the table itself is viewed through the scroll pane
        setViewportView(table);
    }

    /**
     * Clears all existing data from the table.
     */
    public void clearTable() {
        model.setRowCount(0);
    }

    /**
     * Adds a single row of data to the table.
     * @param rowData An array of Objects representing the row's data.
     */
    public void addRow(Object[] rowData) {
        model.addRow(rowData);
    }

    /**
     * Gets the JTable component for advanced configuration or listener attachment.
     * @return The internal JTable instance.
     */
    public JTable getTable() {
        return table;
    }
}