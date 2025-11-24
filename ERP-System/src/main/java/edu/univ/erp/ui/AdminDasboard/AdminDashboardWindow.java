package edu.univ.erp.ui.AdminDasboard;

import edu.univ.erp.data.DAOs.SettingsDAO;
import edu.univ.erp.ui.AdminDasboard.ManageUsersWindow;
import edu.univ.erp.ui.AdminDasboard.CourseManagementWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class AdminDashboardWindow extends JFrame {

    private BufferedImage mintBG;
    private final SettingsDAO settingsDAO = new SettingsDAO();

    public AdminDashboardWindow() {

        try {
            mintBG = ImageIO.read(new File("src/main/resources/gradient-mint-background.jpg"));
        } catch (Exception ignored) {}

        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 900);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        buildHero(root);
        buildCenter(root);
        buildFooter(root);

        JScrollPane scroll = new JScrollPane(root);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        setContentPane(scroll);

        setVisible(true);
    }

    // ============================================================
    // HERO (16px rounded)
    // ============================================================
    private void buildHero(JPanel root) {

        JPanel hero = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (mintBG != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    Shape clip = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16);
                    g2.setClip(clip);
                    g2.drawImage(mintBG, 0, 0, getWidth(), getHeight(), this);
                    g2.dispose();
                }
            }
        };

        hero.setPreferredSize(new Dimension(0, 180));
        hero.setLayout(new BorderLayout());
        hero.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));

        JLabel header = new JLabel("Welcome Admin");
        header.setFont(new Font("Segoe UI", Font.BOLD, 40));
        header.setForeground(Color.WHITE);

        hero.add(header, BorderLayout.WEST);
        root.add(hero, BorderLayout.NORTH);
    }

    // ============================================================
    // CENTER BODY
    // ============================================================
    private void buildCenter(JPanel root) {

        JPanel holder = new JPanel(new BorderLayout());
        holder.setBackground(Color.WHITE);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Left aligned now
        JLabel dash = new JLabel("Admin Dashboard");
        dash.setFont(new Font("Segoe UI", Font.BOLD, 28));
        dash.setForeground(Color.BLACK);
        dash.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(dash);
        body.add(Box.createVerticalStrut(25));

        // Toggle button centered
        JButton toggle = buildMaintenanceButton();
        toggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        body.add(toggle);
        body.add(Box.createVerticalStrut(40));

        // Manage Users Card
        body.add(buildCard("Manage Users", () -> new ManageUsersWindow().setVisible(true)));
        body.add(Box.createVerticalStrut(30));

        // Manage Courses Card
        body.add(buildCard("Manage Courses", () -> new CourseManagementWindow().setVisible(true)));
        body.add(Box.createVerticalStrut(60));

        holder.add(body, BorderLayout.WEST);
        root.add(holder, BorderLayout.CENTER);
    }

    // ============================================================
    // MAINTENANCE TOGGLE BUTTON
    // ============================================================
    private JButton buildMaintenanceButton() {

        String mode = settingsDAO.getSettingValue("maintenance_mode");
        boolean isOn = "ON".equalsIgnoreCase(mode);

        JButton btn = new JButton("Maintenance Mode: " +
                (isOn ? "ON — Click to Disable" : "OFF — Click to Enable")) {

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(210,210,210));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(330, 45));

        btn.addActionListener(e -> {
            try {
                settingsDAO.setSettingValue("maintenance_mode", isOn ? "OFF" : "ON");
                JOptionPane.showMessageDialog(this, "Maintenance Mode updated.");
                dispose();
                new AdminDashboardWindow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to update maintenance mode.");
            }
        });

        return btn;
    }

    // ============================================================
    // FEATURE CARD (16px rounded)
    // ============================================================
    private JPanel buildCard(String title, Runnable onClick) {

        JPanel card = new JPanel() {

            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                // Clip the background with 16px radius
                Shape clip = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setClip(clip);

                if (mintBG != null) {
                    g2.drawImage(mintBG, 0, 0, getWidth(), getHeight(), null);
                }

                g2.dispose();
            }
        };

        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(900, 140));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(Color.WHITE);

        JButton go = new JButton("GO") {

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(90, 90, 90));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        go.setForeground(Color.WHITE);
        go.setFocusPainted(false);
        go.setOpaque(false);
        go.setContentAreaFilled(false);
        go.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        go.setFont(new Font("Segoe UI", Font.BOLD, 16));
        go.setPreferredSize(new Dimension(90, 40));
        go.addActionListener(e -> onClick.run());

        card.add(lbl, BorderLayout.WEST);
        card.add(go, BorderLayout.EAST);

        return card;
    }

    // ============================================================
    // FOOTER
    // ============================================================
    private void buildFooter(JPanel root) {

        JPanel footer = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                if (mintBG != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    Shape clip = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16);
                    g2.setClip(clip);
                    g2.drawImage(mintBG, 0, 0, getWidth(), getHeight(), this);
                    g2.dispose();
                }
            }
        };

        footer.setPreferredSize(new Dimension(0, 180));
        footer.setOpaque(false);
        footer.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        footer.setLayout(new GridLayout(1, 3, 80, 10));

        footer.add(makeFooterColumn("Features", "Manage Users", "Manage Courses"));
        footer.add(makeFooterColumn("Support", "support@iiitd.ac.in"));
        footer.add(makeFooterColumn("Made By", "Parikshit Menon", "Aruhi Sharma"));

        root.add(footer, BorderLayout.SOUTH);
    }

    private JPanel makeFooterColumn(String title, String... lines) {

        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));

        JLabel head = new JLabel(title);
        head.setFont(new Font("Segoe UI", Font.BOLD, 19));
        head.setForeground(Color.WHITE);
        head.setAlignmentX(Component.LEFT_ALIGNMENT);
        col.add(head);
        col.add(Box.createVerticalStrut(10));

        for (String l : lines) {
            JLabel lbl = new JLabel(l);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            lbl.setForeground(Color.WHITE);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            col.add(lbl);
            col.add(Box.createVerticalStrut(6));
        }

        return col;
    }
}
