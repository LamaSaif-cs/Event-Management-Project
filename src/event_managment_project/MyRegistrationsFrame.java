package event_managment_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import utils.DBConnection;

public class MyRegistrationsFrame extends JFrame {

    private JTable eventTable;
    private JButton  toggleViewButton;
    private boolean showingRegistered = false;
    private int currentUserId;

    public MyRegistrationsFrame(int userId) {
        this.currentUserId = userId;
        setTitle("Register For Events");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        eventTable = new JTable();
        add(new JScrollPane(eventTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        toggleViewButton = new JButton("Show My Registered Events");

      
        toggleViewButton.addActionListener(e -> {
            showingRegistered = !showingRegistered;
            toggleViewButton.setText(showingRegistered ? "Show Available Events" : "Show My Registered Events");
            loadEvents();
        });

        buttonPanel.add(toggleViewButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadEvents();
        setVisible(true);
    }

    private void loadEvents() {
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Title", "Date", "Location", "Seats", "Description","CreatedBy"}, 0);

        String sql = showingRegistered
                ? "SELECT e.id, e.title, e.event_date, e.location, e.available_seats, e.description,e.createdby " +
                  "FROM events e JOIN registrations r ON e.id = r.event_id WHERE r.user_id = ?"
                : "SELECT e.id, e.title, e.event_date, e.location, e.available_seats, e.description,e.createdby " +
                  "FROM events e WHERE e.id NOT IN (SELECT event_id FROM registrations WHERE user_id = ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("event_date"),
                        rs.getString("location"),
                        rs.getInt("available_seats"),
                        rs.getString("description"),
                        rs.getString("CreatedBy")
                });
            }

            eventTable.setModel(model);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load events.");
        }
    }
}


