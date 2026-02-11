package event_managment_project;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {
    private String name;
    private String role;
    private int userid;
   
    public Dashboard(String name,String role, int userid) {
        this.name=name;
        this.role = role;
        this.userid = userid;

        setTitle("Dashboard - " + role.toUpperCase());
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel welcomeLabel = new JLabel("Welcome, " + name + " (" + role + ")");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel contentPanel = new JPanel(new GridLayout(0, 1, 10, 10));

        
        switch (role) {
            case "Admin":
                contentPanel.add(createButton("Manage Users"));
                contentPanel.add(createButton("Manage Events"));
                contentPanel.add(createButton("View Reports"));
                break;

            case "organizer":
                contentPanel.add(createButton("Create Event"));
                contentPanel.add(createButton("My Events"));
                break;

            case "attendee":
                contentPanel.add(createButton("Browse Events"));
                contentPanel.add(createButton("My Tickets"));


                break;
        }

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(welcomeLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(logoutBtn, BorderLayout.SOUTH);

        add(panel);
    }

    private JButton createButton(String text) {
    JButton btn = new JButton(text);
    btn.addActionListener(e -> {
        switch (text) {
            case "Manage Users":
             new ManageUsersFrame()
                     .setVisible(true);break;
            case "Manage Events":
                new ManageEventsFrame()
                        .setVisible(true);break;
            case "View Reports":
                new ViewReportsFrame()
                        .setVisible(true);break;
            case "Create Event":
                new EventCreationFrame(name)  
                        .setVisible(true);
                break;
            case "My Events":
                new EventListFrame(userid,name, true)   
                        .setVisible(true);
                break;
            case "Browse Events":
                new EventListFrame(userid,role, false) 
                        .setVisible(true);
                break;
            case "My Tickets":
               new MyTicketsFrame(userid)
                       .setVisible(true);
                break;

        }
    });
    return btn;
}

}

