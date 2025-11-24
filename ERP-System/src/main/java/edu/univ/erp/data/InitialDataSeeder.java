package edu.univ.erp.data;

import edu.univ.erp.auth.PasswordUtil;

import java.sql.*;
import java.util.*;

/**
 * Initializes both authentication and ERP databases with base data.
 * Run once via SeederRunner.main().
 */
public class InitialDataSeeder {

    private static final String ADMIN_PASS = "adminpass";
    private static final String INST_PASS  = "instpass";
    private static final String STU1_PASS  = "stu1pass";
    private static final String STU2_PASS  = "stu2pass";

    private static final String ADMIN_HASH = PasswordUtil.hashPassword(ADMIN_PASS);
    private static final String INST_HASH  = PasswordUtil.hashPassword(INST_PASS);
    private static final String STU1_HASH  = PasswordUtil.hashPassword(STU1_PASS);
    private static final String STU2_HASH  = PasswordUtil.hashPassword(STU2_PASS);

    private static final String[] DEPARTMENTS = {"CSE", "MTH", "DES", "BIO"};

    private static final LinkedHashMap<String,String> EXTRACTED_COURSES = new LinkedHashMap<>();

    static {
        EXTRACTED_COURSES.put("DES537A", "User Experience Design and UI Design");
        EXTRACTED_COURSES.put("BIO101", "Foundations of Biology");
        EXTRACTED_COURSES.put("BIO102", "Foundations of Biology-I");
        EXTRACTED_COURSES.put("BIO201", "Foundations of Biology II");

        EXTRACTED_COURSES.put("BIO546", "Computing for Medicine");
        EXTRACTED_COURSES.put("BIO5XX", "Computational Methods in Oncology Research");
        EXTRACTED_COURSES.put("CSE101", "Introduction to Programming");
        EXTRACTED_COURSES.put("CSE102", "Data Structures & Algorithms");
        EXTRACTED_COURSES.put("CSE112", "Computer Organization");
        EXTRACTED_COURSES.put("CSE121", "Discrete Mathematics");
        EXTRACTED_COURSES.put("CSE140", "Introduction to Intelligent Systems");
        EXTRACTED_COURSES.put("CSE201", "Advanced Programming");

        EXTRACTED_COURSES.put("CSE340", "Digital Image Processing");
        EXTRACTED_COURSES.put("CSE344", "Computer Vision");
        EXTRACTED_COURSES.put("CSE511", "Computer Architecture");
        EXTRACTED_COURSES.put("CSE515", "Bayesian Machine Learning");
        EXTRACTED_COURSES.put("CSE516", "Theories of Deep Learning");
        EXTRACTED_COURSES.put("CSE538", "Wireless Networks");
        EXTRACTED_COURSES.put("CSE553", "Networks and Systems Security I");
        EXTRACTED_COURSES.put("CSE600A", "Object Oriented Programming and Design");
        EXTRACTED_COURSES.put("CSE633", "Robotics");
        EXTRACTED_COURSES.put("CSE636", "Communication Networks");
        EXTRACTED_COURSES.put("CSE694F", "Multimedia Security");

        EXTRACTED_COURSES.put("MTH100", "Linear Algebra");
        EXTRACTED_COURSES.put("MTH201", "Probability and Statistics");
        EXTRACTED_COURSES.put("MTH203", "Multivariate Calculus");
        EXTRACTED_COURSES.put("MTH204", "Mathematics IV");
        EXTRACTED_COURSES.put("MTH210", "Discrete Structures");
        EXTRACTED_COURSES.put("MTH211", "Number Theory");
        EXTRACTED_COURSES.put("MTH212", "Abstract Algebra I");
        EXTRACTED_COURSES.put("MTH240", "Real Analysis I");
        EXTRACTED_COURSES.put("MTH270", "Numerical Methods");
        EXTRACTED_COURSES.put("MTH300", "Introduction to Mathematical Logic");
        EXTRACTED_COURSES.put("MTH302", "Algebra");
        EXTRACTED_COURSES.put("MTH310", "Graph Theory");
        EXTRACTED_COURSES.put("MTH311", "Combinatorics and its Applications");
        EXTRACTED_COURSES.put("MTH340", "Real Analysis-II");
    }

    private static final int DEFAULT_CAPACITY = 30;
    private static final String DEFAULT_SEMESTER = "Monsoon";
    private static final int DEFAULT_YEAR = 2025;

    private static final Random RNG = new Random();

