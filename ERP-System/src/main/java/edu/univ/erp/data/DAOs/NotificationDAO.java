package edu.univ.erp.data.DAOs;

import edu.univ.erp.data.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public List<String> getNotificationsForRole(String role) {
        List<String> messages = new ArrayList<>();
        // Fetch messages for specific role OR 'All'
        String sql = "SELECT message FROM notifications WHERE target_role = ? OR target_role = 'All' ORDER BY created_at DESC";

        try (Connection conn = DatabaseManager.getERPConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role); // e.g., "Student"

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(rs.getString("message"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }
}