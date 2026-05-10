import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



public class DashboardFrame extends JFrame implements ActionListener {


    JButton btnAddStudent, btnViewStudent, btnUpdateStudent,
            btnDeleteStudent, btnEnrollment, btnLogout;
    JLabel  lblTitle, lblWelcome;

    public DashboardFrame() {


        setTitle("Student Management System - Dashboard");
        setSize(500, 450);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 245)); // light gray background


        lblTitle = new JLabel("Student Management System");
        lblTitle.setBounds(100, 20, 320, 35);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(new Color(30, 39, 97));
        add(lblTitle);

        lblWelcome = new JLabel("Select an option below:");
        lblWelcome.setBounds(170, 60, 200, 25);
        lblWelcome.setFont(new Font("Arial", Font.PLAIN, 13));
        lblWelcome.setForeground(Color.GRAY);
        add(lblWelcome);


        btnAddStudent = makeButton("Add Student", 150, 110, new Color(2, 192, 154));
        add(btnAddStudent);


        btnViewStudent = makeButton("View / Search Students", 110, 165, new Color(30, 39, 97));
        add(btnViewStudent);


        btnUpdateStudent = makeButton("Update Student", 140, 220, new Color(255, 165, 0));
        add(btnUpdateStudent);


        btnDeleteStudent = makeButton("Delete Student", 140, 275, new Color(200, 80, 80));
        add(btnDeleteStudent);


        btnEnrollment = makeButton("Course Enrollment", 125, 330, new Color(100, 60, 180));
        add(btnEnrollment);


        btnLogout = new JButton("Logout");
        btnLogout.setBounds(380, 10, 90, 30);
        btnLogout.setBackground(Color.DARK_GRAY);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(this);
        add(btnLogout);

        setVisible(true);
    }


    private JButton makeButton(String text, int x, int y, Color color) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 220, 40);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setFocusPainted(false);    // removes the dotted border on focus
        btn.addActionListener(this);   // register click listener
        return btn;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnAddStudent) {
            new AddStudentFrame();
        }

        if (e.getSource() == btnViewStudent) {
            new ViewStudentFrame();
        }

        if (e.getSource() == btnUpdateStudent) {
            new UpdateStudentFrame();
        }

        if (e.getSource() == btnDeleteStudent) {
            new DeleteStudentFrame();
        }

        if (e.getSource() == btnEnrollment) {
            new EnrollmentFrame();}

        if (e.getSource() == btnLogout) {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                new LoginFrame();
                dispose();
            }
        }
    }
}

