package event_managment_project;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicketFrame extends JFrame {

    public TicketFrame(int eventId) {
        setTitle("Your Ticket 🎟️");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Ticket Details", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel eventLabel = new JLabel("Event ID: " + eventId);
        eventLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        eventLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        JLabel ticketIdLabel = new JLabel("Ticket No: " + generateTicketNumber());
        ticketIdLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        ticketIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton printButton = new JButton("Print Ticket 🖨️");
        printButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton emailButton = new JButton("Send to Email ✉️");
        emailButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Ticket printed successfully!", "Print", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        emailButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String email = JOptionPane.showInputDialog(null, "Enter your email:", "Send Ticket", JOptionPane.PLAIN_MESSAGE);

    
        if (email == null) {
            return;  
        }

        if (email.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a valid email address!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Ticket sent to " + email.trim() + " successfully!", "Email Sent", JOptionPane.INFORMATION_MESSAGE);
        }
    }
});



        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(eventLabel);
        panel.add(ticketIdLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(printButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(emailButton);

        add(panel);
        setVisible(true);
    }

    private String generateTicketNumber() {
        int randomNum = (int)(Math.random() * 1000000);
        return String.format("%06d", randomNum);
    }
}
