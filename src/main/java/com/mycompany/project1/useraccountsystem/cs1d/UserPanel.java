/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.project1.useraccountsystem.cs1d;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author 
 * renzz12345
 * DrAze
 */

public class UserPanel extends javax.swing.JPanel {
private final JFrame parentFrame;
   
    public UserPanel(JFrame frame) {
        this.parentFrame = frame;

        // Frame Setup
        parentFrame.setSize(974, 634);
        parentFrame.setResizable(false);
        parentFrame.setLocationRelativeTo(null);

        initComponents();
        
        // Load data using your connection file
        loadMySQLData();
    }

    public void loadMySQLData() {
    String[] columnNames = {"USER", "JOINED", "LAST LOGIN", "TOTAL LOGIN", "ACTIONS"};

    DefaultTableModel model = new DefaultTableModel(null, columnNames) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 4; // Only the 'Actions' column
        }
    };

    try (Connection conn = DatabaseConnection.getConnection()) {
        // Corrected Query: Removed trailing comma and fixed JOIN aliases
        String query = """
                       SELECT 
                           U.first_name, 
                           U.email,
                           DATE_FORMAT(U.created_at, '%M %d, %Y') as created_at, 
                           DATE_FORMAT(MAX(L.time_out), '%M %d, %Y, %r') as last_logout,
                           COUNT(L.log_id) as total_logins
                       FROM users U
                       LEFT JOIN user_logs L ON U.user_id = L.user_id
                       GROUP BY U.user_id
                       """;

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
            String name = rs.getString("first_name");
            String email = rs.getString("email");

            // Use HTML to put the email on a new line and make it smaller/gray
            String userDisplay = "<html>"
                    + "<table cellpadding='10'>" // This adds 10px padding on all sides
                    + "<tr><td>"
                    + "<b>" + name + "</b><br>"
                    + "<font color='gray'>" + email + "</font>"
                    + "</td></tr>"
                    + "</table></html>";

            model.addRow(new Object[]{
                userDisplay, // Name and Email stacked
                rs.getString("created_at"),
                rs.getString("last_logout") == null ? "Never" : rs.getString("last_logout"),
                rs.getInt("total_logins"),
                "" // Actions
            });
        }
            jTable1.setModel(model);
            applyTableSettings(); // Re-attach your ActionButtonsRenderer and Editor

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyTableSettings() {
        // Design properties
        jTable1.setRowHeight(45);
        jTable1.setShowVerticalLines(false);
        jTable1.setIntercellSpacing(new Dimension(0, 1));
        
        // Create a renderer that centers text
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 1; i < 4; i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Assign the Buttons to the last column
        int actionCol = jTable1.getColumnCount() - 1;
        jTable1.getColumnModel().getColumn(actionCol).setCellRenderer(new ActionButtonsRenderer());
        jTable1.getColumnModel().getColumn(actionCol).setCellEditor(new ActionButtonsEditor());

        // Setup Cursor Hover Logic
        setupTableCursor();
    }

    private void setupTableCursor() {
        jTable1.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = jTable1.columnAtPoint(e.getPoint());
                jTable1.setCursor(col == jTable1.getColumnCount() - 1 
                    ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) 
                    : Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }
        
    //The Panel that holds the buttons
    class ActionPanel extends javax.swing.JPanel {
    public javax.swing.JButton editBtn = new javax.swing.JButton();
    public javax.swing.JButton deleteBtn = new javax.swing.JButton();

    public ActionPanel() {
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));
        setOpaque(true);

        try {
            // Load icons from your resources folder
            // Adjust the path "/icons/edit.png" to match your actual file location
            editBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/User UI/Pen.png")));
            deleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/USER UI/Trash.png")));
        } catch (Exception e) {
            // Fallback to text if icons fail to load
            editBtn.setText("Edit");
            deleteBtn.setText("Del");
        }

        styleIconButton(editBtn);
        styleIconButton(deleteBtn);

        add(editBtn);
        add(deleteBtn);
        }

        private void styleIconButton(javax.swing.JButton btn) {
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            // Ensures the button is only as large as the icon
            btn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        }
    }

    //Renderer (Controls what the user sees)
    class ActionButtonsRenderer extends javax.swing.table.DefaultTableCellRenderer {
        private final ActionPanel panel = new ActionPanel();

        @Override
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            // Match the background to the row selection
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }
    }

    //Editor (Controls the click logic)
    class ActionButtonsEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private final ActionPanel panel = new ActionPanel();

        public ActionButtonsEditor() {
            // EDIT BUTTON LOGIC
            panel.editBtn.addActionListener(e -> {
                System.out.println("Editing user...");
                fireEditingStopped(); // Stops the "edit" mode so the table behaves normally
            });

            // DELETE BUTTON LOGIC
            panel.deleteBtn.addActionListener(e -> {
                int result = javax.swing.JOptionPane.showConfirmDialog(null, "Remove this user permanently?");
                if (result == javax.swing.JOptionPane.YES_OPTION) {
                    // Add your database delete code here
                }
                fireEditingStopped();
            });
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return null; }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        totalLogin = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        Success = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        Failed = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        Success1 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        Failed6 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        DashboardButton = new javax.swing.JButton();
        LogsButton = new javax.swing.JButton();
        UsersButton = new javax.swing.JButton();
        SettingsButton = new javax.swing.JButton();
        LogoutButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        Success7 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        Success8 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();

        totalLogin.setBackground(new java.awt.Color(255, 255, 255));
        totalLogin.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        totalLogin.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N

        javax.swing.GroupLayout totalLoginLayout = new javax.swing.GroupLayout(totalLogin);
        totalLogin.setLayout(totalLoginLayout);
        totalLoginLayout.setHorizontalGroup(
            totalLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, totalLoginLayout.createSequentialGroup()
                .addContainerGap(72, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addGap(28, 28, 28))
        );
        totalLoginLayout.setVerticalGroup(
            totalLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, totalLoginLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addGap(40, 40, 40))
        );

        Success.setBackground(new java.awt.Color(255, 255, 255));
        Success.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Success.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N

        javax.swing.GroupLayout SuccessLayout = new javax.swing.GroupLayout(Success);
        Success.setLayout(SuccessLayout);
        SuccessLayout.setHorizontalGroup(
            SuccessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SuccessLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel11)
                .addContainerGap(71, Short.MAX_VALUE))
        );
        SuccessLayout.setVerticalGroup(
            SuccessLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SuccessLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11)
                .addGap(39, 39, 39))
        );

        Failed.setBackground(new java.awt.Color(255, 255, 255));
        Failed.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Failed.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N

        javax.swing.GroupLayout FailedLayout = new javax.swing.GroupLayout(Failed);
        Failed.setLayout(FailedLayout);
        FailedLayout.setHorizontalGroup(
            FailedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FailedLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel12)
                .addContainerGap(73, Short.MAX_VALUE))
        );
        FailedLayout.setVerticalGroup(
            FailedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FailedLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel12)
                .addGap(39, 39, 39))
        );

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel13.setText("ALL USERS");

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel19.setText("ALL USERS");

        setPreferredSize(new java.awt.Dimension(896, 634));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel4.setText("Users");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Manage all registered Users ");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Success1.setBackground(new java.awt.Color(255, 255, 255));
        Success1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Success1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N

        javax.swing.GroupLayout Success1Layout = new javax.swing.GroupLayout(Success1);
        Success1.setLayout(Success1Layout);
        Success1Layout.setHorizontalGroup(
            Success1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Success1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel14)
                .addContainerGap(143, Short.MAX_VALUE))
        );
        Success1Layout.setVerticalGroup(
            Success1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Success1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14)
                .addGap(39, 39, 39))
        );

        Failed6.setBackground(new java.awt.Color(255, 255, 255));
        Failed6.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Failed6.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N

        javax.swing.GroupLayout Failed6Layout = new javax.swing.GroupLayout(Failed6);
        Failed6.setLayout(Failed6Layout);
        Failed6Layout.setHorizontalGroup(
            Failed6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Failed6Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel21)
                .addContainerGap(125, Short.MAX_VALUE))
        );
        Failed6Layout.setVerticalGroup(
            Failed6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Failed6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel21)
                .addGap(39, 39, 39))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel6.setText("ALL USERS");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel6)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "USER", "JOINED", "LAST LOGIN", "TOTAL LOGIN", "ACTIONS"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
        }

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Login/Logo.png"))); // NOI18N
        jLabel1.setText("STRATA");

        DashboardButton.setBackground(new java.awt.Color(102, 102, 102));
        DashboardButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        DashboardButton.setForeground(new java.awt.Color(255, 255, 255));
        DashboardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard Admin/menu.png"))); // NOI18N
        DashboardButton.setText("Dashboard");
        DashboardButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        DashboardButton.setName("Dashboard"); // NOI18N
        DashboardButton.addActionListener(this::DashboardButtonActionPerformed);

        LogsButton.setBackground(new java.awt.Color(102, 102, 102));
        LogsButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        LogsButton.setForeground(new java.awt.Color(255, 255, 255));
        LogsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard Admin/log.png"))); // NOI18N
        LogsButton.setText("Login logs");
        LogsButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        LogsButton.setName("Login_logs"); // NOI18N
        LogsButton.addActionListener(this::LogsButtonActionPerformed);

        UsersButton.setBackground(new java.awt.Color(102, 102, 102));
        UsersButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        UsersButton.setForeground(new java.awt.Color(255, 255, 255));
        UsersButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard Admin/users.png"))); // NOI18N
        UsersButton.setText("Users");
        UsersButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        UsersButton.setName("Users"); // NOI18N
        UsersButton.addActionListener(this::UsersButtonActionPerformed);

        SettingsButton.setBackground(new java.awt.Color(102, 102, 102));
        SettingsButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        SettingsButton.setForeground(new java.awt.Color(255, 255, 255));
        SettingsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard Admin/settings.png"))); // NOI18N
        SettingsButton.setText("Settings");
        SettingsButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        SettingsButton.setName("Settings"); // NOI18N
        SettingsButton.addActionListener(this::SettingsButtonActionPerformed);

        LogoutButton.setBackground(new java.awt.Color(102, 102, 102));
        LogoutButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        LogoutButton.setForeground(new java.awt.Color(255, 255, 255));
        LogoutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard Admin/log-out.png"))); // NOI18N
        LogoutButton.setText("Log out");
        LogoutButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        LogoutButton.setName("Logout"); // NOI18N
        LogoutButton.addActionListener(this::LogoutButtonActionPerformed);

        jLabel2.setBackground(new java.awt.Color(102, 102, 102));
        jLabel2.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("OVERVIEW");

        jLabel3.setBackground(new java.awt.Color(102, 102, 102));
        jLabel3.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("SYSTEM");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(DashboardButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(LogsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(UsersButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SettingsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(LogoutButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addGap(33, 33, 33))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DashboardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(LogsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(UsersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SettingsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(LogoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        Success7.setBackground(new java.awt.Color(255, 255, 255));
        Success7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Success7.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N

        javax.swing.GroupLayout Success7Layout = new javax.swing.GroupLayout(Success7);
        Success7.setLayout(Success7Layout);
        Success7Layout.setHorizontalGroup(
            Success7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Success7Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel24)
                .addContainerGap(143, Short.MAX_VALUE))
        );
        Success7Layout.setVerticalGroup(
            Success7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Success7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel24)
                .addGap(39, 39, 39))
        );

        Success8.setBackground(new java.awt.Color(255, 255, 255));
        Success8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Success8.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N

        javax.swing.GroupLayout Success8Layout = new javax.swing.GroupLayout(Success8);
        Success8.setLayout(Success8Layout);
        Success8Layout.setHorizontalGroup(
            Success8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Success8Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel25)
                .addContainerGap(143, Short.MAX_VALUE))
        );
        Success8Layout.setVerticalGroup(
            Success8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Success8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel25)
                .addGap(39, 39, 39))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Success1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Success7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                        .addComponent(Success8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Failed6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addGap(5, 5, 5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Success1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Success7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Failed6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Success8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                .addGap(5, 5, 5))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void DashboardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DashboardButtonActionPerformed
        // dashboard function go to DashboardPanel
        parentFrame.setContentPane(new DashboardPanel(parentFrame));
        parentFrame.revalidate();
        parentFrame.repaint();
    }//GEN-LAST:event_DashboardButtonActionPerformed

    private void LogsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogsButtonActionPerformed
        // login logs function go to LogsPanel
        parentFrame.setContentPane(new LogsPanel(parentFrame));
        parentFrame.revalidate();
        parentFrame.repaint();
        //loadSummaryData();
    }//GEN-LAST:event_LogsButtonActionPerformed

    private void UsersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UsersButtonActionPerformed
        // go to userPanel
        parentFrame.setContentPane(new UserPanel(parentFrame));
        parentFrame.revalidate();
        parentFrame.repaint();
    }//GEN-LAST:event_UsersButtonActionPerformed

    private void SettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingsButtonActionPerformed
        // go to settings panel
        parentFrame.setContentPane(new SettingsPanel(parentFrame));
        parentFrame.revalidate();
        parentFrame.repaint();
    }//GEN-LAST:event_SettingsButtonActionPerformed

    private void LogoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogoutButtonActionPerformed
        // logout function go to LoginScreen
        LogService.updateTimeOut();
        parentFrame.setContentPane(new LoginScreen(parentFrame));
        parentFrame.revalidate();
        parentFrame.repaint();
    }//GEN-LAST:event_LogoutButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton DashboardButton;
    private javax.swing.JPanel Failed;
    private javax.swing.JPanel Failed6;
    private javax.swing.JButton LogoutButton;
    private javax.swing.JButton LogsButton;
    private javax.swing.JButton SettingsButton;
    private javax.swing.JPanel Success;
    private javax.swing.JPanel Success1;
    private javax.swing.JPanel Success7;
    private javax.swing.JPanel Success8;
    private javax.swing.JButton UsersButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel totalLogin;
    // End of variables declaration//GEN-END:variables
}
