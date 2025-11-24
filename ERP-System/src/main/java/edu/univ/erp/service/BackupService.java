package edu.univ.erp.service;

import edu.univ.erp.data.DatabaseManager;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupService {

    // Tables to backup (Order matters for Foreign Keys!)
    private static final String[] TABLES = {
            "users_auth", "students", "instructors", "courses",
            "sections", "enrollments", "grades", "settings", "notifications"
    };

    public String performBackup(File folder) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File backupFile = new File(folder, "erp_backup_" + timestamp + ".sql");

        try (PrintWriter pw = new PrintWriter(backupFile);
             Connection conn = DatabaseManager.getERPConnection()) {

            pw.println("-- ERP BACKUP " + timestamp);
            pw.println("SET FOREIGN_KEY_CHECKS=0;"); // Disable checks for restore

            for (String table : TABLES) {
                exportTable(conn, table, pw);
            }

            pw.println("SET FOREIGN_KEY_CHECKS=1;");
            return "Backup saved to: " + backupFile.getName();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    private void exportTable(Connection conn, String tableName, PrintWriter pw) throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            pw.println("DELETE FROM " + tableName + ";");

            while (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("INSERT INTO ").append(tableName).append(" VALUES (");

                for (int i = 1; i <= colCount; i++) {
                    Object val = rs.getObject(i);
                    if (val == null) {
                        sb.append("NULL");
                    } else if (val instanceof Number) {
                        sb.append(val);
                    } else {
                        // Escape quotes for SQL
                        String str = val.toString().replace("'", "''");
                        sb.append("'").append(str).append("'");
                    }
                    if (i < colCount) sb.append(", ");
                }
                sb.append(");");
                pw.println(sb.toString());
            }
            pw.println();
        }
    }

    public String performRestore(File backupFile) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(backupFile));
             Connection conn = DatabaseManager.getERPConnection();
             Statement stmt = conn.createStatement()) {

            conn.setAutoCommit(false); // Transaction start

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Skip comments or empty lines
                if (line.isEmpty() || line.startsWith("--")) continue;

                sb.append(line);
                if (line.endsWith(";")) {
                    stmt.addBatch(sb.toString());
                    sb.setLength(0);
                }
            }
            stmt.executeBatch();
            conn.commit();
            return "Restore successful! Database updated.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Restore Failed: " + e.getMessage();
        }
    }
}