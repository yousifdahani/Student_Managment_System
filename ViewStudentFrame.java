import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;



public class ViewStudentFrame extends JFrame implements ActionListener {

    JTextField txtSearch;
    JButton btnSearch, btnShowAll, btnBack;
    JTable table;
    DefaultTableModel tableModel;
    JScrollPane scrollPane;

    public ViewStudentFrame() {

        setTitle("View / Search Students");
        setSize(750, 500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        // --- Title ---
        JLabel lblTitle = new JLabel("All Students");
        lblTitle.setBounds(310, 10, 200, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(30, 39, 97));
        add(lblTitle);

        // --- Search Label ---
        JLabel lblSearch = new JLabel("Search by Name:");
        lblSearch.setBounds(20, 55, 120, 25);
        add(lblSearch);

        // --- Search Field ---
        txtSearch = new JTextField();
        txtSearch.setBounds(145, 55, 200, 25);
        add(txtSearch);


        btnSearch = new JButton("Search");
        btnSearch.setBounds(355, 55, 90, 25);
        btnSearch.setBackground(new Color(30, 39, 97));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(this);
        add(btnSearch);


        btnShowAll = new JButton("Show All");
        btnShowAll.setBounds(455, 55, 90, 25);
        btnShowAll.setBackground(new Color(2, 192, 154));
        btnShowAll.setForeground(Color.WHITE);
        btnShowAll.addActionListener(this);
        add(btnShowAll);


        btnBack = new JButton("Back");
        btnBack.setBounds(620, 55, 90, 25);
        btnBack.setBackground(Color.DARK_GRAY);
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(this);
        add(btnBack);


        String[] columns = {"ID", "First Name", "Last Name", "Email", "Phone", "Gender", "DOB"};
        tableModel = new DefaultTableModel(columns, 0); // 0 means start with no rows
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(30, 39, 97));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(200, 220, 255)); // highlight selected row


        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 100, 700, 350);
        add(scrollPane);

        loadAllStudents();
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnSearch) {
            searchStudents();
        }

        if (e.getSource() == btnShowAll) {
            txtSearch.setText("");
            loadAllStudents();
        }

        if (e.getSource() == btnBack) {
            dispose();
        }
    }


    private void loadAllStudents() {
        tableModel.setRowCount(0);

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT student_id, first_name, last_name, email, phone, gender, date_of_birth FROM students ORDER BY student_id";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);


            while (rs.next()) {
                Object[] row = {
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("gender"),
                    rs.getString("date_of_birth")
                };
                tableModel.addRow(row);
            }

            con.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private void searchStudents() {
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name to search!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();

            // LIKE with % means "contains this keyword anywhere"
            String sql = "SELECT student_id, first_name, last_name, email, phone, gender, date_of_birth FROM students WHERE first_name LIKE ? OR last_name LIKE ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("gender"),
                    rs.getString("date_of_birth")
                };
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No students found with that name.", "Not Found", JOptionPane.INFORMATION_MESSAGE);
            }

            con.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error searching: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
