import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;



public class UpdateStudentFrame extends JFrame implements ActionListener {

    JTextField txtID, txtFirstName, txtLastName, txtEmail, txtPhone, txtAddress, txtDOB;
    JComboBox<String> cmbGender;
    JButton btnLoad, btnUpdate, btnClear, btnBack;

    public UpdateStudentFrame() {

        setTitle("Update Student");
        setSize(450, 500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);


        JLabel lblTitle = new JLabel("Update Student Information");
        lblTitle.setBounds(100, 15, 280, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitle.setForeground(new Color(30, 39, 97));
        add(lblTitle);


        addLabel("Student ID:",  30, 60);
        txtID = new JTextField();
        txtID.setBounds(170, 60, 130, 25);
        add(txtID);

        btnLoad = new JButton("Load");
        btnLoad.setBounds(310, 60, 80, 25);
        btnLoad.setBackground(new Color(30, 39, 97));
        btnLoad.setForeground(Color.WHITE);
        btnLoad.addActionListener(this);
        add(btnLoad);


        addLabel("First Name:",  30, 105);
        txtFirstName = addField(170, 105);

        addLabel("Last Name:",   30, 145);
        txtLastName  = addField(170, 145);

        addLabel("Email:",       30, 185);
        txtEmail     = addField(170, 185);

        addLabel("Phone:",       30, 225);
        txtPhone     = addField(170, 225);

        addLabel("Address:",     30, 265);
        txtAddress   = addField(170, 265);

        addLabel("DOB (YYYY-MM-DD):", 30, 305);
        txtDOB       = addField(230, 305);

        addLabel("Gender:",      30, 345);
        cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        cmbGender.setBounds(170, 345, 180, 25);
        add(cmbGender);


        setFieldsEnabled(false);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(50,  415, 100, 30);
        btnUpdate.setBackground(new Color(255, 165, 0));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setEnabled(false); // disabled until student is loaded
        btnUpdate.addActionListener(this);
        add(btnUpdate);

        btnClear = new JButton("Clear");
        btnClear.setBounds(170, 415, 100, 30);
        btnClear.setBackground(Color.GRAY);
        btnClear.setForeground(Color.WHITE);
        btnClear.addActionListener(this);
        add(btnClear);

        btnBack = new JButton("Back");
        btnBack.setBounds(290, 415, 100, 30);
        btnBack.setBackground(Color.DARK_GRAY);
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(this);
        add(btnBack);

        setVisible(true);
    }

    private void addLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 160, 25);
        add(lbl);
    }

    private JTextField addField(int x, int y) {
        JTextField field = new JTextField();
        field.setBounds(x, y, 200, 25);
        add(field);
        return field;
    }


    private void setFieldsEnabled(boolean enabled) {
        txtFirstName.setEnabled(enabled);
        txtLastName.setEnabled(enabled);
        txtEmail.setEnabled(enabled);
        txtPhone.setEnabled(enabled);
        txtAddress.setEnabled(enabled);
        txtDOB.setEnabled(enabled);
        cmbGender.setEnabled(enabled);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnLoad) {
            loadStudent();
        }

        if (e.getSource() == btnUpdate) {
            updateStudent();
        }

        if (e.getSource() == btnClear) {
            clearAll();
        }

        if (e.getSource() == btnBack) {
            dispose();
        }
    }


    private void loadStudent() {
        String idText = txtID.getText().trim();

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idText); // convert text to number
            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM students WHERE student_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                txtFirstName.setText(rs.getString("first_name"));
                txtLastName.setText(rs.getString("last_name"));
                txtEmail.setText(rs.getString("email"));
                txtPhone.setText(rs.getString("phone"));
                txtAddress.setText(rs.getString("address"));
                txtDOB.setText(rs.getString("date_of_birth") != null ? rs.getString("date_of_birth") : "");
                cmbGender.setSelectedItem(rs.getString("gender"));

                setFieldsEnabled(true);   // allow editing
                btnUpdate.setEnabled(true); // allow saving
            } else {
                JOptionPane.showMessageDialog(this, "No student found with ID: " + id, "Not Found", JOptionPane.ERROR_MESSAGE);
            }

            con.close();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID must be a number!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private void updateStudent() {
        try {
            int id = Integer.parseInt(txtID.getText().trim());
            Connection con = DBConnection.getConnection();

            String sql = "UPDATE students SET first_name=?, last_name=?, email=?, phone=?, address=?, date_of_birth=?, gender=? WHERE student_id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, txtFirstName.getText().trim());
            ps.setString(2, txtLastName.getText().trim());
            ps.setString(3, txtEmail.getText().trim());
            ps.setString(4, txtPhone.getText().trim());
            ps.setString(5, txtAddress.getText().trim());
            ps.setString(6, txtDOB.getText().trim().isEmpty() ? null : txtDOB.getText().trim());
            ps.setString(7, (String) cmbGender.getSelectedItem());
            ps.setInt(8, id); // the WHERE condition — which student to update

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Student updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            con.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void clearAll() {
        txtID.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtDOB.setText("");
        cmbGender.setSelectedIndex(0);
        setFieldsEnabled(false);
        btnUpdate.setEnabled(false);
    }
}
