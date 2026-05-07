/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.project1.useraccountsystem.cs1d;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author 
 * renzz12345
 * DrAze
 */


public class LoginScreen extends javax.swing.JPanel {

    private javax.swing.JFrame parentFrame;
    private Image backgroundImage;

    public LoginScreen(JFrame frame) {
        this.parentFrame = frame;
            parentFrame.setSize(420, 530);
            parentFrame.setResizable(false);
            parentFrame.setLocationRelativeTo(null);

        // Load background image
        try {
            java.net.URL imgUrl = getClass().getResource("/BACKGROUND LOGIN-edited.png");
            if (imgUrl != null) {
                backgroundImage = ImageIO.read(imgUrl);
            } else {
                System.out.println("Resource not found: /BACKGROUND LOGIN-edited.png");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        initComponents();
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
        super.paintComponent(g);
    }
    private void doLogin() {
    String username = jTextField1.getText().trim();
    String password = new String(jPasswordField2.getPassword());

    if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Enter email and password!");
        return;
    }
    
    String role = authenticate(username, password);
             
        if (role != null) {
            int userId = getUserId(username);
            Session.setUserId(userId); 

            // 1. Create the Log Entry
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO user_logs (user_id, time_in) VALUES (?, NOW())";
                // PreparedStatement.RETURN_GENERATED_KEYS is required to get the ID for logout later
                PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS); 

                ps.setInt(1, userId);
                ps.executeUpdate();

                // 2. Save the Log ID into the Session class
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    Session.setLogId(rs.getInt(1)); 
                }

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Log error: " + e.getMessage());
            }  

            // 3. Role-Based Navigation
            if (role.equalsIgnoreCase("admin")) {
                JOptionPane.showMessageDialog(this, "Admin Login Successful!");
                // Switch the content pane to LogsPanel
                parentFrame.setContentPane(new LogsPanel(parentFrame));
            } else {
                JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + username);
                // Switch the content pane to DashboardPanel for regular users
                parentFrame.setContentPane(new DashboardPanel(parentFrame, username));
            }

            // 4. Refresh the Frame to show the new panel
            parentFrame.revalidate();
            parentFrame.repaint();
        
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password!");
        }
    }
    private String authenticate(String username, String password) {
      String role = null;

        try (Connection con = DatabaseConnection.getConnection()) {
            if (con == null) return null;

            String sql = "SELECT role FROM users WHERE email = ? AND password = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                role = rs.getString("role");
            }
        } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
    }

    return role;
    }

    public void openRegistration() {
        RegistrationScreen regPanel = new RegistrationScreen(parentFrame);
        parentFrame.setContentPane(regPanel);
        parentFrame.revalidate();
        parentFrame.repaint();
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPasswordField2 = new javax.swing.JPasswordField();
        jLabel8 = new javax.swing.JLabel();

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Login/Logo.png"))); // NOI18N
        jLabel2.setText("STRATA");

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(153, 0, 0), null, null));
        setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        setPreferredSize(new java.awt.Dimension(480, 680));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(153, 0, 0), null, null));
        jPanel1.setMaximumSize(new java.awt.Dimension(400, 488));
        jPanel1.setMinimumSize(new java.awt.Dimension(400, 488));
        jPanel1.setPreferredSize(new java.awt.Dimension(400, 488));
        jPanel1.setRequestFocusEnabled(false);

        jTextField1.setBackground(new java.awt.Color(51, 51, 51));
        jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(255, 255, 255));
        jTextField1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 0, 0), null, null));
        jTextField1.setSelectionColor(new java.awt.Color(0, 0, 204));
        jTextField1.addActionListener(this::jTextField1ActionPerformed);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        jLabel4.setText("Log into Strata");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel5.setText("Email ");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel6.setText("Password");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel7.setText("Enter your credentials to continue...");

        jButton3.setBackground(new java.awt.Color(102, 0, 0));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Login");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jButton4.setBackground(new java.awt.Color(102, 0, 0));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("SignUp");
        jButton4.addActionListener(this::jButton4ActionPerformed);

        jPasswordField2.setBackground(new java.awt.Color(51, 51, 51));
        jPasswordField2.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jPasswordField2.setForeground(new java.awt.Color(255, 255, 255));
        jPasswordField2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 0, 0), null, null));
        jPasswordField2.setSelectionColor(new java.awt.Color(0, 0, 204));
        jPasswordField2.addActionListener(this::jPasswordField2ActionPerformed);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(60, 63, 65));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Login/Logo.png"))); // NOI18N
        jLabel8.setText("STRATA");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel8)
                        .addComponent(jLabel7)))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(66, 66, 66))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // login function
        doLogin();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // signup function
        openRegistration();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jPasswordField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordField2ActionPerformed
        // password 
    }//GEN-LAST:event_jPasswordField2ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

    private int getUserId(String username) {
        int userId = -1;
        
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT user_id FROM users WHERE email = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            userId = rs.getInt("user_id");
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "getUserId error: " + e.getMessage());
    }

    return userId;
    }
}

       

