import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddStudentFrame extends JFrame implements ActionListener {

    // Input fields
    JTextField txtFirstName, txtLastName, txtEmail, txtPhone, txtAddress, txtDOB;
    JComboBox<String> cmbGender;   // dropdown for gender
    JButton btnSave, btnClear, btnBack;
    JLabel lblTitle;

    public AddStudentFrame() {

        setTitle("Add New Student");
        setSize(450, 450);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        // --- Title ---
        lblTitle = new JLabel("Add New Student");
        lblTitle.setBounds(140, 15, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(30, 39, 97));
        add(lblTitle);


        addLabel("First Name:",               30,  60);
        txtFirstName = addField(170, 60);

        addLabel("Last Name:",                30,  100);
        txtLastName  = addField(170, 100);

        addLabel("Email:",                    30,  140);
        txtEmail     = addField(170, 140);

        addLabel("Phone:",                    30,  180);
        txtPhone     = addField(170, 180);

        addLabel("Address:",                  30,  220);
        txtAddress   = addField(170, 220);

        addLabel("Date of Birth (YYYY-MM-DD):", 30, 260);
        txtDOB       = addField(270, 260);

        // --- Gender Dropdown ---
        addLabel("Gender:",                   30,  300);
        cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cmbGender.setBounds(170, 300, 200, 25);
        add(cmbGender);

        // --- Buttons ---
        btnSave = new JButton("Save");
        btnSave.setBounds(60,  370, 90, 30);
        btnSave.setBackground(new Color(2, 192, 154));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(this);
        add(btnSave);

        btnClear = new JButton("Clear");
        btnClear.setBounds(175, 370, 90, 30);
        btnClear.setBackground(new Color(255, 165, 0));
        btnClear.setForeground(Color.WHITE);
        btnClear.addActionListener(this);
        add(btnClear);

        btnBack = new JButton("Back");
        btnBack.setBounds(290, 370, 90, 30);
        btnBack.setBackground(Color.DARK_GRAY);
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(this);
        add(btnBack);

        setVisible(true);
    }


    private void addLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 160, 25);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        add(lbl);
    }


    private JTextField addField(int x, int y) {
        JTextField field = new JTextField();
        field.setBounds(x, y, 200, 25);
        add(field);
        return field;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnSave) {
            saveStudent();
        }

        if (e.getSource() == btnClear) {
            clearFields();
        }

        if (e.getSource() == btnBack) {
            dispose();
        }
    }


    private void saveStudent() {

        // Read all values from the form
        String firstName = txtFirstName.getText().trim();
        String lastName  = txtLastName.getText().trim();
        String email     = txtEmail.getText().trim();
        String phone     = txtPhone.getText().trim();
        String address   = txtAddress.getText().trim();
        String dob       = txtDOB.getText().trim();
        String gender    = (String) cmbGender.getSelectedItem();


        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name, Last Name, and Email are required!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();


            String sql = "INSERT INTO students (first_name, last_name, email, phone, address, date_of_birth, gender) VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, email);
            ps.setString(4, phone);
            ps.setString(5, address);
            ps.setString(6, dob.isEmpty() ? null : dob); // if empty, store NULL
            ps.setString(7, gender);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            }

            con.close();

        } catch (SQLIntegrityConstraintViolationException ex) {

            JOptionPane.showMessageDialog(this, "This email is already registered!", "Duplicate Email", JOptionPane.ERROR_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // --- Resets all fields back to empty ---
    private void clearFields() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtDOB.setText("");
        cmbGender.setSelectedIndex(0);
    }
}
