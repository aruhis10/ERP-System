package edu.univ.erp.ui;

import edu.univ.erp.auth.LoginService;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.ui.AdminDasboard.AdminDashboardWindow;
import edu.univ.erp.ui.InstructorDashboard.InstructorDashboardWindow;
import edu.univ.erp.ui.StudentDashboard.StudentDashboardWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;
    private LoginService loginService;

    public LoginWindow() {
        loginService = new LoginService();
        initializeUI();
        setVisible(true);
    }

    private void initializeUI() {
        setTitle("IIITD ERP - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        final BufferedImage backgroundImage;
        try {
            backgroundImage = ImageIO.read(new File("src/main/resources/campus_bg.jpg"));
        } catch (IOException e) {
            throw new RuntimeException("Background image not found.", e);
        }

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 60, 140, 120),
                        getWidth(), getHeight(), new Color(0, 40, 100, 120)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.dispose();
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        JPanel loginCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 210));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 35, 35));
                g2.dispose();
            }
        };
        loginCard.setOpaque(false);
        loginCard.setPreferredSize(new Dimension(360, 460));
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        try {
            ImageIcon logoIcon = new ImageIcon("src/main/resources/iiitd_logo.png");
            Image scaledLogo = logoIcon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            loginCard.add(logoLabel);
        } catch (Exception e) {
            JLabel fallback = new JLabel("IIITD ERP", SwingConstants.CENTER);
            fallback.setFont(new Font("Segoe UI", Font.BOLD, 28));
            fallback.setForeground(new Color(20, 40, 80));
            fallback.setAlignmentX(Component.CENTER_ALIGNMENT);
            loginCard.add(fallback);
        }

        loginCard.add(Box.createVerticalStrut(15));

        JLabel title = new JLabel("Login");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(25, 50, 90));
        loginCard.add(title);
        loginCard.add(Box.createVerticalStrut(25));

        // Username
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(30, 50, 70));
        loginCard.add(usernameLabel);

        usernameField = new RoundedTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameField.setForeground(Color.BLACK);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        loginCard.add(usernameField);
        loginCard.add(Box.createVerticalStrut(18));

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(30, 50, 70));
        loginCard.add(passwordLabel);

        passwordField = new RoundedPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setForeground(Color.BLACK);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        loginCard.add(passwordField);
        loginCard.add(Box.createVerticalStrut(15));

        // --- Styled Forgot Password Button ---
        JButton forgotBtn = new JButton("Forgot Password?") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(255, 255, 255, 180));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

                super.paintComponent(g2);
                g2.dispose();
            }
        };
        forgotBtn.setFocusPainted(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setOpaque(false);
        forgotBtn.setBorderPainted(false);
        forgotBtn.setForeground(new Color(30, 50, 90));
        forgotBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        forgotBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotBtn.setMaximumSize(new Dimension(250, 35));

        forgotBtn.addActionListener(e -> new ForgotPasswordWindow().setVisible(true));
        loginCard.add(forgotBtn);
        loginCard.add(Box.createVerticalStrut(15));

        // Login button
        loginButton = new JButton("Log in") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));

                super.paintComponent(g2);
                g2.dispose();
            }
        };
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(false);
        loginButton.setBorderPainted(false);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(Color.decode("#3DC7B5"));
        loginButton.setPreferredSize(new Dimension(280, 45));
        loginButton.setMaximumSize(new Dimension(280, 45));

        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                loginButton.setBackground(Color.decode("#3DD6D3"));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                loginButton.setBackground(Color.decode("#3DC7B5"));
            }
        });

        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setOpaque(false);
        buttonWrapper.setLayout(new BoxLayout(buttonWrapper, BoxLayout.X_AXIS));
        buttonWrapper.add(Box.createHorizontalGlue());
        buttonWrapper.add(loginButton);
        buttonWrapper.add(Box.createHorizontalGlue());
        loginCard.add(buttonWrapper);
        loginCard.add(Box.createVerticalStrut(10));

        messageLabel = new JLabel(" ");
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setForeground(Color.RED);
        loginCard.add(messageLabel);

        backgroundPanel.add(loginCard, new GridBagConstraints());
        add(backgroundPanel);

        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        loginButton.setEnabled(false);
        messageLabel.setText("Logging in...");

        SwingUtilities.invokeLater(() -> {
            try {
                LoginService.LoginResult result = loginService.login(username, password);

                if (result.isSuccess()) {

                    String role = SessionManager.getInstance().getCurrentUser().getRole();

                    JOptionPane.showMessageDialog(this,
                            "Login successful (" + role + ")",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                    if ("ADMIN".equalsIgnoreCase(role)) {
                        new AdminDashboardWindow().setVisible(true);
                    } else if ("STUDENT".equalsIgnoreCase(role)) {
                        new StudentDashboardWindow(username).setVisible(true);
                    } else if ("INSTRUCTOR".equalsIgnoreCase(role)) {
                        new InstructorDashboardWindow(username).setVisible(true);
                    }

                    this.dispose();
                    return;
                }

                // FIRST LOGIN HANDLING
                if ("FIRST_LOGIN".equals(result.getMessage())) {
                    messageLabel.setText("First login detected. Set a new password.");
                    new ChangePasswordWindow(username).setVisible(true);
                    return;
                }

                // Normal error
                messageLabel.setText(result.getMessage());
                JOptionPane.showMessageDialog(this,
                        result.getMessage(),
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);

            } finally {
                loginButton.setEnabled(true);
            }
        });
    }

    static class RoundedTextField extends JTextField {
        public RoundedTextField() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(255, 255, 255, 240));
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
            g2.setColor(new Color(180, 180, 180));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    static class RoundedPasswordField extends JPasswordField {
        public RoundedPasswordField() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(255, 255, 255, 240));
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
            g2.setColor(new Color(180, 180, 180));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}
