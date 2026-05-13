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
import java.sql.PreparedStatement;
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
public final class AdminUserPanel extends javax.swing.JPanel {

    private final JFrame parentFrame;
    private final String currentUsername;
    private final int currentUserId;

    public AdminUserPanel(JFrame frame, int userId, String username) {

        this.parentFrame = frame;
        this.currentUserId = userId;
        this.currentUsername = username;

        // Frame Setup
        Dimension lockSize = new Dimension(974, 634);

        this.setPreferredSize(lockSize);
        this.setMinimumSize(lockSize);
        this.setMaximumSize(lockSize);

        initComponents();

        updateStatCards();
        loadMySQLData();
    }

    public void loadMySQLData() {

        // Added hidden ID column
        String[] columnNames = {
            "ID",
            "USER",
            "JOINED",
            "LAST LOGIN",
            "TOTAL LOGIN",
            "ACTIONS"
        };

        DefaultTableModel model = new DefaultTableModel(null, columnNames) {

            @Override
            public boolean isCellEditable(int row, int column) {

                // ACTIONS column only
                return column == 5;
            }
        };

        try (Connection conn = DatabaseConnection.getConnection()) {

            String query = """
                    SELECT 
                        U.user_id,
                        U.first_name,
                        U.email,

                        DATE_FORMAT(U.created_at, '%M %d, %Y') AS joined,

                        DATE_FORMAT(MAX(L.time_in), '%M %d, %Y %h:%i %p') AS last_login,

                        COUNT(L.log_id) AS total_login

                    FROM users U

                    LEFT JOIN user_logs L
                        ON U.user_id = L.user_id

                    GROUP BY 
                        U.user_id,
                        U.first_name,
                        U.email,
                        U.created_at
                    """;

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {

                String name = rs.getString("first_name");
                String email = rs.getString("email");

                String userDisplay =
                        "<html>"
                        + "<table cellpadding='10'>"
                        + "<tr><td>"
                        + "<b>" + name + "</b><br>"
                        + "<font color='dcdbd7'>" + email + "</font>"
                        + "</td></tr>"
                        + "</table></html>";

                String lastLogin = rs.getString("last_login");

                model.addRow(new Object[]{
                    rs.getInt("user_id"), // Hidden ID
                    userDisplay,
                    rs.getString("joined"),
                    lastLogin == null ? "Never" : lastLogin,
                    rs.getInt("total_login"),
                    ""
                });
            }

            UserTable.setModel(model);

            applyTableSettings();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "SQL Error: " + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private void applyTableSettings() {

        UserTable.setRowHeight(50);

        UserTable.setShowVerticalLines(false);

        UserTable.setIntercellSpacing(new Dimension(0, 1));

        // Hide ID column
        UserTable.getColumnModel().getColumn(0).setMinWidth(0);
        UserTable.getColumnModel().getColumn(0).setMaxWidth(0);
        UserTable.getColumnModel().getColumn(0).setWidth(0);

        // Center renderer
        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Center JOINED / LAST LOGIN / TOTAL LOGIN
        for (int i = 2; i < 5; i++) {

            UserTable.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(centerRenderer);
        }

        // ACTION COLUMN
        int actionCol = UserTable.getColumnCount() - 1;

        UserTable.getColumnModel()
                .getColumn(actionCol)
                .setCellRenderer(new ActionButtonsRenderer());

        UserTable.getColumnModel()
                .getColumn(actionCol)
                .setCellEditor(new ActionButtonsEditor());

        setupTableCursor();
    }

    private void setupTableCursor() {

        UserTable.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {

                int col = UserTable.columnAtPoint(e.getPoint());

                UserTable.setCursor(
                        col == UserTable.getColumnCount() - 1
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
                );
            }
        });
    }

    // PANEL FOR BUTTONS
    class ActionPanel extends javax.swing.JPanel {
        
        public javax.swing.JButton editBtn = new javax.swing.JButton();
        public javax.swing.JButton deleteBtn = new javax.swing.JButton();
        
        public ActionPanel() {
            setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));
            setOpaque(true);

