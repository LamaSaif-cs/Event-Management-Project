package event_managment_project;

import utils.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class EventListFrame extends JFrame {

    private final JTable eventTable = new JTable();
    private final JTextField searchField = new JTextField(15); 
    private final JButton searchButton = new JButton("Search");
    private final JButton bookButton = new JButton("Book Ticket 🎟️"); 
    private final String currentUserName;
    private final boolean organizerView;
    private final int currentUserId;

    public EventListFrame(int currentUserId, String userName, boolean organizerView) {
        this.currentUserName = userName;
        this.organizerView = organizerView;
        this.currentUserId = currentUserId;

        setTitle(organizerView ? "My Events" : "All Events");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Search Event:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        add(new JScrollPane(eventTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(bookButton);
        add(bottomPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> loadEvents());
        bookButton.addActionListener(e -> bookSelectedTicket());

        loadEvents();           
        setVisible(true);
    }

    private void loadEvents() {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Title", "Date", "Location", "Seats", "Description", "CreatedBy"}, 0);

        String sql;
        if (organizerView) {
            sql = "SELECT id, title, event_date, location, available_seats, description, createdby FROM events WHERE createdby = ?";
        } else {
            sql = "SELECT id, title, event_date, location, available_seats, description, createdby FROM events";
        }

        String searchText = searchField.getText().trim();
        boolean hasSearch = !searchText.isEmpty();

        if (hasSearch) {
            if (organizerView) {
                sql += " AND title LIKE ?";
            } else {
                sql += " WHERE title LIKE ?";
            }
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (organizerView) {
                ps.setString(paramIndex++, currentUserName);
            }
            if (hasSearch) {
                ps.setString(paramIndex, "%" + searchText + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("event_date"),
                            rs.getString("location"),
                            rs.getInt("available_seats"),
                            rs.getString("description"),
                            rs.getString("createdby")
                    });
                }
            }

            eventTable.setModel(model);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load events.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
       
private void bookSelectedTicket() {
    int selectedRow = eventTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an event to book.", "No Event Selected", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int eventId = (int) eventTable.getValueAt(selectedRow, 0); 
    int availableSeats = (int) eventTable.getValueAt(selectedRow, 4); 

    if (availableSeats <= 0) {
        JOptionPane.showMessageDialog(this, "No seats available for this event.", "Fully Booked", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int userId = currentUserId;
    String ticketNumber = generateTicketNumber();
    Timestamp createAt = new Timestamp(System.currentTimeMillis());

    try (Connection conn = DBConnection.getConnection()) {

        
        PreparedStatement checkStmt = conn.prepareStatement(
            "SELECT COUNT(*) FROM registrations WHERE user_id = ? AND event_id = ?"
        );
        checkStmt.setInt(1, userId);
        checkStmt.setInt(2, eventId);

        ResultSet rsCheck = checkStmt.executeQuery();
        if (rsCheck.next() && rsCheck.getInt(1) > 0) {
            JOptionPane.showMessageDialog(this, "You have already booked a ticket for this event.", "Already Registered", JOptionPane.WARNING_MESSAGE);
            return;
        }

        conn.setAutoCommit(false); 

        
        PreparedStatement psUpdate = conn.prepareStatement(
            "UPDATE events SET available_seats = available_seats - 1 WHERE id = ? AND available_seats > 0"
        );
        psUpdate.setInt(1, eventId);
        int rowsUpdated = psUpdate.executeUpdate();

        if (rowsUpdated == 0) {
            conn.rollback();
            JOptionPane.showMessageDialog(this, "Failed to book seat. Maybe no seats left.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        
        PreparedStatement psInsertTicket = conn.prepareStatement(
            "INSERT INTO tickets (ticket_number, event_id, user_id, create_at) VALUES (?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        );
        psInsertTicket.setString(1, ticketNumber);
        psInsertTicket.setInt(2, eventId);
        psInsertTicket.setInt(3, userId);
        psInsertTicket.setTimestamp(4, createAt);
        psInsertTicket.executeUpdate();

        ResultSet generatedKeys = psInsertTicket.getGeneratedKeys();
        int ticketId = -1;
        if (generatedKeys.next()) {
            ticketId = generatedKeys.getInt(1);
        } else {
            conn.rollback();
            throw new SQLException("Failed to retrieve ticket ID.");
        }

   
        PreparedStatement psInsertRegistration = conn.prepareStatement(
            "INSERT INTO registrations (user_id, event_id, ticket_id) VALUES (?, ?, ?)"
        );
        psInsertRegistration.setInt(1, userId);
        psInsertRegistration.setInt(2, eventId);
        psInsertRegistration.setInt(3, ticketId);
        psInsertRegistration.executeUpdate();

        conn.commit(); 

        JOptionPane.showMessageDialog(this, "Seat booked, ticket and registration saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        new TicketFrame(eventId); 
        loadEvents(); 

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    }

    private String generateTicketNumber() {
        return "TICKET-" + System.currentTimeMillis();
    }
}
