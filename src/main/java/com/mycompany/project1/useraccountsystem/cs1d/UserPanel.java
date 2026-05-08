/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.project1.useraccountsystem.cs1d;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JDialog;
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

public final class UserPanel extends javax.swing.JPanel {
    private final JFrame parentFrame;
    private final String currentUsername;
   
    public UserPanel(JFrame frame, String username) {
        this.parentFrame = frame;
        this.currentUsername = username;

        // Frame Setup
        Dimension lockSize = new Dimension(974, 634);
        this.setPreferredSize(lockSize);
        this.setMinimumSize(lockSize);
        this.setMaximumSize(lockSize);
        
        initComponents();
        updateStatCards();
        
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
                           DATE_FORMAT(MAX(L.time_out), '%M %d, %Y, %h:%i %p') as last_logout,
                           COUNT(L.log_id) as total_logins
                       FROM users U
                       LEFT JOIN user_logs L ON U.user_id = L.user_id
                       GROUP BY U.user_id, U.first_name, U.email
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
                    + "<font color='dcdbd7'>" + email + "</font>"
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
            UserTable.setModel(model);
            applyTableSettings(); // Re-attach your ActionButtonsRenderer and Editor

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyTableSettings() {
        // Design properties
        UserTable.setRowHeight(50);
        UserTable.setShowVerticalLines(false);
        UserTable.setIntercellSpacing(new Dimension(0, 1));
        
        // Create a renderer that centers text
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 1; i < 4; i++) {
            UserTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Assign the Buttons to the last column
        int actionCol = UserTable.getColumnCount() - 1;
        UserTable.getColumnModel().getColumn(actionCol).setCellRenderer(new ActionButtonsRenderer());
        UserTable.getColumnModel().getColumn(actionCol).setCellEditor(new ActionButtonsEditor());

        // Setup Cursor Hover Logic
        setupTableCursor();
    }

    private void setupTableCursor() {
        UserTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = UserTable.columnAtPoint(e.getPoint());
                UserTable.setCursor(col == UserTable.getColumnCount() - 1 
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

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(0, 5, 0, 5); // 5px horizontal gap

        add(editBtn, gbc);
        add(deleteBtn, gbc);
        }

        private void styleIconButton(javax.swing.JButton btn) {
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            // Ensures the button is only as large as the icon and centered
            btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            btn.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
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
    
    public void updateStatCards() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Query to get total users and users joined today
            String query = """
                            SELECT 
                                (SELECT COUNT(*) FROM users) as total,
                                (SELECT COUNT(*) FROM users WHERE DATE(created_at) = CURDATE()) as today
                           """;

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);

            if (rs.next()) {
                int total = rs.getInt("total");
                int today = rs.getInt("today");

                //The Total Users Number
                TotalUsersLabel.setText(String.valueOf(total));
                TotalUsersLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 10));
                TotalUsersLabel.setForeground(new java.awt.Color(33, 37, 41));

                // The Trend Label
                // You might need to add this label in your NetBeans Design tab first
                TrendLabel.setText("+" + today + " today");
                TrendLabel.setForeground(new java.awt.Color(40, 167, 69)); // Success Green
                TrendLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 8));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        Header = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        AddUserButton = new javax.swing.JButton();
        TotalUsers = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        TotalUsersHeader = new javax.swing.JLabel();
        TrendLabel = new javax.swing.JLabel();
        TotalUsersLabel = new javax.swing.JLabel();
        Subheading = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        SearchField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        UserTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        DashboardButton = new javax.swing.JButton();
        UsersButton = new javax.swing.JButton();
        SettingsButton = new javax.swing.JButton();
        LogoutButton = new javax.swing.JButton();
        OverviewLabel = new javax.swing.JLabel();
        SystemLabel = new javax.swing.JLabel();
        Failed2 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        SuspendedUsersLabel = new javax.swing.JLabel();
        Pending = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        PendingUsersLabel = new javax.swing.JLabel();
        Active = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        ActiveUsersLabel = new javax.swing.JLabel();

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

        setBackground(new java.awt.Color(250, 245, 241));
        setForeground(new java.awt.Color(250, 245, 241));
        setMaximumSize(new java.awt.Dimension(974, 634));
        setMinimumSize(new java.awt.Dimension(974, 634));
        setPreferredSize(new java.awt.Dimension(974, 634));

        Header.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel4.setText("Users");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Manage all registered Users ");

        AddUserButton.setBackground(new java.awt.Color(163, 31, 19));
        AddUserButton.setForeground(new java.awt.Color(255, 255, 255));
        AddUserButton.setText("+ Add User");
        AddUserButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        AddUserButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        AddUserButton.addActionListener(this::AddUserButtonActionPerformed);

        javax.swing.GroupLayout HeaderLayout = new javax.swing.GroupLayout(Header);
        Header.setLayout(HeaderLayout);
        HeaderLayout.setHorizontalGroup(
            HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HeaderLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(HeaderLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(AddUserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        HeaderLayout.setVerticalGroup(
            HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(HeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(AddUserButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TotalUsers.setBackground(new java.awt.Color(255, 255, 255));
        TotalUsers.setToolTipText("");
        TotalUsers.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TotalUsers.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        TotalUsers.setName(""); // NOI18N
        TotalUsers.setPreferredSize(new java.awt.Dimension(175, 51));

        TotalUsersHeader.setText("TOTAL USERS");
        TotalUsersHeader.setName(""); // NOI18N

        TrendLabel.setText("+/-");

        TotalUsersLabel.setText("-");

        javax.swing.GroupLayout TotalUsersLayout = new javax.swing.GroupLayout(TotalUsers);
        TotalUsers.setLayout(TotalUsersLayout);
        TotalUsersLayout.setHorizontalGroup(
            TotalUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TotalUsersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TotalUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(TotalUsersLayout.createSequentialGroup()
                        .addComponent(TotalUsersHeader)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(TotalUsersLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(TotalUsersLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(TrendLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        TotalUsersLayout.setVerticalGroup(
            TotalUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TotalUsersLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel14)
                .addGap(39, 39, 39))
            .addGroup(TotalUsersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TotalUsersHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TotalUsersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TotalUsersLabel)
                    .addComponent(TrendLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Subheading.setBackground(new java.awt.Color(250, 245, 241));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel6.setText("ALL USERS");

        SearchField.setBackground(new java.awt.Color(255, 255, 255));
        SearchField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        SearchField.setText("Search name or email");
        SearchField.setToolTipText("Search name or email");
        SearchField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        javax.swing.GroupLayout SubheadingLayout = new javax.swing.GroupLayout(Subheading);
        Subheading.setLayout(SubheadingLayout);
        SubheadingLayout.setHorizontalGroup(
            SubheadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SubheadingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        SubheadingLayout.setVerticalGroup(
            SubheadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SubheadingLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(SubheadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        UserTable.setBackground(new java.awt.Color(255, 255, 255));
        UserTable.setForeground(new java.awt.Color(34, 43, 48));
        UserTable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(UserTable);
        if (UserTable.getColumnModel().getColumnCount() > 0) {
            UserTable.getColumnModel().getColumn(0).setResizable(false);
            UserTable.getColumnModel().getColumn(1).setResizable(false);
            UserTable.getColumnModel().getColumn(2).setResizable(false);
            UserTable.getColumnModel().getColumn(3).setResizable(false);
            UserTable.getColumnModel().getColumn(4).setResizable(false);
        }

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(203, 634));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Login/Logo.png"))); // NOI18N
        jLabel1.setText("STRATA");

        DashboardButton.setBackground(new java.awt.Color(102, 102, 102));
        DashboardButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        DashboardButton.setForeground(new java.awt.Color(255, 255, 255));
        DashboardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard Admin/menu.png"))); // NOI18N
        DashboardButton.setText("Dashboard");
        DashboardButton.setBorderPainted(false);
        DashboardButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        DashboardButton.setName("Dashboard"); // NOI18N
        DashboardButton.addActionListener(this::DashboardButtonActionPerformed);

        UsersButton.setBackground(new java.awt.Color(163, 31, 19));
        UsersButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        UsersButton.setForeground(new java.awt.Color(255, 255, 255));
        UsersButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard Admin/users.png"))); // NOI18N
        UsersButton.setText("Users");
        UsersButton.setBorderPainted(false);
        UsersButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        UsersButton.setName("Users"); // NOI18N
        UsersButton.addActionListener(this::UsersButtonActionPerformed);

        SettingsButton.setBackground(new java.awt.Color(102, 102, 102));
        SettingsButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        SettingsButton.setForeground(new java.awt.Color(255, 255, 255));
        SettingsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard Admin/settings.png"))); // NOI18N
        SettingsButton.setText("Settings");
        SettingsButton.setBorderPainted(false);
        SettingsButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        SettingsButton.setName("Settings"); // NOI18N
        SettingsButton.addActionListener(this::SettingsButtonActionPerformed);

        LogoutButton.setBackground(new java.awt.Color(102, 102, 102));
        LogoutButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        LogoutButton.setForeground(new java.awt.Color(255, 255, 255));
        LogoutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard Admin/log-out.png"))); // NOI18N
        LogoutButton.setText("Log out");
        LogoutButton.setBorderPainted(false);
        LogoutButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        LogoutButton.setName("Logout"); // NOI18N
        LogoutButton.addActionListener(this::LogoutButtonActionPerformed);

        OverviewLabel.setBackground(new java.awt.Color(102, 102, 102));
        OverviewLabel.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        OverviewLabel.setForeground(new java.awt.Color(255, 255, 255));
        OverviewLabel.setText("OVERVIEW");

        SystemLabel.setBackground(new java.awt.Color(102, 102, 102));
        SystemLabel.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        SystemLabel.setForeground(new java.awt.Color(255, 255, 255));
        SystemLabel.setText("SYSTEM");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(DashboardButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(UsersButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SettingsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(LogoutButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(SystemLabel)
                    .addComponent(OverviewLabel))
                .addGap(33, 33, 33))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(OverviewLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DashboardButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(UsersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(134, 134, 134)
                .addComponent(SystemLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SettingsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(LogoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 148, Short.MAX_VALUE))
        );

        Failed2.setBackground(new java.awt.Color(255, 255, 255));
        Failed2.setToolTipText("");
        Failed2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Failed2.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        Failed2.setName(""); // NOI18N
        Failed2.setPreferredSize(new java.awt.Dimension(175, 51));

        SuspendedUsersLabel.setText("SUSPENDED");
        SuspendedUsersLabel.setName(""); // NOI18N

        javax.swing.GroupLayout Failed2Layout = new javax.swing.GroupLayout(Failed2);
        Failed2.setLayout(Failed2Layout);
        Failed2Layout.setHorizontalGroup(
            Failed2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Failed2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SuspendedUsersLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addContainerGap(97, Short.MAX_VALUE))
        );
        Failed2Layout.setVerticalGroup(
            Failed2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Failed2Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addGap(39, 39, 39))
            .addGroup(Failed2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SuspendedUsersLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Pending.setBackground(new java.awt.Color(255, 255, 255));
        Pending.setToolTipText("");
        Pending.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Pending.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        Pending.setName(""); // NOI18N
        Pending.setPreferredSize(new java.awt.Dimension(175, 51));

        PendingUsersLabel.setText("PENDING");
        PendingUsersLabel.setName(""); // NOI18N

        javax.swing.GroupLayout PendingLayout = new javax.swing.GroupLayout(Pending);
        Pending.setLayout(PendingLayout);
        PendingLayout.setHorizontalGroup(
            PendingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PendingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PendingUsersLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PendingLayout.setVerticalGroup(
            PendingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PendingLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel17)
                .addGap(39, 39, 39))
            .addGroup(PendingLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PendingUsersLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Active.setBackground(new java.awt.Color(255, 255, 255));
        Active.setToolTipText("");
        Active.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Active.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        Active.setName(""); // NOI18N
        Active.setPreferredSize(new java.awt.Dimension(175, 51));

        ActiveUsersLabel.setText("ACTIVE");
        ActiveUsersLabel.setName(""); // NOI18N

        javax.swing.GroupLayout ActiveLayout = new javax.swing.GroupLayout(Active);
        Active.setLayout(ActiveLayout);
        ActiveLayout.setHorizontalGroup(
            ActiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ActiveLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ActiveUsersLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        ActiveLayout.setVerticalGroup(
            ActiveLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ActiveLayout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(jLabel16)
                .addGap(39, 39, 39))
            .addGroup(ActiveLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ActiveUsersLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                        .addGap(5, 5, 5))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(TotalUsers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Active, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Pending, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(Failed2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Header, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Subheading, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TotalUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Failed2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Pending, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Active, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Subheading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
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

    private void UsersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UsersButtonActionPerformed
        // go to userPanel
        parentFrame.setContentPane(new UserPanel(parentFrame, currentUsername));
        parentFrame.revalidate();
        parentFrame.repaint();
    }//GEN-LAST:event_UsersButtonActionPerformed

    private void SettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SettingsButtonActionPerformed
        // go to settings panel
        parentFrame.setContentPane(new SettingsPanel(parentFrame, currentUsername));
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

    private void AddUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddUserButtonActionPerformed
        // TODO add your handling code here:   
        // 1. Create the Dialog
        JDialog regDialog = new JDialog(parentFrame, "New Registration", true);

        // 2. Set behavior: Dispose only closes the popup
        regDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // 3. Add your screen
        AddAccountScreen regContent = new AddAccountScreen(parentFrame);
        regDialog.add(regContent);

        // 4. Styling
        regDialog.setUndecorated(true);

        // IMPORTANT: pack() sets the dialog to the 680x510 size of regContent
        // without affecting the parentFrame's size.
        regDialog.pack();
        regDialog.setLocationRelativeTo(parentFrame);

        // 5. Show it
        regDialog.setVisible(true);

        // 6. Refresh the Table after popup is closed
        loadMySQLData();
    }//GEN-LAST:event_AddUserButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Active;
    private javax.swing.JLabel ActiveUsersLabel;
    private javax.swing.JButton AddUserButton;
    private javax.swing.JButton DashboardButton;
    private javax.swing.JPanel Failed;
    private javax.swing.JPanel Failed2;
    private javax.swing.JPanel Header;
    private javax.swing.JButton LogoutButton;
    private javax.swing.JLabel OverviewLabel;
    private javax.swing.JPanel Pending;
    private javax.swing.JLabel PendingUsersLabel;
    private javax.swing.JTextField SearchField;
    private javax.swing.JButton SettingsButton;
    private javax.swing.JPanel Subheading;
    private javax.swing.JPanel Success;
    private javax.swing.JLabel SuspendedUsersLabel;
    private javax.swing.JLabel SystemLabel;
    private javax.swing.JPanel TotalUsers;
    private javax.swing.JLabel TotalUsersHeader;
    private javax.swing.JLabel TotalUsersLabel;
    private javax.swing.JLabel TrendLabel;
    private javax.swing.JTable UserTable;
    private javax.swing.JButton UsersButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel totalLogin;
    // End of variables declaration//GEN-END:variables
}
