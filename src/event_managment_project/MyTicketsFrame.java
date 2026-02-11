package event_managment_project;

import utils.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MyTicketsFrame extends JFrame {

    public MyTicketsFrame(int userId) {  
        setTitle("My Tickets 🎟️");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Ticket ID", "Event Title", "Date", "Location"}, 0);

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT t.id, t.ticket_number, e.title, e.event_date, e.location, t.create_at " +
                "FROM tickets t " +
                "JOIN events e ON t.event_id = e.id " +
                "WHERE t.user_id = ?"  
             )) {

            ps.setInt(1, userId);  

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("event_date"),
                            rs.getString("location")
                    });
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load tickets.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        add(scrollPane, BorderLayout.CENTER);
    }
}
