package edu.univ.erp.data;

import edu.univ.erp.auth.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InitialDataSeeder {

    // Default passwords for sample accounts (to be hashed)
    private static final String ADMIN_PASS = "adminpass";
    private static final String INST_PASS = "instpass";
    private static final String STU1_PASS = "stu1pass";
    private static final String STU2_PASS = "stu2pass";

    // Hashed passwords (pre-calculated for simplicity and consistency)
    private static final String ADMIN_HASH = PasswordUtil.hashPassword(ADMIN_PASS);
    private static final String INST_HASH = PasswordUtil.hashPassword(INST_PASS);
    private static final String STU1_HASH = PasswordUtil.hashPassword(STU1_PASS);
    private static final String STU2_HASH = PasswordUtil.hashPassword(STU2_PASS);

    public static void seed() {
        // Unique IDs for linking the two DBs. We use explicit IDs for clear linking.
        int adminId = 100; // Starting at a higher number for clarity
        int inst1Id = 101;
        int stu1Id = 201;
        int stu2Id = 202;

        System.out.println("Starting data seeding...");

        try (Connection authConn = DatabaseManager.getAuthConnection();
             Connection erpConn = DatabaseManager.getERPConnection()) {

            // Important: Enable transactions for safety
            authConn.setAutoCommit(false);
            erpConn.setAutoCommit(false);

            // --- 1. Clear existing data for safe re-seeding ---
            clearTables(authConn, erpConn);

            // --- 2. Auth DB Inserts ---
            insertAuthUser(authConn, adminId, "admin1", "Admin", ADMIN_HASH);
            insertAuthUser(authConn, inst1Id, "inst1", "Instructor", INST_HASH);
            insertAuthUser(authConn, stu1Id, "stu1", "Student", STU1_HASH);
            insertAuthUser(authConn, stu2Id, "stu2", "Student", STU2_HASH);
            authConn.commit();

            // --- 3. ERP DB Inserts ---
            // Instructors
            insertInstructorProfile(erpConn, inst1Id, "Computer Science");

            // Students
            insertStudentProfile(erpConn, stu1Id, "2024CS001", "B.Tech CS", 1);
            insertStudentProfile(erpConn, stu2Id, "2024IT002", "B.Tech IT", 1);

            // Courses
            int cs101 = insertCourse(erpConn, "CS101", "Intro to Programming", 4);
            int ma203 = insertCourse(erpConn, "MA203", "Discrete Math", 3);

            // Sections (inst1 teaches CS101)
            int sec1 = insertSection(erpConn, cs101, inst1Id, "Mon/Wed 10:00-11:30", "LH-101", 30, "Fall", 2024);
            int sec2 = insertSection(erpConn, ma203, null, "Tue/Thu 14:00-15:30", "LH-205", 40, "Fall", 2024);

            // Enrollments (Returns the AUTO_INCREMENT ID)
            int enrollment1 = insertEnrollment(erpConn, stu1Id, sec1, "REGISTERED"); // Stu1 registers CS101
            int enrollment2 = insertEnrollment(erpConn, stu2Id, sec1, "REGISTERED"); // Stu2 registers CS101
            insertEnrollment(erpConn, stu1Id, sec2, "REGISTERED"); // Stu1 registers MA203

            // Grades (Partial grades for CS101 for stu1)
            insertGrade(erpConn, enrollment1, "Quiz 1", 18.0);
            insertGrade(erpConn, enrollment1, "Midterm", 85.5);

            erpConn.commit();

            System.out.println("✅ Initial data seeding successful! Accounts are ready to use.");
            System.out.println("   Admin: admin1 / adminpass | Instructor: inst1 / instpass");
            System.out.println("   Student 1: stu1 / stu1pass | Student 2: stu2 / stu2pass");

        } catch (SQLException e) {
            System.err.println("❌ Database seeding failed! Check DatabaseManager password and server status.");
            e.printStackTrace();
        }
    }

    private static void clearTables(Connection authConn, Connection erpConn) throws SQLException {
        // Clearing ERP tables (reverse dependency order)
        try (Statement st = erpConn.createStatement()) {
            st.executeUpdate("DELETE FROM grades");
            st.executeUpdate("DELETE FROM enrollments");
            st.executeUpdate("DELETE FROM sections");
            st.executeUpdate("DELETE FROM courses");
            st.executeUpdate("DELETE FROM students");
            st.executeUpdate("DELETE FROM instructors");
            st.executeUpdate("UPDATE settings SET setting_value = 'false' WHERE setting_key = 'maintenance_on'");
            erpConn.commit();
        }

        // Clearing Auth table
        try (Statement st = authConn.createStatement()) {
            st.executeUpdate("DELETE FROM users_auth");
            // Optional: Reset AUTO_INCREMENT (specific to MySQL)
            st.executeUpdate("ALTER TABLE users_auth AUTO_INCREMENT = 1");
            authConn.commit();
        }
    }

    private static void insertAuthUser(Connection conn, int id, String username, String role, String hash) throws SQLException {
        String sql = "INSERT INTO users_auth (user_id, username, role, password_hash) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, username);
            ps.setString(3, role);
            ps.setString(4, hash);
            ps.executeUpdate();
        }
    }

    private static void insertStudentProfile(Connection conn, int id, String rollNo, String program, int year) throws SQLException {
        String sql = "INSERT INTO students (student_id, roll_no, program, year) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, rollNo);
            ps.setString(3, program);
            ps.setInt(4, year);
            ps.executeUpdate();
        }
    }

    private static void insertInstructorProfile(Connection conn, int id, String department) throws SQLException {
        String sql = "INSERT INTO instructors (instructor_id, department) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, department);
            ps.executeUpdate();
        }
    }

    private static int insertCourse(Connection conn, String code, String title, int credits) throws SQLException {
        String sql = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            ps.setString(2, title);
            ps.setInt(3, credits);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to retrieve course ID.");
    }

    private static int insertSection(Connection conn, int courseId, Integer instructorId, String dayTime, String room, int capacity, String semester, int year) throws SQLException {
        String sql = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, courseId);
            // Use setInt for instructorId, handling null if needed
            if (instructorId != null) {
                ps.setInt(2, instructorId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setString(3, dayTime);
            ps.setString(4, room);
            ps.setInt(5, capacity);
            ps.setString(6, semester);
            ps.setInt(7, year);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to retrieve section ID.");
    }

    private static int insertEnrollment(Connection conn, int studentId, int sectionId, String status) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ps.setString(3, status);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to retrieve enrollment ID.");
    }

    private static void insertGrade(Connection conn, int enrollmentId, String component, double score) throws SQLException {
        String sql = "INSERT INTO grades (enrollment_id, component, score) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setString(2, component);
            ps.setDouble(3, score);
            ps.executeUpdate();
        }
    }
}
