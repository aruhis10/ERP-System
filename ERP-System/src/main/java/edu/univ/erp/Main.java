package edu.univ.erp;

import javax.swing.SwingUtilities;
import edu.univ.erp.ui.LoginWindow;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginWindow());
    }
}