    public static void seed() {
        int adminId = 100;
        int instBaseId = 300;
        int stu1Id  = 201;
        int stu2Id  = 202;

        System.out.println("Starting data seeding...");

        try (Connection authConn = DatabaseManager.getAuthConnection();
             Connection erpConn  = DatabaseManager.getERPConnection()) {

            authConn.setAutoCommit(false);
            erpConn.setAutoCommit(false);

            clearTables(authConn, erpConn);

            insertAuthUser(authConn, adminId, "admin1", "Admin", ADMIN_HASH);
            insertAuthUser(authConn, stu1Id,  "stu1",  "Student",   STU1_HASH);
            insertAuthUser(authConn, stu2Id,  "stu2",  "Student",   STU2_HASH);
            authConn.commit();

            Map<String,Integer> deptInstructorId = new HashMap<>();
            int nextInstId = instBaseId;
            List<String> instructorUsernames = new ArrayList<>();

            for (String dept : DEPARTMENTS) {
                String username = "inst_" + dept.toLowerCase();
                int instId = nextInstId++;
                insertAuthUser(authConn, instId, username, "Instructor", INST_HASH);
                deptInstructorId.put(dept, instId);
                instructorUsernames.add(username);
            }
            authConn.commit();

            for (String dept : DEPARTMENTS) {
                insertInstructorProfile(erpConn, deptInstructorId.get(dept), dept);
            }

            insertStudentProfile(erpConn, stu1Id, "2024CS001", "B.Tech CS", 1, "Student One");
            insertStudentProfile(erpConn, stu2Id, "2024IT002", "B.Tech IT", 1, "Student Two");

            Map<String,Integer> courseCodeToId = seedCourses(erpConn);
            Map<Integer,List<Integer>> courseSections = createSectionsForCourses(erpConn, courseCodeToId, deptInstructorId);

            enrollTwoStudentsDistinct(erpConn, stu1Id, stu2Id, courseSections, 5);

            erpConn.commit();

            // Print usernames and passwords
            printUserCredentials(instructorUsernames);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Map<String,Integer> seedCourses(Connection conn) throws SQLException {
        String sql =
                "INSERT INTO courses (code,title,credits) VALUES (?,?,?) " +
                        "ON DUPLICATE KEY UPDATE title=VALUES(title), credits=VALUES(credits)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (var e : EXTRACTED_COURSES.entrySet()) {
                ps.setString(1, e.getKey());
                ps.setString(2, e.getValue());
                ps.setInt(3, creditsForCode(e.getKey()));
                ps.executeUpdate();
            }
        }

        Map<String,Integer> map = new HashMap<>();
        String fetch = "SELECT course_id,code FROM courses";

        try (PreparedStatement ps = conn.prepareStatement(fetch);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) map.put(rs.getString("code"), rs.getInt("course_id"));
        }

