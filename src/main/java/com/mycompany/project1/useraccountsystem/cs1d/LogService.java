/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.project1.useraccountsystem.cs1d;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author hezen
 */
public class LogService {
    public static void updateTimeOut() {
        int logId = Session.getLogId();
        
        // If logId is 0, no session was started (avoids database errors)
        if (logId <= 0) return; 

        String sql = "UPDATE user_logs SET time_out = NOW() WHERE log_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, logId);
            ps.executeUpdate();
            
            // Clear session data
            Session.setLogId(0); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}