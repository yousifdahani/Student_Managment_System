import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;


public class EnrollmentFrame extends JFrame implements ActionListener {

    JTextField txtStudentID;
    JComboBox<String> cmbCourses;     // dropdown showing all available courses
    JButton btnEnroll, btnLoadEnrollments, btnBack;
    JTable table;
    DefaultTableModel tableModel;


    int[] courseIDs;

    public EnrollmentFrame() {

        setTitle("Course Enrollment");
        setSize(700, 520);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);


        JLabel lblTitle = new JLabel("Course Enrollment");
        lblTitle.setBounds(270, 15, 220, 30);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setForeground(new Color(100, 60, 180));
        add(lblTitle);


        JLabel lblID = new JLabel("Student ID:");
        lblID.setBounds(20, 65, 90, 25);
        add(lblID);

        txtStudentID = new JTextField();
        txtStudentID.setBounds(115, 65, 100, 25);
        add(txtStudentID);


        JLabel lblCourse = new JLabel("Select Course:");
        lblCourse.setBounds(230, 65, 100, 25);
        add(lblCourse);

        cmbCourses = new JComboBox<>();
        cmbCourses.setBounds(340, 65, 230, 25);
        add(cmbCourses);

        loadCourses(); // fill the dropdown from database


        btnEnroll = new JButton("Enroll");
        btnEnroll.setBounds(585, 65, 80, 25);
        btnEnroll.setBackground(new Color(100, 60, 180));
        btnEnroll.setForeground(Color.WHITE);
        btnEnroll.addActionListener(this);
        add(btnEnroll);


        JLabel lblView = new JLabel("View Enrollments — Enter Student ID and click Load:");
        lblView.setBounds(20, 110, 380, 25);
        lblView.setFont(new Font("Arial", Font.ITALIC, 12));
        add(lblView);


        btnLoadEnrollments = new JButton("Load Enrollments");
        btnLoadEnrollments.setBounds(420, 110, 160, 25);
        btnLoadEnrollments.setBackground(new Color(2, 192, 154));
        btnLoadEnrollments.setForeground(Color.WHITE);
        btnLoadEnrollments.addActionListener(this);
        add(btnLoadEnrollments);


        btnBack = new JButton("Back");
        btnBack.setBounds(595, 110, 80, 25);
        btnBack.setBackground(Color.DARK_GRAY);
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(this);
        add(btnBack);


        String[] columns = {"Enrollment ID", "Student Name", "Course Name", "Course Code", "Enroll Date", "Grade"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(100, 60, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 150, 650, 320);
        add(scroll);

        setVisible(true);
    }


    private void loadCourses() {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT course_id, course_name, course_code FROM courses";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            java.util.ArrayList<Integer> idList = new java.util.ArrayList<>();

            while (rs.next()) {
                idList.add(rs.getInt("course_id"));
                cmbCourses.addItem(rs.getString("course_name") + " (" + rs.getString("course_code") + ")");
            }

            courseIDs = idList.stream().mapToInt(i -> i).toArray();

            con.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnEnroll) {
            enrollStudent();
        }

        if (e.getSource() == btnLoadEnrollments) {
            loadEnrollments();
        }

        if (e.getSource() == btnBack) {
            dispose();
        }
    }


    private void enrollStudent() {
        String idText = txtStudentID.getText().trim();

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int studentID = Integer.parseInt(idText);
            int selectedIndex = cmbCourses.getSelectedIndex();
            int courseID = courseIDs[selectedIndex];

            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, studentID);
            ps.setInt(2, courseID);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student enrolled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadEnrollments(); // refresh the table

            con.close();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID must be a number!", "Invalid", JOptionPane.ERROR_MESSAGE);

        } catch (SQLIntegrityConstraintViolationException ex) {
            // Duplicate enrollment — our UNIQUE constraint in DB prevents this
            JOptionPane.showMessageDialog(this, "Student is already enrolled in this course!", "Duplicate", JOptionPane.ERROR_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }


    private void loadEnrollments() {
        String idText = txtStudentID.getText().trim();

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID to view their enrollments!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0); // clear table

        try {
            int studentID = Integer.parseInt(idText);
            Connection con = DBConnection.getConnection();

            // JOIN query to get student name + course info together
            String sql =
                "SELECT e.enrollment_id, " +
                "       CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
                "       c.course_name, c.course_code, e.enroll_date, e.grade " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.student_id " +
                "JOIN courses  c ON e.course_id  = c.course_id  " +
                "WHERE e.student_id = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, studentID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("enrollment_id"),
                    rs.getString("student_name"),
                    rs.getString("course_name"),
                    rs.getString("course_code"),
                    rs.getString("enroll_date"),
                    rs.getString("grade") != null ? rs.getString("grade") : "N/A"
                };
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No enrollments found for Student ID: " + studentID, "Not Found", JOptionPane.INFORMATION_MESSAGE);
            }

            con.close();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Student ID must be a number!", "Invalid", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
