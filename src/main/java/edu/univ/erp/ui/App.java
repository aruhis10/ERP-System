package edu.univ.erp.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // 1. Set the FlatLaf Look and Feel
        try {
            FlatDarkLaf.setup(); // Or FlatLightLaf.setup() for a brighter theme
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
        }

        // 2. Start the application on the Event Dispatch Thread (Swing standard)
        SwingUtilities.invokeLater(() -> {
            new LoginWindow().setVisible(true);
        });
    }
}