package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.data.DAOs.AuthUserDAO;
import edu.univ.erp.domain.AuthUser;
import edu.univ.erp.domain.ServiceResult;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ForgotPasswordWindow extends JFrame {

    private final AuthService authService = new AuthService();
    private final AuthUserDAO authUserDAO = new AuthUserDAO();

    public ForgotPasswordWindow() {
        setTitle("Forgot Password");
        setSize(500, 450);
        setLocationRelativeTo(null);

        JPanel bg = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                // gradient background like login
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 60, 140, 120),
                        getWidth(), getHeight(), new Color(0, 40, 100, 120)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        // frosted card panel
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(255, 255, 255, 210));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        card.setPreferredSize(new Dimension(380, 350));

        JLabel title = new JLabel("Forgot Password", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField usernameField = new JTextField();
        JLabel questionLabel = new JLabel("Security Question");
        JTextField answerField = new JTextField();
        JPasswordField passField = new JPasswordField();

        JButton load = new JButton("Load Question");
        JButton reset = new JButton("Reset Password");

        styleButton(load);
        styleButton(reset);

        load.addActionListener(e -> {
            AuthUser u = authUserDAO.findByUsername(usernameField.getText().trim());
            if (u == null)
                questionLabel.setText("User not found.");
            else
                questionLabel.setText(u.getSecurityQuestion());
        });

        reset.addActionListener(e -> {
            ServiceResult r = authService.resetPassword(
                    usernameField.getText(),
                    answerField.getText(),
                    new String(passField.getPassword())
            );
            JOptionPane.showMessageDialog(this, r.getMessage());
        });

        addField(card, "Username:", usernameField);
        card.add(load);
        card.add(Box.createVerticalStrut(10));
        card.add(questionLabel);
        addField(card, "Your Answer:", answerField);
        addField(card, "New Password:", passField);
        card.add(Box.createVerticalStrut(10));
        card.add(reset);

        bg.add(card, new GridBagConstraints());
        add(bg);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void addField(JPanel card, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        card.add(lbl);
        card.add(field);
        card.add(Box.createVerticalStrut(10));
    }

    private void styleButton(JButton btn) {
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setForeground(new Color(30, 50, 90));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }
}