            try {
                editBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/USER UI/Pen.png")));
                deleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/USER UI/Trash.png")));
                
            } catch (Exception e) {
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
            btn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            btn.setVerticalAlignment(javax.swing.SwingConstants.CENTER );
            btn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        }
    }

    // RENDERER
    class ActionButtonsRenderer extends javax.swing.table.DefaultTableCellRenderer {
        private final ActionPanel panel = new ActionPanel();

        @Override
        public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) 
        {
            panel.setBackground(
                    isSelected
                    ? table.getSelectionBackground()
                    : table.getBackground()
            );
            return panel;
        }
    }

    // EDITOR
    class ActionButtonsEditor
            extends javax.swing.AbstractCellEditor
            implements javax.swing.table.TableCellEditor {

        private final ActionPanel panel = new ActionPanel();

        public ActionButtonsEditor() {
            
            panel.editBtn.addActionListener(e -> {
                int selectedRow = UserTable.getEditingRow();
                if (selectedRow >= 0) {
                    // 1. Get the Hidden ID from Column 0
                    int userId = (int) UserTable.getValueAt(selectedRow, 0);

                    // Stop cell editing so the UI doesn't hang
                    fireEditingStopped();

                    // 2. Call the method to open the edit panel
                    openEditAccountPanel(userId);
                }
            });

            panel.deleteBtn.addActionListener(e -> {int selectedRow = UserTable.getEditingRow();    
                if (selectedRow < 0) {

                    fireEditingStopped();
                    return;
                }

                // Hidden ID column
                int userId = Integer.parseInt(UserTable.getValueAt(selectedRow, 0).toString());
                int result = JOptionPane.showConfirmDialog(
                        null,
                        "Remove this user permanently?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

                if (result == JOptionPane.YES_OPTION) {
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        String query = "DELETE FROM users WHERE user_id = ?";

                        PreparedStatement pst = conn.prepareStatement(query);

                        pst.setInt(1, userId);

                        int rows = pst.executeUpdate();

                        if (rows > 0) {JOptionPane.showMessageDialog(null, "User deleted successfully.");
                            // Refresh
                            loadMySQLData();
                            updateStatCards();

                        } else {JOptionPane.showMessageDialog(null, "User not found.");
                        }

                    } catch (SQLException ex) {

                        JOptionPane.showMessageDialog( null, "SQL Error: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                fireEditingStopped();
            });
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(
                javax.swing.JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column
        ) {

            panel.setBackground(
                    table.getSelectionBackground()
            );

            return panel;
        }

        @Override
        public Object getCellEditorValue() {

            return null;
        }
    }

    public void updateStatCards() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = """
                            SELECT 
                                (SELECT COUNT(*) FROM users) as total,
                                (SELECT COUNT(*) 
                                 FROM users 
                                 WHERE DATE(created_at) = CURDATE()) as today
                           """;

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            
            if (rs.next()) {

                int total = rs.getInt("total");

                int today = rs.getInt("today");

                TotalUsersLabel.setText(String.valueOf(total));

                TotalUsersLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 10));

                TotalUsersLabel.setForeground(new java.awt.Color(33, 37, 41));

                TrendLabel.setText("+" + today + " today");

                TrendLabel.setForeground(new java.awt.Color(40, 167, 69));

                TrendLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD,8));
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
    
    private void openEditAccountPanel(int userId) {
        // 1. Create the dialog to hold the SettingsPanel
        JDialog dialog = new JDialog(parentFrame, "Edit User Profile", true);

        // 2. Instantiate your SettingsPanel (Pass the current admin username for context)
        AdminSettingsPanel editPanel = new AdminSettingsPanel(parentFrame, currentUserId, currentUsername);

        // 3. Fetch the data for the specific user being edited
        // Note: Ensure your AdminSettingsPanel has a method to fetch data by ID
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT first_name, last_name, email, role FROM users WHERE user_id = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Use the method we established for the UI in image_71c646.png
                editPanel.setEditData(
                        userId,
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching user: " + e.getMessage());
            return;
        }

        // 4. Configure and show the dialog
        dialog.getContentPane().add(editPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setResizable(false);
        dialog.setVisible(true); // Program execution pauses here until dialog is closed

        // 5. Refresh your table after the edit is done
        loadMySQLData();
        updateStatCards();
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
        jLabel21 = new javax.swing.JLabel();
        HomeButton = new javax.swing.JButton();
        UsersButton = new javax.swing.JButton();
        LogsButton = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        LogoutButton = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
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

        setBackground(new java.awt.Color(51, 51, 51));
        setForeground(new java.awt.Color(250, 245, 241));
        setMaximumSize(new java.awt.Dimension(954, 604));
        setMinimumSize(new java.awt.Dimension(954, 604));
        setPreferredSize(new java.awt.Dimension(954, 604));

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
                        .addComponent(TotalUsersLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
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
        jPanel1.setPreferredSize(new java.awt.Dimension(180, 600));

        jLabel21.setBackground(new java.awt.Color(102, 102, 102));
        jLabel21.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("OVERVIEW");

        HomeButton.setBackground(new java.awt.Color(102, 102, 102));
        HomeButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        HomeButton.setForeground(new java.awt.Color(255, 255, 255));
        HomeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard Admin/menu.png"))); // NOI18N
        HomeButton.setText("Home");
        HomeButton.setBorderPainted(false);
        HomeButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        HomeButton.setName("Dashboard"); // NOI18N
        HomeButton.addActionListener(this::HomeButtonActionPerformed);

        UsersButton.setBackground(new java.awt.Color(163, 31, 19));
        UsersButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        UsersButton.setForeground(new java.awt.Color(255, 255, 255));
        UsersButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard Admin/users.png"))); // NOI18N
        UsersButton.setText("Users");
        UsersButton.setBorderPainted(false);
        UsersButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        UsersButton.setName("UsersButton"); // NOI18N
        UsersButton.addActionListener(this::UsersButtonActionPerformed);

        LogsButton.setBackground(new java.awt.Color(102, 102, 102));
        LogsButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        LogsButton.setForeground(new java.awt.Color(255, 255, 255));
        LogsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard Admin/log.png"))); // NOI18N
        LogsButton.setText("Logs");
        LogsButton.setBorderPainted(false);
        LogsButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        LogsButton.setName("Users"); // NOI18N
        LogsButton.addActionListener(this::LogsButtonActionPerformed);

        jLabel20.setBackground(new java.awt.Color(102, 102, 102));
        jLabel20.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("SYSTEM");

        LogoutButton.setBackground(new java.awt.Color(102, 102, 102));
        LogoutButton.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        LogoutButton.setForeground(new java.awt.Color(255, 255, 255));
        LogoutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard Admin/log-out.png"))); // NOI18N
        LogoutButton.setText("Log out");
        LogoutButton.setBorderPainted(false);
        LogoutButton.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        LogoutButton.setName("Logout"); // NOI18N
        LogoutButton.addActionListener(this::LogoutButtonActionPerformed);

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Login/Logo.png"))); // NOI18N
        jLabel23.setText("STRATA");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(UsersButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(LogsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(LogoutButton, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                                    .addComponent(HomeButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(HomeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(UsersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(LogsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LogoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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
                .addContainerGap(102, Short.MAX_VALUE))
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
                .addContainerGap(116, Short.MAX_VALUE))
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
                .addContainerGap(125, Short.MAX_VALUE))
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
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(TotalUsers, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(Active, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(23, 23, 23)
                        .addComponent(Pending, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(Failed2, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)
                    .addComponent(Header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Subheading, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                .addGap(5, 5, 5))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

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

    private void HomeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomeButtonActionPerformed
        // dashboard function go to DashboardPanel
        parentFrame.setContentPane(new AdminDashboardPanel(parentFrame, currentUserId, currentUsername));
        parentFrame.revalidate();
        parentFrame.repaint();
    }//GEN-LAST:event_HomeButtonActionPerformed

    private void UsersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UsersButtonActionPerformed
        // go to userPanel
    }//GEN-LAST:event_UsersButtonActionPerformed

    private void LogsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogsButtonActionPerformed
        // TODO add your handling code here:
        parentFrame.setContentPane(new AdminLogsPanel(parentFrame, currentUsername));
        parentFrame.revalidate();
        parentFrame.repaint();
    }//GEN-LAST:event_LogsButtonActionPerformed

    private void LogoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogoutButtonActionPerformed
        // logout function go to LoginScreen
        LogService.updateTimeOut();
        parentFrame.setContentPane(new LoginScreen(parentFrame));
        parentFrame.revalidate();
        parentFrame.repaint();
    }//GEN-LAST:event_LogoutButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Active;
    private javax.swing.JLabel ActiveUsersLabel;
    private javax.swing.JButton AddUserButton;
    private javax.swing.JPanel Failed;
    private javax.swing.JPanel Failed2;
    private javax.swing.JPanel Header;
    private javax.swing.JButton HomeButton;
    private javax.swing.JButton LogoutButton;
    private javax.swing.JButton LogsButton;
    private javax.swing.JPanel Pending;
    private javax.swing.JLabel PendingUsersLabel;
    private javax.swing.JTextField SearchField;
    private javax.swing.JPanel Subheading;
    private javax.swing.JPanel Success;
    private javax.swing.JLabel SuspendedUsersLabel;
    private javax.swing.JPanel TotalUsers;
    private javax.swing.JLabel TotalUsersHeader;
    private javax.swing.JLabel TotalUsersLabel;
    private javax.swing.JLabel TrendLabel;
    private javax.swing.JTable UserTable;
    private javax.swing.JButton UsersButton;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel totalLogin;
    // End of variables declaration//GEN-END:variables
}
