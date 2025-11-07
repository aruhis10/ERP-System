package edu.univ.erp.ui;

import edu.univ.erp.auth.LoginService;
import edu.univ.erp.auth.SessionManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
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

        // Load and blur background
        final BufferedImage backgroundImage;
        try {
            backgroundImage = blurImage(ImageIO.read(new File("src/main/resources/campus_bg.jpg")), 10);
        } catch (IOException e) {
            throw new RuntimeException("Background image not found.", e);
        }

        // Background panel
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                // Blue gradient overlay
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 60, 140),
                        getWidth(), getHeight(), new Color(0, 40, 100)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        // Frosted glass login card
        JPanel loginCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.92f));
                g2.setColor(new Color(255, 255, 255, 210));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 35, 35));
                g2.dispose();
            }
        };
        loginCard.setOpaque(false);
        loginCard.setPreferredSize(new Dimension(360, 420));
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // IIITD Logo
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

        // Title
        JLabel title = new JLabel("Login");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(25, 50, 90));
        loginCard.add(title);
        loginCard.add(Box.createVerticalStrut(25));

        // Username
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(30, 50, 70));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(usernameLabel);
        loginCard.add(Box.createVerticalStrut(5));

        usernameField = new RoundedTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameField.setForeground(Color.BLACK);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        loginCard.add(usernameField);
        loginCard.add(Box.createVerticalStrut(18));

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(30, 50, 70));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(passwordLabel);
        loginCard.add(Box.createVerticalStrut(5));

        passwordField = new RoundedPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setForeground(Color.BLACK);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        loginCard.add(passwordField);
        loginCard.add(Box.createVerticalStrut(25));

        // --- Solid Color Login Button (No Transparency) ---
        loginButton = new JButton("Log in") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fill background with solid color
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

        // Default button color
        loginButton.setBackground(Color.decode("#3DC7B5"));

        // Size and alignment
        loginButton.setPreferredSize(new Dimension(280, 45));
        loginButton.setMaximumSize(new Dimension(280, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // --- Hover Effect ---
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                loginButton.setBackground(Color.decode("#3DD6D3"));
                loginButton.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                loginButton.setBackground(Color.decode("#3DC7B5"));
                loginButton.repaint();
            }
        });

        // Wrap to maintain width under BoxLayout
        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setOpaque(false);
        buttonWrapper.setLayout(new BoxLayout(buttonWrapper, BoxLayout.X_AXIS));
        buttonWrapper.add(Box.createHorizontalGlue());
        buttonWrapper.add(loginButton);
        buttonWrapper.add(Box.createHorizontalGlue());
        loginCard.add(buttonWrapper);
        loginCard.add(Box.createVerticalStrut(15));

        // Message label
        messageLabel = new JLabel(" ");
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setForeground(Color.RED);
        loginCard.add(messageLabel);

        backgroundPanel.add(loginCard, new GridBagConstraints());
        add(backgroundPanel);

        // Listeners
        loginButton.addActionListener(e -> handleLogin());
        passwordField.addActionListener(e -> handleLogin());
    }

    private BufferedImage blurImage(BufferedImage src, int radius) {
        int size = radius * 2 + 1;
        float[] data = new float[size * size];
        float value = 1f / (size * size);
        for (int i = 0; i < data.length; i++) data[i] = value;
        ConvolveOp op = new ConvolveOp(new Kernel(size, size, data), ConvolveOp.EDGE_NO_OP, null);
        return op.filter(src, null);
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

        SwingUtilities.invokeLater(() -> {
            try {
                LoginService.LoginResult result = loginService.login(username, password);
                if (result.isSuccess()) {
                    String role = SessionManager.getInstance().getCurrentUser().getRole();
                    JOptionPane.showMessageDialog(this,
                            "Welcome, " + username + " (" + role + ")",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            result.getMessage(),
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Unexpected Error",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                loginButton.setEnabled(true);
            }
        });
    }

    // Rounded input fields
    static class RoundedTextField extends JTextField {
        public RoundedTextField() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(255, 255, 255, 240));
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
            g2.setColor(new Color(180, 180, 180));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}
