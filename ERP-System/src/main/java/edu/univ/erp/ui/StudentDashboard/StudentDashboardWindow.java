package edu.univ.erp.ui.StudentDashboard;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.StudentDashboard.StudentGradesWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class StudentDashboardWindow extends JFrame {

    private final StudentService studentService;
    private BufferedImage mintBG;
    private BufferedImage footerBG;

    public StudentDashboardWindow(String username) {
        this.studentService = new StudentService();
        int studentId = SessionManager.getInstance().getCurrentUser().getUserId();

        // load images
        try {
            mintBG = ImageIO.read(new File("src/main/resources/gradient-mint-background.jpg"));
            footerBG = ImageIO.read(new File("src/main/resources/gradient-mint-background.jpg"));
        } catch (Exception e) {
            System.err.println("Could not load background images: " + e.getMessage());
        }

        setTitle("Student Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1000);
        setLocationRelativeTo(null);

        // Top-level content panel uses BorderLayout for predictable stretching
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);

        // HERO: full-width banner at NORTH
        JPanel hero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (mintBG != null) {
                    g.drawImage(mintBG, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(0, 180, 160));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        hero.setPreferredSize(new Dimension(0, 200));
        hero.setMinimumSize(new Dimension(0, 200));
        hero.setLayout(new BorderLayout());
        hero.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Welcome Student");
        title.setFont(new Font("Segoe UI", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        hero.add(title, BorderLayout.WEST);
        content.add(hero, BorderLayout.NORTH);

        // CENTER: a holder panel which contains the left-flush body
        JPanel centerHolder = new JPanel(new BorderLayout());
        centerHolder.setBackground(Color.WHITE);

        // BODY: left column panel with fixed left inset (zero) and right flexible gap
        JPanel body = new JPanel();
        body.setBackground(Color.WHITE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40)); // left padding = 40
        // Note: left padding is 40 by design; reduce to 0 if you want no left offset
        // body.setBorder(BorderFactory.createEmptyBorder(30, 0, 40, 0));

        // Titles (black on white)
        JLabel sub1 = new JLabel("Welcome to IIITD ERP");
        sub1.setFont(new Font("Segoe UI", Font.BOLD, 24));
        sub1.setForeground(Color.BLACK);
        sub1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub2 = new JLabel("Following are your details");
        sub2.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        sub2.setForeground(Color.BLACK);
        sub2.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(sub1);
        body.add(Box.createVerticalStrut(6));
        body.add(sub2);
        body.add(Box.createVerticalStrut(26));

        // Registered courses header
        JLabel reg = new JLabel("Registered Courses");
        reg.setFont(new Font("Segoe UI", Font.BOLD, 20));
        reg.setForeground(Color.BLACK);
        reg.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(reg);
        body.add(Box.createVerticalStrut(12));

        // Course row — full width left-aligned (use BoxLayout X-axis)
        JPanel courseRow = new JPanel();
        courseRow.setOpaque(false);
        courseRow.setLayout(new BoxLayout(courseRow, BoxLayout.X_AXIS));
        courseRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        List<Object[]> registered = studentService.getTimetable(studentId);
        for (Object[] r : registered) {
            courseRow.add(makeCourseBox((String) r[0]));
            courseRow.add(Box.createHorizontalStrut(16));
        }
        body.add(courseRow);
        body.add(Box.createVerticalStrut(36));

        // Timetable header
        JLabel tLabel = new JLabel("Timetable");
        tLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        tLabel.setForeground(Color.BLACK);
        tLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(tLabel);
        body.add(Box.createVerticalStrut(12));

        // Timetable container (full grey box)
        JPanel tableContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 60, 60));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        tableContainer.setOpaque(false);
        tableContainer.setPreferredSize(new Dimension(1000, 240));
        tableContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        tableContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTable table = new JTable();
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(60, 60, 60));
        table.setSelectionBackground(new Color(80, 80, 80));
        table.setRowHeight(28);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Course", "Day/Time", "Room"}, 0);
        for (Object[] r : registered) model.addRow(new Object[]{r[0] + " - " + r[1], r[2], r[3]});
        table.setModel(model);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setOpaque(false);
        tableScroll.setOpaque(false);

        tableContainer.add(tableScroll, BorderLayout.CENTER);
        body.add(tableContainer);
        body.add(Box.createVerticalStrut(46));

        // Action cards row (left aligned)
        JPanel actionRow = new JPanel();
        actionRow.setOpaque(false);
        actionRow.setLayout(new BoxLayout(actionRow, BoxLayout.X_AXIS));
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        actionRow.add(makeActionCard("Register for Courses", this::openRegisterPage));
        actionRow.add(Box.createHorizontalStrut(60));
        actionRow.add(makeActionCard("Drop Course", this::openDropPage));
        actionRow.add(Box.createHorizontalStrut(60));
        actionRow.add(makeActionCard("View Grades", this::openGradesPage));

        body.add(actionRow);
        body.add(Box.createVerticalStrut(60));

        // add body to holder at WEST so it stays flush-left; center area remains flexible
        centerHolder.add(body, BorderLayout.WEST);

        // Add centerHolder to content
        content.add(centerHolder, BorderLayout.CENTER);

        // Footer (mint background) in SOUTH
        JPanel footer = new JPanel(new GridLayout(1, 3, 80, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (footerBG != null) g.drawImage(footerBG, 0, 0, getWidth(), getHeight(), this);
            }
        };
        footer.setPreferredSize(new Dimension(0, 180));
        footer.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        footer.setOpaque(false);
        footer.add(makeFooterColumn("Features", "Register for Courses", "Drop Course", "View Grades"));
        footer.add(makeFooterColumn("Support", "support@iiitd.ac.in"));
        footer.add(makeFooterColumn("Made By", "Parikshit Menon", "Aruhi Sharma"));

        content.add(footer, BorderLayout.SOUTH);

        // Wrap content in a scroll pane
        JScrollPane containerScroll = new JScrollPane(content);
        containerScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        containerScroll.getVerticalScrollBar().setUnitIncrement(20);

        // Ensure content width follows viewport width (keeps left flush)
        containerScroll.getViewport().addChangeListener(evt -> {
            JViewport vp = (JViewport) evt.getSource();
            Dimension viewSize = content.getPreferredSize();
            int w = vp.getWidth();
            if (w > viewSize.width) {
                content.setPreferredSize(new Dimension(w, viewSize.height));
                content.revalidate();
            }
        });

        setContentPane(containerScroll);
        setVisible(true);
    }

    // COMPONENTS

    private JPanel makeCourseBox(String code) {
        return new JPanel() {
            {
                setPreferredSize(new Dimension(130, 42));
                setMaximumSize(new Dimension(130, 42));
                setLayout(new GridBagLayout());
                setOpaque(false);
                JLabel l = new JLabel(code);
                l.setFont(new Font("Segoe UI", Font.BOLD, 15));
                l.setForeground(Color.WHITE);
                add(l);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 60, 60));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
    }

    private JPanel makeActionCard(String title, Runnable action) {
        return new JPanel() {
            {
                setPreferredSize(new Dimension(330, 200));
                setMaximumSize(new Dimension(330, 200));
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                setOpaque(false);

                JLabel head = new JLabel(title);
                head.setFont(new Font("Segoe UI", Font.BOLD, 22));
                head.setForeground(Color.WHITE);
                head.setAlignmentX(Component.CENTER_ALIGNMENT);

                JButton go = new JButton("Go");
                go.setFont(new Font("Segoe UI", Font.BOLD, 16));
                go.setMaximumSize(new Dimension(290, 40));
                go.setAlignmentX(Component.CENTER_ALIGNMENT);
                go.addActionListener(e -> action.run());

                add(Box.createVerticalGlue());
                add(head);
                add(Box.createVerticalStrut(16));
                add(go);
                add(Box.createVerticalGlue());
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Shape round = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setClip(round);
                if (mintBG != null) g2.drawImage(mintBG, 0, 0, getWidth(), getHeight(), this);
                g2.setClip(null);

                g2.setColor(Color.BLACK);
                g2.draw(round);
                g2.dispose();
            }
        };
    }

    private JPanel makeFooterColumn(String title, String... lines) {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);

        JLabel head = new JLabel(title);
        head.setFont(new Font("Segoe UI", Font.BOLD, 20));
        head.setForeground(Color.WHITE);
        head.setAlignmentX(Component.LEFT_ALIGNMENT);
        col.add(head);
        col.add(Box.createVerticalStrut(10));

        for (String line : lines) {
            JLabel l = new JLabel(line);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            l.setForeground(Color.WHITE);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            col.add(l);
            col.add(Box.createVerticalStrut(6));
        }
        return col;
    }

    // NAV
    private void openRegisterPage() {
        new CourseRegistrationWindow().setVisible(true);
    }

    private void openDropPage() {
        new CourseRegistrationWindow().setVisible(true);
    }

    private void openGradesPage() {
        int studentId = SessionManager.getInstance().getCurrentUser().getUserId();
        new StudentGradesWindow(studentId).setVisible(true);
    }

}
