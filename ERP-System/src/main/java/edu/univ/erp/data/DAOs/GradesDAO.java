package edu.univ.erp.data.DAOs;

import edu.univ.erp.data.DatabaseManager;

import java.sql.*;
import java.util.*;

/**
 * GradesDAO - manages grade rows and final computation.
 *
 * Schema (expected):
 * grades(grade_id, enrollment_id, component, score, total, percentage, final)
 *
 * Component rows contain score/total/percentage. Final is stored in the same table in
 * the 'final' column on a row with component='__FINAL__' (one per enrollment).
 */
public class GradesDAO {

    public static class StudentInfo {
        public final int enrollmentId;
        public final int studentId;
        public final String rollNo;
        public final String fullName;

        public StudentInfo(int enrollmentId, int studentId, String rollNo, String fullName) {
            this.enrollmentId = enrollmentId;
            this.studentId = studentId;
            this.rollNo = rollNo;
            this.fullName = fullName;
        }
    }

    public static class GradeCell {
        public final Double score;
        public final Double total;
        public final Double percentage;
        public final Double finalValue;

        public GradeCell(Double score, Double total, Double percentage, Double finalValue) {
            this.score = score;
            this.total = total;
            this.percentage = percentage;
            this.finalValue = finalValue;
        }
    }

