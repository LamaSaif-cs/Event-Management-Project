
package event_managment_project;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import utils.DBConnection;
import java.time.LocalDate;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EventCreationFrame extends JFrame {
    private JTextField nameField, dateField, locationField,setsField,createdbyField;
    private JTextArea descriptionArea;
    private JButton createButton , CancelButton;
    private String organizerName;

    public EventCreationFrame(String name) {
        this.organizerName = name;

        setTitle("Create Event");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(8, 2));
        
        add(new JLabel("Event Name:"));//row 1,c1
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Date (DD-MM-YYYY):"));
        dateField = new JTextField();
        add(dateField);

        add(new JLabel("Location:"));
        locationField = new JTextField();
        add(locationField);

        add(new JLabel("Number of Seats:"));
        setsField=new JTextField();
        add(setsField);
        
        add(new JLabel("Description:"));
        descriptionArea = new JTextArea();
        add(descriptionArea);
        
        add(new JLabel("Created By:"));
        createdbyField=new JTextField();
        add(createdbyField);

        createButton = new JButton("Create");
        createButton.addActionListener(e -> createEvent());
        add(createButton);
        
        CancelButton = new JButton("Cancel");
        add(CancelButton);
        CancelButton.addActionListener(e ->{dispose();});
        
        
        setVisible(true);
    }
      private static final DateTimeFormatter INPUT_FMT =
            DateTimeFormatter.ofPattern("d-M-yyyy"); 
    private void createEvent() {
        Date sqlDate;

      
        try {
            LocalDate ld = LocalDate.parse(dateField.getText().trim(), INPUT_FMT);
            sqlDate = Date.valueOf(ld);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Date must be in DD-MM-YYYY format", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        
        if (!createdbyField.getText().trim().equalsIgnoreCase(organizerName)) {
            JOptionPane.showMessageDialog(this, "Created By field should only contain your assigned name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

      
        int sets;
        try {
            sets = Integer.parseInt(setsField.getText().trim());
            if (sets < 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive number of seats.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for seats.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

      
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO events (title, event_date, location, available_seats, description, CreatedBy) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nameField.getText().trim());
            stmt.setDate(2, sqlDate);
            stmt.setString(3, locationField.getText().trim());
            stmt.setInt(4, sets);
            stmt.setString(5, descriptionArea.getText().trim());
            stmt.setString(6, createdbyField.getText().trim());
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Event created successfully!");
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to create event.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

