package edu.univ.erp.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    // CRITICAL: Replace 'YOUR_ROOT_PASSWORD_HERE' with your actual MySQL root password.
    // The driver is now set to 'mariadb' to match the dependency in pom.xml.
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Aruhi@2020";

    private static final HikariDataSource AUTH_DATA_SOURCE;
    private static final HikariDataSource ERP_DATA_SOURCE;

    static {
        try {
            // --- 1. AUTH DB Configuration ---
            HikariConfig authConfig = new HikariConfig();
            authConfig.setJdbcUrl("jdbc:mariadb://localhost:3306/university_auth_db");
            authConfig.setUsername(DB_USER);
            authConfig.setPassword(DB_PASSWORD);
            authConfig.setMaximumPoolSize(5);
            AUTH_DATA_SOURCE = new HikariDataSource(authConfig);

            // --- 2. ERP DB Configuration ---
            HikariConfig erpConfig = new HikariConfig();
            erpConfig.setJdbcUrl("jdbc:mariadb://localhost:3306/university_erp_db");
            erpConfig.setUsername(DB_USER);
            erpConfig.setPassword(DB_PASSWORD);
            erpConfig.setMaximumPoolSize(10);
            ERP_DATA_SOURCE = new HikariDataSource(erpConfig);

        } catch (Exception e) {
            System.err.println("FATAL: Failed to initialize database connection pools. Check MySQL server status and credentials.");
            throw new RuntimeException("Database Initialization Failed", e);
        }
    }

    // Method to get a connection for the security database
    public static Connection getAuthConnection() throws SQLException {
        return AUTH_DATA_SOURCE.getConnection();
    }

    // Method to get a connection for the main data database
    public static Connection getERPConnection() throws SQLException {
        return ERP_DATA_SOURCE.getConnection();
    }

    // Simple method to test if connections can be established
    public static void testConnections() {
        try (Connection authConn = getAuthConnection();
             Connection erpConn = getERPConnection()) {
            if (authConn != null && erpConn != null) {
                System.out.println("✅ Database connections established successfully.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed. Check server status and DatabaseManager credentials.");
            e.printStackTrace();
            throw new RuntimeException("Connection Test Failed");
        }
    }
}