        return map;
    }

    private static Map<Integer,List<Integer>> createSectionsForCourses(
            Connection conn,
            Map<String,Integer> courseCodeToId,
            Map<String,Integer> deptInstructorId
    ) throws SQLException {

        Map<Integer,List<Integer>> result = new HashMap<>();

        for (var entry : courseCodeToId.entrySet()) {

            String code = entry.getKey();
            int courseId = entry.getValue();

            String prefix = extractPrefix(code);
            Integer numeric = extractNumeric(code);

            // ----------------------------
            // SECTION COUNT LOGIC
            // ----------------------------
            int sections = 1;
            if (numeric != null && numeric < 300)
                sections = 2;

            // ----------------------------
            // CAPACITY LOGIC
            // ----------------------------
            int capacity;
            if (numeric != null && numeric >= 500)
                capacity = 30;
            else if (prefix.equals("DES") || prefix.equals("BIO"))
                capacity = 30;
            else if (numeric != null && numeric < 300)
                capacity = 30;   // 2 sections * 30 each → total 60
            else
                capacity = DEFAULT_CAPACITY;

            // Match course prefix to department instructor
            Integer instructorId = deptInstructorId.get(prefix);

            List<Integer> secIds = new ArrayList<>();

            for (int s = 0; s < sections; s++) {
                String sectionName = (s == 0 ? "SecA" : "SecB");
                String schedule    = randomSchedule();

                int secId = insertSection(
                        conn,
                        courseId,
                        instructorId,
                        sectionName,
                        schedule,
                        "LH-" + (100 + RNG.nextInt(50)),
                        capacity,
                        DEFAULT_SEMESTER,
                        DEFAULT_YEAR
                );

                secIds.add(secId);
            }

            result.put(courseId, secIds);
        }

        return result;
    }

    private static int insertSection(Connection conn,
                                     int courseId,
                                     Integer instructorId,
                                     String sectionName,
                                     String dayTime,
                                     String room,
                                     int capacity,
                                     String semester,
                                     int year) throws SQLException {

        String sql =
                "INSERT INTO sections (section_name,course_id,instructor_id,day_time,room,capacity,semester,year) " +
                        "VALUES (?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, sectionName);
            ps.setInt(2, courseId);

            if (instructorId != null) ps.setInt(3, instructorId);
            else ps.setNull(3, Types.INTEGER);

            ps.setString(4, dayTime);
            ps.setString(5, room);
            ps.setInt(6, capacity);
            ps.setString(7, semester);
            ps.setInt(8, year);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        throw new SQLException("Failed to retrieve section ID.");
    }

    private static void enrollTwoStudentsDistinct(
            Connection conn,
            int stu1Id,
            int stu2Id,
            Map<Integer,List<Integer>> sectionMap,
            int count
    ) throws SQLException {

        List<Integer> allSections = new ArrayList<>();
        for (List<Integer> list : sectionMap.values()) allSections.addAll(list);

        Collections.shuffle(allSections, RNG);

        Set<Integer> stu1 = new HashSet<>(allSections.subList(0, Math.min(count, allSections.size())));
        Set<Integer> stu2 = new HashSet<>();

        int index = stu1.size();

        while (stu2.size() < stu1.size() && index < allSections.size()) {
            int id = allSections.get(index++);
            if (!stu1.contains(id)) stu2.add(id);
        }

        for (int sec : stu1) insertEnrollment(conn, stu1Id, sec, "REGISTERED");
        for (int sec : stu2) insertEnrollment(conn, stu2Id, sec, "REGISTERED");
    }

    private static int creditsForCode(String code) {
        // All courses default to 4 credits
        return 4;
    }

    private static String extractPrefix(String code) {
        StringBuilder sb = new StringBuilder();
        for (char c : code.toCharArray()) {
            if (Character.isLetter(c)) sb.append(c);
            else break;
        }
        return sb.toString().toUpperCase();
    }

    private static Integer extractNumeric(String code) {
        StringBuilder sb = new StringBuilder();
        for (char c : code.toCharArray()) {
            if (Character.isDigit(c)) sb.append(c);
            else if (sb.length() > 0) break;
        }
        if (sb.length() == 0) return null;
        return Integer.parseInt(sb.toString());
    }

    private static String randomSchedule() {
        String[] days = {"Mon","Tue","Wed","Thu","Fri"};
        List<String> d = new ArrayList<>(Arrays.asList(days));
        Collections.shuffle(d, RNG);

        int hour = 8 + RNG.nextInt(9);
        int min  = RNG.nextBoolean() ? 0 : 30;

        int endH = hour + 1 + ((min + 30 >= 60) ? 1 : 0);
        int endM = (min + 30) % 60;

        return d.get(0) + "/" + d.get(1) + " " +
                String.format("%02d:%02d", hour, min) + "-" +
                String.format("%02d:%02d", endH, endM);
    }

    private static void clearTables(Connection authConn, Connection erpConn) throws SQLException {
        try (Statement st = erpConn.createStatement()) {
            st.executeUpdate("DELETE FROM grades");
            st.executeUpdate("DELETE FROM enrollments");
            st.executeUpdate("DELETE FROM sections");
            st.executeUpdate("DELETE FROM courses");
            st.executeUpdate("DELETE FROM students");
            st.executeUpdate("DELETE FROM instructors");
            st.executeUpdate("UPDATE settings SET setting_value='false' WHERE setting_key='maintenance_on'");
            erpConn.commit();
        }

        try (Statement st = authConn.createStatement()) {
            st.executeUpdate("DELETE FROM users_auth");
            st.executeUpdate("ALTER TABLE users_auth AUTO_INCREMENT=1");
            authConn.commit();
        }
    }

    private static void insertAuthUser(Connection conn, int id, String username, String role, String hash) throws SQLException {
        String sql = "INSERT INTO users_auth (user_id, username, role, password_hash) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, username);
            ps.setString(3, role);
            ps.setString(4, hash);
            ps.executeUpdate();
        }
    }

    private static void insertStudentProfile(Connection conn, int id, String roll, String program, int year, String fullName) throws SQLException {
        String sql = "INSERT INTO students (student_id, roll_no, program, year, full_name) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, roll);
            ps.setString(3, program);
            ps.setInt(4, year);
            ps.setString(5, fullName);
            ps.executeUpdate();
        }
    }

    private static void insertInstructorProfile(Connection conn, int id, String dept) throws SQLException {
        String sql = "INSERT INTO instructors (instructor_id, department) VALUES (?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, dept);
            ps.executeUpdate();
        }
    }

    private static int insertEnrollment(Connection conn, int studentId, int sectionId, String status) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ps.setString(3, status);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Enrollment insert failed");
    }

    private static void insertGrade(Connection conn, int enrollmentId, String comp, double score) throws SQLException {
        String sql = "INSERT INTO grades (enrollment_id, component, score) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            ps.setString(2, comp);
            ps.setDouble(3, score);
            ps.executeUpdate();
        }
    }

    private static void printUserCredentials(List<String> instructorUsernames) {
        System.out.println("\n=== User Credentials ===");
        System.out.println("Admin:");
        System.out.println("  Username: admin1");
        System.out.println("  Password: adminpass");
        
        System.out.println("\nStudents:");
        System.out.println("  Username: stu1");
        System.out.println("  Password: stu1pass");
        System.out.println("  Username: stu2");
        System.out.println("  Password: stu2pass");
        
        System.out.println("\nInstructors:");
        for (String username : instructorUsernames) {
            System.out.println("  Username: " + username);
            System.out.println("  Password: instpass");
        }
        System.out.println("========================\n");
    }
}
