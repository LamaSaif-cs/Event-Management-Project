package event_managment_project;

import utils.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginBtn, registerBtn;

    public LoginFrame() {
        setTitle("Login");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginBtn = new JButton("Login");
        registerBtn = new JButton("Register");

        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginBtn);
        panel.add(registerBtn);

        add(panel);

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> {
            dispose();
            new RegisterFrame().setVisible(true);
        });
    }

    private void login() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?");
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();
                new Dashboard(rs.getString("username"),rs.getString("role"),rs.getInt("id")).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

