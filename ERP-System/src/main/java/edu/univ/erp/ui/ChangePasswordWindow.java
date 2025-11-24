package edu.univ.erp.ui;

import edu.univ.erp.auth.AuthService;
import edu.univ.erp.domain.ServiceResult;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ChangePasswordWindow extends JFrame {

    private final AuthService authService = new AuthService();

    public ChangePasswordWindow(String username) {
        setTitle("Set New Password");
        setSize(500, 350);
        setLocationRelativeTo(null);

        JPanel bg = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 60, 140, 120),
                        getWidth(), getHeight(), new Color(0, 40, 100, 120)
                );

                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

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
        card.setPreferredSize(new Dimension(380, 260));

        JLabel title = new JLabel("Set New Password", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField p1 = new JPasswordField();
        JPasswordField p2 = new JPasswordField();

        JButton updateBtn = new JButton("Update Password");
        styleButton(updateBtn);

        updateBtn.addActionListener(e -> {
            String pass1 = new String(p1.getPassword());
            String pass2 = new String(p2.getPassword());

            if (pass1.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty.");
                return;
            }

            if (!pass1.equals(pass2)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.");
                return;
            }

            ServiceResult r = authService.setNewPasswordDirect(username, pass1);

            JOptionPane.showMessageDialog(this, r.getMessage());
            if (r.isSuccess()) {
                dispose();
            }
        });

        addField(card, "New Password:", p1);
        addField(card, "Confirm Password:", p2);

        card.add(updateBtn);

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
