package edu.univ.erp.data.DAOs;

import edu.univ.erp.data.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsDAO {

    /**
     * Retrieves a setting value by key from the settings table.
     * Uses columns: setting_key, setting_value
     */
    public String getSettingValue(String key) {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = ?";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, key);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("setting_value");
                }
            }
        } catch (SQLException e) {
            System.err.println("DB Error retrieving setting '" + key + "': " + e.getMessage());
        }
        return "";
    }

    /**
     * Inserts or updates a setting in the settings table.
     */
    public void setSettingValue(String key, String value) {
        String sql = "INSERT INTO settings (setting_key, setting_value) VALUES (?, ?) ON DUPLICATE KEY UPDATE setting_value = ?";
        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.setString(3, value);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DB Error updating setting '" + key + "': " + e.getMessage());
        }
    }
}
