package event_managment_project;

import utils.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboardFrame extends JFrame {

    private JTable eventsTable, usersTable;
    private DefaultTableModel eventsModel, usersModel;
    private JButton deleteEventButton, deleteUserButton, refreshButton;

    public AdminDashboardFrame() {
        setTitle("Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        
        JPanel eventsPanel = new JPanel(new BorderLayout());
        eventsModel = new DefaultTableModel(new String[]{"ID", "Title", "Date", "Location", "Seats"}, 0);
        eventsTable = new JTable(eventsModel);
        eventsPanel.add(new JScrollPane(eventsTable), BorderLayout.CENTER);
        deleteEventButton = new JButton("Delete Selected Event");
        deleteEventButton.addActionListener(e -> deleteSelectedEvent());
        eventsPanel.add(deleteEventButton, BorderLayout.SOUTH);

        tabs.add("Events", eventsPanel);

        
        JPanel usersPanel = new JPanel(new BorderLayout());
        usersModel = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Role"}, 0);
        usersTable = new JTable(usersModel);
        usersPanel.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        deleteUserButton = new JButton("Delete Selected User");
        deleteUserButton.addActionListener(e -> deleteSelectedUser());
        usersPanel.add(deleteUserButton, BorderLayout.SOUTH);

        tabs.add("Users", usersPanel);

        
        add(tabs, BorderLayout.CENTER);

     
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            loadEvents();
            loadUsers();
        });
        add(refreshButton, BorderLayout.SOUTH);

       
        loadEvents();
        loadUsers();

        setVisible(true);
    }

    private void loadEvents() {
        eventsModel.setRowCount(0); 
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, title, event_date, location, available_seats FROM events")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                eventsModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("event_date"),
                        rs.getString("location"),
                        rs.getInt("available_seats")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load events.");
        }
    }

    private void loadUsers() {
        usersModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, name, email, role FROM users")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                usersModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load users.");
        }
    }

    private void deleteSelectedEvent() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to delete.");
            return;
        }
        int eventId = (int) eventsModel.getValueAt(selectedRow, 0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM events WHERE id = ?")) {
            stmt.setInt(1, eventId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Event deleted successfully.");
            loadEvents(); 
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete event.");
        }
    }

    private void deleteSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
            return;
        }
        int userId = (int) usersModel.getValueAt(selectedRow, 0);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User deleted successfully.");
            loadUsers();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete user.");
        }
    }
}

