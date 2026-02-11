package event_managment_project;

import javax.swing.SwingUtilities;

public class MAIN {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });
     }
}

