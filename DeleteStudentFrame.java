import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;



public class DeleteStudentFrame extends JFrame implements ActionListener {

    JTextField txtID;
    JButton btnLoad, btnDelete, btnBack;
    JTextArea txtPreview;

    public DeleteStudentFrame() {

        setTitle("Delete Student");
        setSize(420, 370);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);


        JLabel lblTitle = new JLabel("Delete Student");
        lblTitle.setBounds(150, 15, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(200, 80, 80)); // red to signal danger
        add(lblTitle);


        JLabel lblID = new JLabel("Enter Student ID:");
        lblID.setBounds(30, 65, 130, 25);
        add(lblID);

        txtID = new JTextField();
        txtID.setBounds(170, 65, 120, 25);
        add(txtID);

        btnLoad = new JButton("Load");
        btnLoad.setBounds(300, 65, 80, 25);
        btnLoad.setBackground(new Color(30, 39, 97));
        btnLoad.setForeground(Color.WHITE);
        btnLoad.addActionListener(this);
        add(btnLoad);


        JLabel lblPreview = new JLabel("Student Info Preview:");
        lblPreview.setBounds(30, 110, 180, 25);
        lblPreview.setFont(new Font("Arial", Font.BOLD, 12));
        add(lblPreview);


        txtPreview = new JTextArea();
        txtPreview.setEditable(false);
        txtPreview.setBackground(new Color(245, 245, 245));
        txtPreview.setFont(new Font("Courier New", Font.PLAIN, 12)); // monospace font looks clean
        txtPreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JScrollPane scroll = new JScrollPane(txtPreview);
        scroll.setBounds(30, 140, 350, 150);
        add(scroll);


        btnDelete = new JButton("Delete Student");
        btnDelete.setBounds(70, 310, 140, 30);
        btnDelete.setBackground(new Color(200, 80, 80));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setEnabled(false); // disabled until student is loaded
        btnDelete.addActionListener(this);
        add(btnDelete);

        btnBack = new JButton("Back");
        btnBack.setBounds(240, 310, 100, 30);
        btnBack.setBackground(Color.DARK_GRAY);
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(this);
        add(btnBack);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnLoad) {
            loadStudentPreview();
        }

        if (e.getSource() == btnDelete) {
            deleteStudent();
        }

        if (e.getSource() == btnBack) {
            dispose();
        }
    }


    private void loadStudentPreview() {
        String idText = txtID.getText().trim();

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM students WHERE student_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Build a readable preview text
                String preview =
                    "ID      : " + rs.getInt("student_id")       + "\n" +
                    "Name    : " + rs.getString("first_name")     + " " + rs.getString("last_name") + "\n" +
                    "Email   : " + rs.getString("email")          + "\n" +
                    "Phone   : " + rs.getString("phone")          + "\n" +
                    "Gender  : " + rs.getString("gender")         + "\n" +
                    "DOB     : " + rs.getString("date_of_birth")  + "\n" +
                    "Address : " + rs.getString("address");

                txtPreview.setText(preview);
                btnDelete.setEnabled(true); // now allow deletion

            } else {
                txtPreview.setText("No student found with ID: " + id);
                btnDelete.setEnabled(false);
            }

            con.close();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID must be a number!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private void deleteStudent() {

        // Ask admin to confirm before deleting (safety check)
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this student?\nThis action cannot be undone!",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int id = Integer.parseInt(txtID.getText().trim());
            Connection con = DBConnection.getConnection();

            // DELETE query — the ON DELETE CASCADE in database will also remove enrollments
            String sql = "DELETE FROM students WHERE student_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                txtPreview.setText("");
                txtID.setText("");
                btnDelete.setEnabled(false);
            }

            con.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
