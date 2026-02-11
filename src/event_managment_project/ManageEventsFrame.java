package event_managment_project;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import utils.DBConnection;

public class ManageEventsFrame extends JFrame {
    private JButton addButton, deleteButton, viewButton, reportButton, backButton;
   private static final DateTimeFormatter INPUT_FMT =
            DateTimeFormatter.ofPattern("d-M-yyyy"); 
     Date sqlDate;
    public ManageEventsFrame() {
        setTitle("Manage Events");
        setSize(400, 400);
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10));
        setVisible(true);

        addButton = new JButton("Add Event");
        deleteButton = new JButton("Delete Event");
        viewButton = new JButton("View Events");
        reportButton = new JButton("Generate Report");
        backButton = new JButton("Back");

        add(addButton);
        add(deleteButton);
        add(viewButton);
        add(reportButton);
        add(backButton);

       
        addButton.addActionListener(e -> showAddEventForm());
        deleteButton.addActionListener(e -> showDeleteEventForm());
        viewButton.addActionListener(e -> showEventsTable());
        reportButton.addActionListener(e -> generateReport());
        backButton.addActionListener(e -> dispose());
    }

    
   private void showAddEventForm() {
    JFrame addFrame = new JFrame("Add Event");
    addFrame.setSize(400, 400);
    addFrame.setLayout(new GridLayout(7, 2, 10, 10));
    addFrame.setLocationRelativeTo(null);

    JTextField nameField = new JTextField();
    JTextField dateField = new JTextField();
    JTextField locationField = new JTextField();
    JTextField seatsField = new JTextField();
    JTextField createdby = new JTextField();
    JTextArea descriptionArea = new JTextArea();
    JScrollPane descriptionScroll = new JScrollPane(descriptionArea);

    JButton saveButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");

    addFrame.add(new JLabel("Event Name:"));
    addFrame.add(nameField);
    addFrame.add(new JLabel("Date (DD-MM-YYYY):"));
    addFrame.add(dateField);
    addFrame.add(new JLabel("Location:"));
    addFrame.add(locationField);
    addFrame.add(new JLabel("Available Seats:"));
    addFrame.add(seatsField);
    addFrame.add(new JLabel("Description:"));
    addFrame.add(descriptionScroll);
    addFrame.add(new JLabel("Created By:"));
    addFrame.add(createdby);
    addFrame.add(saveButton);
    addFrame.add(cancelButton);

    saveButton.addActionListener(e -> {
        Date sqlDate;
        try {
            LocalDate ld = LocalDate.parse(dateField.getText().trim(), INPUT_FMT);
            sqlDate = Date.valueOf(ld);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(addFrame, "Date must be in DD-MM-YYYY format", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int seats;
        try {
            seats = Integer.parseInt(seatsField.getText().trim());
            if (seats < 0) {
                JOptionPane.showMessageDialog(addFrame, "Please enter a positive number of seats.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(addFrame, "Please enter a valid number for seats.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO events (title, event_date, location, available_seats, description, createdby) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nameField.getText().trim());
            pstmt.setDate(2, sqlDate);
            pstmt.setString(3, locationField.getText().trim());
            pstmt.setInt(4, seats);
            pstmt.setString(5, descriptionArea.getText().trim());
            pstmt.setString(6, createdby.getText().trim());
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(addFrame, "Event added successfully!");
            addFrame.dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(addFrame, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    cancelButton.addActionListener(e -> addFrame.dispose());
    addFrame.setVisible(true);
}


    
    private void showDeleteEventForm() {
    JFrame deleteFrame = new JFrame("Delete Event");
    deleteFrame.setSize(350, 200);  
    deleteFrame.setLayout(new GridLayout(3, 1, 10, 10));
    deleteFrame.setLocationRelativeTo(null);

    JLabel instructionLabel = new JLabel("Enter Event ID to delete:");
    JTextField eventIdField = new JTextField();
    JButton deleteButton = new JButton("Delete");

    deleteFrame.add(instructionLabel);
    deleteFrame.add(eventIdField);
    deleteFrame.add(deleteButton);

    deleteButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String idText = eventIdField.getText().trim();
            
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(deleteFrame, 
                    "Please enter an Event ID", 
                    "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int eventId = Integer.parseInt(idText);
              
                int confirm = JOptionPane.showConfirmDialog(
                    deleteFrame,
                    "Are you sure you want to delete event #" + eventId + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Connection conn = DBConnection.getConnection();
                        
                        // First check if event exists
                        String checkSql = "SELECT [ID] FROM [events] WHERE [ID] = ?";                        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                        checkStmt.setInt(1, eventId);
                        ResultSet rs = checkStmt.executeQuery();
                        
                        if (!rs.next()) {
                            JOptionPane.showMessageDialog(deleteFrame,
                                "Event ID " + eventId + " not found",
                                "Not Found",
                                JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        
                        
                        String deleteSql = "DELETE FROM [events] WHERE [ID] = ?";                        PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                        deleteStmt.setInt(1, eventId);
                        int rowsAffected = deleteStmt.executeUpdate();
                        
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(deleteFrame,
                                "Event #" + eventId + " deleted successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                            deleteFrame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(deleteFrame,
                                "Failed to delete event #" + eventId,
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                        
                        
                        deleteStmt.close();
                        checkStmt.close();
                        conn.close();
                        
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(deleteFrame,
                            "Database Error: " + ex.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(deleteFrame,
                    "Please enter a valid numeric Event ID",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    deleteFrame.setVisible(true);
}

    private void showEventsTable() {
    try {
        Connection conn = DBConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM events");
        
    
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i-1] = metaData.getColumnName(i);
        }
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i-1] = rs.getObject(i);
            }
            model.addRow(row);
        }
        
        JTable table = new JTable(model);
        JFrame frame = new JFrame("Events");
        frame.add(new JScrollPane(table));
        frame.pack();
        frame.setVisible(true);
        
        rs.close();
        stmt.close();
        conn.close();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Error loading events: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

  
    private void generateReport() {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) AS TotalEvents, SUM(available_seats) AS TotalSeats FROM events";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                int totalEvents = rs.getInt("TotalEvents");
                int totalSeats = rs.getInt("TotalSeats");
                String report = "Events Report:\n\n" +
                               "Total Events: " + totalEvents + "\n" +
                               "Total Available Seats: " + totalSeats;
                JOptionPane.showMessageDialog(this, report, "Events Report", JOptionPane.INFORMATION_MESSAGE);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
        
}