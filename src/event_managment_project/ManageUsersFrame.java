package event_managment_project;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import utils.DBConnection;
import utils.Validator;

public class ManageUsersFrame extends JFrame {
    private JButton addButton, deleteButton, viewButton, reportButton, backButton;

    public ManageUsersFrame() {
        setTitle("Manage Users");
        setSize(400, 400);
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10));
        setVisible(true);

        addButton = new JButton("Add User");
        deleteButton = new JButton("Delete User");
        viewButton = new JButton("View Users");
        reportButton = new JButton("Generate Report");
        backButton = new JButton("Back");

        add(addButton);
        add(deleteButton);
        add(viewButton);
        add(reportButton);
        add(backButton);

        
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddUserForm();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDeleteUserForm();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showUsersTable();
            }
        });

        reportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
        
        backButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                Back();
            }
        });
    }

   
    private void showAddUserForm() {
    JFrame addFrame = new JFrame("Add User");
    addFrame.setSize(400, 300);
    addFrame.setLayout(new GridLayout(6, 2, 10, 10));
    addFrame.setLocationRelativeTo(null);

    JTextField usernameField = new JTextField();
    JTextField emailField = new JTextField();
    JTextField passwordField = new JTextField();
    
    
    JComboBox<String> roleBox = new JComboBox<>(new String[]{"attendee", "Admin", "organizer"});
    
    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel"); 

    addFrame.add(new JLabel("Username:"));
    addFrame.add(usernameField);
    addFrame.add(new JLabel("Email:"));
    addFrame.add(emailField);
    addFrame.add(new JLabel("Password:"));
    addFrame.add(passwordField);
    addFrame.add(new JLabel("Role:"));
    addFrame.add(roleBox);
    addFrame.add(saveButton);
    addFrame.add(cancelButton);

    
    saveButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String password = new String(passwordField.getText());
            try {
                if (!Validator.isValidEmail(emailField.getText())) {
            JOptionPane.showMessageDialog(ManageUsersFrame.this, "Invalid email format.");
            return;
        }

        if (!Validator.isValidPassword(password)) {
            JOptionPane.showMessageDialog(ManageUsersFrame.this, "Password must be at least 8 characters.");
            return;
        }
        
                Connection conn = DBConnection.getConnection();
                String sql = "INSERT INTO Users (Username, Email, Password, Role) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, usernameField.getText());
                pstmt.setString(2, emailField.getText());
                pstmt.setString(3, passwordField.getText());
                pstmt.setString(4, (String) roleBox.getSelectedItem());
                pstmt.executeUpdate();
                pstmt.close();
                conn.close();
                JOptionPane.showMessageDialog(addFrame, "User added successfully!");
                addFrame.dispose(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(addFrame, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    
    cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            addFrame.dispose(); 
        }
    });

    addFrame.setVisible(true);
}


    
    private void showDeleteUserForm() {
        JFrame deleteFrame = new JFrame("Delete User");
        deleteFrame.setSize(300, 150);
        deleteFrame.setLayout(new GridLayout(3, 1, 10, 10));
        deleteFrame.setLocationRelativeTo(null);

        JTextField userIdField = new JTextField();
        JButton deleteButton = new JButton("Delete");

        deleteFrame.add(new JLabel("Enter User ID to delete:"));
        deleteFrame.add(userIdField);
        deleteFrame.add(deleteButton);

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String idText = userIdField.getText().trim();
                if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(deleteFrame, 
                    "Please enter an User ID", 
                    "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
              try {
          int userid = Integer.parseInt(userIdField.getText());
    
         Connection conn = DBConnection.getConnection();

    
           String checkSql = "SELECT COUNT(*) FROM Registrations WHERE user_id = ?";
           PreparedStatement checkStmt = conn.prepareStatement(checkSql);
           checkStmt.setInt(1, userid);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            checkStmt.close();

         if (count > 0) {
              JOptionPane.showMessageDialog(deleteFrame,
                "This user is registered for events.",
                "Cannot Delete User",
                JOptionPane.WARNING_MESSAGE);
                conn.close();
                 return;
    }

    
                  int confirm = JOptionPane.showConfirmDialog(
                  deleteFrame,
                  "Are you sure you want to delete user #" + userid + "?",
                  "Confirm Deletion",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.WARNING_MESSAGE
                  );

         if (confirm == JOptionPane.YES_OPTION) {
             String deleteSql = "DELETE FROM Users WHERE ID = ?";
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
             deleteStmt.setInt(1, userid);
             int rowsAffected = deleteStmt.executeUpdate();
             deleteStmt.close();
             conn.close();

          if (rowsAffected > 0) {
             JOptionPane.showMessageDialog(deleteFrame, "User deleted successfully!");
                 deleteFrame.dispose();
            } else {
                     JOptionPane.showMessageDialog(deleteFrame, "User ID not found.");
                     }
          } else {
          conn.close(); 
    }

                } catch (SQLException ex) {
                           ex.printStackTrace();
                    JOptionPane.showMessageDialog(deleteFrame, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
                   } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(deleteFrame,
                        "Please enter a valid numeric User ID",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    }

        }
        });
        deleteFrame.setVisible(true);
    }

    
    private void showUsersTable() {
        JFrame viewFrame = new JFrame("Users List");
        viewFrame.setSize(600, 400);
        viewFrame.setLocationRelativeTo(null);

        String[] columnNames = {"ID", "Username", "Email", "Role"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT ID, Username, Email, Role FROM Users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("ID");
                String username = rs.getString("Username");
                String email = rs.getString("Email");
                String role = rs.getString("Role");
                tableModel.addRow(new Object[]{id, username, email, role});
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(viewFrame, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        viewFrame.add(scrollPane);
        viewFrame.setVisible(true);
    }

    
    private void generateReport() {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) AS TotalUsers FROM Users";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                int totalUsers = rs.getInt("TotalUsers");
                JOptionPane.showMessageDialog(this, "Total Users: " + totalUsers);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void Back(){
        dispose();
    }
    
}


