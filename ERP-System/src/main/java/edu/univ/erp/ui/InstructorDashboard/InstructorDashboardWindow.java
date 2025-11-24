package edu.univ.erp.ui.InstructorDashboard;

import edu.univ.erp.domain.InstructorSectionRow;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.ui.ERPSortableTable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class InstructorDashboardWindow extends JFrame {

    private final InstructorService instructorService = new InstructorService();
    private BufferedImage mintBG;

    private ERPSortableTable sectionsTable;
    private static final int SECTION_ID_COLUMN_INDEX = 5;

    public InstructorDashboardWindow(String username) {

        // load hero/footer background
        try { mintBG = ImageIO.read(new File("src/main/resources/gradient-mint-background.jpg")); }
        catch (Exception ignored) {}

        setTitle("Instructor Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 900);
        setLocationRelativeTo(null);

        // ROOT: BorderLayout (same as student)
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);

        // ============================================================
        // HERO (NORTH)
        // ============================================================
        JPanel hero = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (mintBG != null)
                    g.drawImage(mintBG, 0, 0, getWidth(), getHeight(), this);
            }
        };
        hero.setPreferredSize(new Dimension(0, 180));
        hero.setLayout(new BorderLayout());
        hero.setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));

        JLabel title = new JLabel("Welcome " + username);
        title.setFont(new Font("Segoe UI", Font.BOLD, 40));
        title.setForeground(Color.WHITE);
        hero.add(title, BorderLayout.WEST);

        content.add(hero, BorderLayout.NORTH);

        // ============================================================
        // CENTER HOLDER (WEST-anchored body)
        // ============================================================
        JPanel centerHolder = new JPanel(new BorderLayout());
        centerHolder.setBackground(Color.WHITE);

        JPanel body = new JPanel();
        body.setBackground(Color.WHITE);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));  // match Student UI
        body.setAlignmentX(Component.LEFT_ALIGNMENT);

        // TITLE TEXT
        JLabel dash = new JLabel("Instructor Dashboard");
        dash.setFont(new Font("Segoe UI", Font.BOLD, 26));
        dash.setForeground(Color.BLACK);
        dash.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(dash);

        body.add(Box.createVerticalStrut(10));

        JLabel sub = new JLabel("Your assigned sections");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        sub.setForeground(Color.BLACK);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(sub);

        body.add(Box.createVerticalStrut(24));

        // ============================================================
        // COURSE BOX COLUMN (VERTICAL LIST)
        // ============================================================
        JPanel leftWrapper = new JPanel();
        leftWrapper.setLayout(new BoxLayout(leftWrapper, BoxLayout.Y_AXIS));
        leftWrapper.setOpaque(false);

        // Load cards
        List<InstructorSectionRow> rows = instructorService.getMySections();
        if (rows != null) {
            for (InstructorSectionRow r : rows) {

                // ---- NEW: Symmetric left-right padding container ----
                JPanel cardRow = new JPanel(new BorderLayout());
                cardRow.setOpaque(false);
                cardRow.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40)); // SAME L/R PADDING

                cardRow.add(createCourseCard(r), BorderLayout.CENTER);

                leftWrapper.add(cardRow);
                leftWrapper.add(Box.createVerticalStrut(30));
            }
        }

        body.add(leftWrapper);
        body.add(Box.createVerticalStrut(60));

        // anchor body to WEST so no centering ever happens
        centerHolder.add(body, BorderLayout.WEST);
        content.add(centerHolder, BorderLayout.CENTER);

        // ============================================================
        // FOOTER (SOUTH)
        // ============================================================
        JPanel footer = new JPanel(new GridLayout(1, 3, 80, 10)) {
            @Override protected void paintComponent(Graphics g) {
                if (mintBG != null)
                    g.drawImage(mintBG, 0, 0, getWidth(), getHeight(), this);
            }
        };
        footer.setOpaque(false);
        footer.setPreferredSize(new Dimension(0, 180));
        footer.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        footer.add(makeFooterColumn("Features", "Open Gradebook", "Compute Final Grades"));
        footer.add(makeFooterColumn("Support", "support@iiitd.ac.in"));
        footer.add(makeFooterColumn("Made By", "Parikshit Menon", "Aruhi Sharma"));

        content.add(footer, BorderLayout.SOUTH);

        // ============================================================
        // SCROLLPANE WRAPPER (LEFT-ANCHOR FIX)
        // ============================================================
        JScrollPane containerScroll = new JScrollPane(content);
        containerScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        containerScroll.getVerticalScrollBar().setUnitIncrement(20);

        containerScroll.getViewport().addChangeListener(evt -> {
            JViewport vp = (JViewport) evt.getSource();
            Dimension pref = content.getPreferredSize();
            int w = vp.getWidth();
            if (w > pref.width) {
                content.setPreferredSize(new Dimension(w, pref.height));
                content.revalidate();
            }
        });

        setContentPane(containerScroll);

        setVisible(true);
    }

    // ============================================================
    // COURSE CARD UI
    // ============================================================
    private JPanel createCourseCard(InstructorSectionRow row) {

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (mintBG != null)
                    g2.drawImage(mintBG, 0, 0, getWidth(), getHeight(), null);

                g2.dispose();
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(1000, 280));
        card.setMaximumSize(new Dimension(2000, 280));
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel semester = new JLabel("Semester - Monsoon");
        semester.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        semester.setForeground(Color.WHITE);
        semester.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel code = new JLabel(row.getCourseCode());
        code.setFont(new Font("Segoe UI", Font.BOLD, 16));
        code.setForeground(Color.WHITE);
        code.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel name = new JLabel(row.getCourseTitle());
        name.setFont(new Font("Segoe UI", Font.BOLD, 26));
        name.setForeground(Color.WHITE);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel enroll = new JLabel(row.getEnrolledCount() + " / " + row.getCapacity());
        enroll.setFont(new Font("Segoe UI", Font.BOLD, 28));
        enroll.setForeground(Color.WHITE);
        enroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton open = new JButton("OPEN GRADEBOOK");
        open.setFont(new Font("Segoe UI", Font.BOLD, 15));
        open.setMaximumSize(new Dimension(250, 40));
        open.setAlignmentX(Component.LEFT_ALIGNMENT);

        open.addActionListener(e -> {
            int sectionId = row.getSectionId();
            new GradebookWindow(sectionId).setVisible(true);
        });


        card.add(semester);
        card.add(Box.createVerticalStrut(5));
        card.add(code);
        card.add(Box.createVerticalStrut(12));
        card.add(name);
        card.add(Box.createVerticalStrut(18));
        card.add(enroll);
        card.add(Box.createVerticalStrut(25));
        card.add(open);

        return card;
    }

    // ============================================================
    // FOOTER COLUMN BUILDER
    // ============================================================
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
