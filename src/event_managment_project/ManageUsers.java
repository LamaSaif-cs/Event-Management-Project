
package event_managment_project;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.sql.Connection;

public class ManageUsers extends JFrame{
    private JLabel viewLabel,deletLabel,AddLabel;
    private JTextField FieldView,FieldAdd,FieldDelete;
    private JButton ADD,DELETE,VIEW,Report;
    
    public ManageUsers(String role, int userid){
    setTitle("User Managment");
    setSize(300,450);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(new GridLayout(4,1));
    ADD=new JButton("ADD Users");
    DELETE= new JButton("Delete Users");
    VIEW= new JButton("View Users");
    Report=new JButton("Generate Reporet");
    add(ADD);
    add(DELETE);
    add(VIEW);
    add(Report);
    ADD.addActionListener(new ADDUSER());
    DELETE.addActionListener(new DELETEUSER());
    VIEW.addActionListener(new VIEWUSER());
    Report.addActionListener(new REPORT());
    }
    
    public class ADDUSER implements ActionListener{

       
        public void actionPerformed(ActionEvent ae) {
            
        }
}
        public class DELETEUSER implements ActionListener{

        public void actionPerformed(ActionEvent ae) {
            
        }
}
            public class VIEWUSER implements ActionListener{
        public void actionPerformed(ActionEvent ae) { 
        }


}
                public class REPORT implements ActionListener{

        public void actionPerformed(ActionEvent ae) {
            
        }


}
}
