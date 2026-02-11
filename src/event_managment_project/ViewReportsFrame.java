package event_managment_project;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import utils.DBConnection;

public class ViewReportsFrame extends JFrame {
    private JComboBox<String> reportTypeCombo;
    private JTable reportTable;
    private JButton generateButton, backButton;

    public ViewReportsFrame() {
        setTitle("Admin Reports Viewer");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        reportTypeCombo = new JComboBox<>(new String[]{
            "Events Summary",
            "User Statistics",
            "Attendance Report",
            "Revenue Summary"
        });
        reportTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        
        generateButton = new JButton("Generate Report");
        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        topPanel.add(new JLabel("Select Report Type:"));
        topPanel.add(reportTypeCombo);
        topPanel.add(generateButton);
        
        
        reportTable = new JTable();
        reportTable.setFont(new Font("Arial", Font.PLAIN, 14));
        reportTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(reportTable);
        
       
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        bottomPanel.add(backButton);
        
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
      
        generateButton.addActionListener(e -> generateSelectedReport());
        backButton.addActionListener(e -> dispose());
        
        setVisible(true);
    }
    
    private void generateSelectedReport() {
        String selectedReport = (String) reportTypeCombo.getSelectedItem();
        
        try {
            switch (selectedReport) {
                case "Events Summary":
                    showEventsSummary();
                    break;
                case "User Statistics":
                    showUserStatistics();
                    break;
                case "Attendance Report":
                    showAttendanceReport();
                    break;
                case "Revenue Summary":
                    showRevenueSummary();
                    break;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Database Error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showEventsSummary() throws SQLException {
        String[] columns = {"Event ID", "Event Name", "Date", "Location", "Attendees", "Available Seats"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT e.ID, e.title, e.event_date, e.location, " +
                 "COUNT(r.user_id) as attendees, e.available_seats " +
                 "FROM events e LEFT JOIN registrations r ON e.ID = r.event_id " +
                 "GROUP BY e.ID, e.title, e.event_date, e.location, e.available_seats")) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ID"),
                    rs.getString("title"),
                    rs.getDate("event_date"),
                    rs.getString("location"),
                    rs.getInt("attendees"),
                    rs.getInt("available_seats")
                });
            }
        }
        
        reportTable.setModel(model);
    }
    
    private void showUserStatistics() throws SQLException {
    String[] columns = {"User Type", "Count"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(
             "SELECT Role as user_type, COUNT(*) as count " +
             "FROM Users GROUP BY Role")) {
        
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("user_type"),
                rs.getInt("count")
            });
        }
    }
    
    reportTable.setModel(model);
}
    
    private void showAttendanceReport() throws SQLException {
    String[] columns = {"Event", "Date", "Total Capacity", "Attendees", "Attendance %"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(
             "SELECT e.[ID], e.[title] as event, e.[event_date], " +
             "e.[available_seats] as capacity, " +
             "COUNT(r.[user_id]) as attendees, " +
             "CASE WHEN e.[available_seats] > 0 THEN " +
             "ROUND(COUNT(r.[user_id])*100.0/e.[available_seats], 2) ELSE 0 END as attendance_percent " +
             "FROM [events] e LEFT JOIN [registrations] r ON e.[ID] = r.[event_id] " +
             "GROUP BY e.[ID], e.[title], e.[event_date], e.[available_seats]")) {
        
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("event"),
                rs.getDate("event_date"),
                rs.getInt("capacity"),
                rs.getInt("attendees"),
                rs.getDouble("attendance_percent") + "%"
            });
        }
    } catch (SQLException ex) {
        
        if (ex.getMessage().contains("division by zero")) {
            JOptionPane.showMessageDialog(this,
                "Error: Some events have zero capacity causing division error",
                "Data Error",
                JOptionPane.ERROR_MESSAGE);
        }
        throw ex; 
    }
    
    reportTable.setModel(model);
}
    
    private void showRevenueSummary() throws SQLException {
    String[] columns = {"Event", "Date", "Attendees"};
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    
    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(
             "SELECT e.title as event, e.event_date, " +
             "COUNT(r.user_id) as attendees " +
             "FROM events e LEFT JOIN registrations r ON e.ID = r.event_id " +
             "GROUP BY e.title, e.event_date")) {
        
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("event"),
                rs.getDate("event_date"),
                rs.getInt("attendees")
            });
        }
    }
    
    reportTable.setModel(model);
}
}