    // -------------------------
    // Students in a section
    // -------------------------
    public List<StudentInfo> getStudentsInSection(int sectionId) {
        List<StudentInfo> out = new ArrayList<>();
        String sql = """
                SELECT e.enrollment_id, s.student_id, s.roll_no,
                       COALESCE(s.full_name, s.roll_no) AS fullname
                FROM enrollments e
                JOIN students s ON e.student_id = s.student_id
                WHERE e.section_id = ?
                ORDER BY s.roll_no
                """;
        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new StudentInfo(
                            rs.getInt("enrollment_id"),
                            rs.getInt("student_id"),
                            rs.getString("roll_no"),
                            rs.getString("fullname")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    // -------------------------
    // Components present for section (ordered by creation order)
    // -------------------------
    public LinkedHashSet<String> getComponentsForSection(int sectionId) {
        LinkedHashSet<String> comps = new LinkedHashSet<>();
        String sql = """
                SELECT g.component, MIN(g.grade_id) AS first_id
                FROM grades g
                JOIN enrollments e ON g.enrollment_id = e.enrollment_id
                WHERE e.section_id = ? AND g.component != '__FINAL__'
                GROUP BY g.component
                ORDER BY first_id
                """;
        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) comps.add(rs.getString("component"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comps;
    }

    // -------------------------
    // Fetch all grades for section: component -> (enrollmentId -> GradeCell)
    // -------------------------
    public LinkedHashMap<String, LinkedHashMap<Integer, GradeCell>> getGradesForSection(int sectionId) {
        LinkedHashMap<String, LinkedHashMap<Integer, GradeCell>> map = new LinkedHashMap<>();
        String sql = """
                SELECT g.enrollment_id, g.component, g.score, g.total, g.percentage, g.final
                FROM grades g
                JOIN enrollments e ON g.enrollment_id = e.enrollment_id
                WHERE e.section_id = ?
                ORDER BY g.grade_id
                """;
        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int eid = rs.getInt("enrollment_id");
                    String comp = rs.getString("component");
                    Double score = rs.getObject("score") == null ? null : rs.getDouble("score");
                    Double total = rs.getObject("total") == null ? null : rs.getDouble("total");
                    Double pct = rs.getObject("percentage") == null ? null : rs.getDouble("percentage");
                    Double finalVal = rs.getObject("final") == null ? null : rs.getDouble("final");

                    GradeCell cell = new GradeCell(score, total, pct, finalVal);
                    map.computeIfAbsent(comp, k -> new LinkedHashMap<>()).put(eid, cell);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    // -------------------------
    // Insert or update a grade (score + total), computes percentage and saves.
    // After save recomputes final for the section (requires UI to pass weights).
    // -------------------------
    public void insertOrUpdateGrade(int enrollmentId, String component, double score, double total) throws Exception {
        if (total <= 0) throw new IllegalArgumentException("Total must be > 0");
        double percentage = (score / total) * 100.0;
        if (percentage > 100.0) throw new IllegalArgumentException("Percentage cannot exceed 100");

        String updateSql = "UPDATE grades SET score = ?, total = ?, percentage = ? WHERE enrollment_id = ? AND component = ?";
        String insertSql = "INSERT INTO grades (enrollment_id, component, score, total, percentage) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getERPConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement upd = conn.prepareStatement(updateSql)) {
                upd.setDouble(1, score);
                upd.setDouble(2, total);
                upd.setDouble(3, percentage);
                upd.setInt(4, enrollmentId);
                upd.setString(5, component);
                int rows = upd.executeUpdate();
                if (rows == 0) {
                    try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                        ins.setInt(1, enrollmentId);
                        ins.setString(2, component);
                        ins.setDouble(3, score);
                        ins.setDouble(4, total);
                        ins.setDouble(5, percentage);
                        ins.executeUpdate();
                    }
                }
            }
            conn.commit();
        }
    }

    // -------------------------
    // Find enrollment id by roll and section
    // -------------------------
    public int getEnrollmentIdForRollInSection(String rollNo, int sectionId) {
        String sql = """
                SELECT e.enrollment_id
                FROM enrollments e
                JOIN students s ON e.student_id = s.student_id
                WHERE e.section_id = ? AND s.roll_no = ?
                LIMIT 1
                """;
        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ps.setString(2, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // -------------------------
    // Compute and store finals for section.
    // weights: Map<component, weightPercentage> - can be null (then compute simple average)
    // Final formula: sum(percentage(component) * weight/100). If weight map is null -> simple average.
    // -------------------------
    public void computeAndStoreFinalsForSection(int sectionId, Map<String, Double> weights) throws Exception {
        // get enrollment ids
        List<Integer> enrollmentIds = new ArrayList<>();
        String eSql = "SELECT enrollment_id FROM enrollments WHERE section_id = ?";
        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(eSql)) {
            ps.setInt(1, sectionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) enrollmentIds.add(rs.getInt(1));
            }
        }

        if (enrollmentIds.isEmpty()) return;

        // get grades organized
        LinkedHashMap<String, LinkedHashMap<Integer, GradeCell>> grades = getGradesForSection(sectionId);
        LinkedHashSet<String> comps = getComponentsForSection(sectionId);

        // If weights provided, ensure they include only existing components; components without weight are skipped.
        boolean useWeights = weights != null && !weights.isEmpty();

        // If weights provided but none map to existing components, fallback to simple average
        if (useWeights) {
            boolean anyMatch = false;
            for (String c : comps) {
                if (weights.containsKey(c)) { anyMatch = true; break; }
            }
            if (!anyMatch) useWeights = false;
        }

        // compute final for each enrollment
        for (int eid : enrollmentIds) {
            Double finalValue = null;

            if (useWeights) {
                double sum = 0.0;
                boolean any = false;
                for (String comp : comps) {
                    if (!weights.containsKey(comp)) continue;
                    Double w = weights.get(comp);
                    if (w == null) continue;
                    LinkedHashMap<Integer, GradeCell> compMap = grades.get(comp);
                    if (compMap == null) continue;
                    GradeCell gc = compMap.get(eid);
                    if (gc == null || gc.percentage == null) continue;
                    any = true;
                    sum += gc.percentage * (w / 100.0);
                }
                finalValue = any ? sum : null;
            } else {
                // simple average across available components
                double sum = 0.0;
                int count = 0;
                for (String comp : comps) {
                    LinkedHashMap<Integer, GradeCell> compMap = grades.get(comp);
                    if (compMap == null) continue;
                    GradeCell gc = compMap.get(eid);
                    if (gc == null || gc.percentage == null) continue;
                    sum += gc.percentage;
                    count++;
                }
                finalValue = (count == 0) ? null : (sum / count);
            }

            upsertFinal(eid, finalValue);
        }
    }

    // -------------------------
    // Upsert final row for an enrollment (component='__FINAL__')
    // -------------------------
    private void upsertFinal(int enrollmentId, Double finalValue) throws Exception {
        String findSql = "SELECT grade_id FROM grades WHERE enrollment_id = ? AND component = '__FINAL__' LIMIT 1";
        String insertSql = "INSERT INTO grades (enrollment_id, component, final) VALUES (?, '__FINAL__', ?)";
        String updateSql = "UPDATE grades SET final = ? WHERE grade_id = ?";

        try (Connection conn = DatabaseManager.getERPConnection()) {
            conn.setAutoCommit(false);
            int gid = -1;
            try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                ps.setInt(1, enrollmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) gid = rs.getInt(1);
                }
            }
            if (gid == -1) {
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setInt(1, enrollmentId);
                    if (finalValue == null) ps.setNull(2, Types.DOUBLE);
                    else ps.setDouble(2, finalValue);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    if (finalValue == null) ps.setNull(1, Types.DOUBLE);
                    else ps.setDouble(1, finalValue);
                    ps.setInt(2, gid);
                    ps.executeUpdate();
                }
            }
            conn.commit();
        }
    }

    // -------------------------
    // Get stored final for enrollment
    // -------------------------
    public Double getFinalForEnrollment(int enrollmentId) {
        String sql = "SELECT final FROM grades WHERE enrollment_id = ? AND component = '__FINAL__' LIMIT 1";
        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, enrollmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getObject("final") == null ? null : rs.getDouble("final");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getSectionName(int sectionId) {
        String sql =
                """
                SELECT section_name
                FROM sections
                WHERE section_id = ?
                """;

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String sec = rs.getString("section_name");
                    return (sec != null && !sec.isEmpty())
                            ? sec
                            : "Section " + sectionId;   // fallback
                }
            }

        } catch (Exception e) {
            System.err.println("Error fetching section_name: " + e.getMessage());
        }

        return "Section " + sectionId; // fallback
    }



}
