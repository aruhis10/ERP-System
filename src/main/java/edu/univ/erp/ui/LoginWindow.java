package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    public LoginWindow() {
        setTitle("University ERP Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setLocationRelativeTo(null); // Center the window

        // Initialize Components
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        messageLabel = new JLabel("Enter your credentials.");

        // Add components to the frame (using simple layout for now)
        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(loginButton);
        add(messageLabel);

        // Attach action listener to the login button
        loginButton.addActionListener(new LoginButtonListener());
    }

    // Inner class to handle button clicks
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);

            // TODO: Step 3 will integrate the LoginService call here
            System.out.println("Attempting login for: " + username);

            // Clear password array immediately after use for security
            java.util.Arrays.fill(passwordChars, '0');
        }
    }
}