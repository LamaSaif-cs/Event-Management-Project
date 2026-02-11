package event_managment_project;

import utils.DBConnection;
import utils.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterFrame extends JFrame {
    private JTextField usernameField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JButton registerBtn, backBtn;

    public RegisterFrame() {
        setTitle("Register");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        roleBox = new JComboBox<>(new String[]{"attendee", "organizer","Admin"});
        registerBtn = new JButton("Register");
        backBtn = new JButton("Back to Login");

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleBox);
        panel.add(registerBtn);
        panel.add(backBtn);

        add(panel);

        registerBtn.addActionListener(e -> register());
        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        setVisible(true);
    }

    private void register() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if (!Validator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.");
            return;
        }

        if (!Validator.isValidPassword(password)) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
 
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO users(username, email, password, role) VALUES (?, ?, ?, ?)");
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration Successful!");
            dispose();
            new LoginFrame().setVisible(true);

        } catch (SQLException ex) {
            if (ex.getMessage().contains("UNIQUE constraint failed")) {
                JOptionPane.showMessageDialog(this, "Email already registered.");
            } else {
                ex.printStackTrace();
            }
        }
    }
}

