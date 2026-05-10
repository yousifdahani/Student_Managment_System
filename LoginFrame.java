import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;



public class LoginFrame extends JFrame implements ActionListener {


    JLabel lblTitle, lblUser, lblPass;
    JTextField txtUsername;
    JPasswordField txtPassword;
    JButton btnLogin, btnClear;


    public LoginFrame() {


        setTitle("Student Management System - Login");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(30, 39, 97));


        lblTitle = new JLabel("Student Management System");
        lblTitle.setBounds(60, 20, 300, 30);
        lblTitle.setForeground(Color.WHITE);          // white text
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitle);


        lblUser = new JLabel("Username:");
        lblUser.setBounds(60, 80, 100, 25);
        lblUser.setForeground(Color.WHITE);
        add(lblUser);


        txtUsername = new JTextField();
        txtUsername.setBounds(170, 80, 160, 25);
        add(txtUsername);

        // --- Password Label ---
        lblPass = new JLabel("Password:");
        lblPass.setBounds(60, 130, 100, 25);
        lblPass.setForeground(Color.WHITE);
        add(lblPass);

        // --- Password Field (shows dots instead of characters) ---
        txtPassword = new JPasswordField();
        txtPassword.setBounds(170, 130, 160, 25);
        add(txtPassword);

        // --- Login Button ---
        btnLogin = new JButton("Login");
        btnLogin.setBounds(90, 190, 90, 30);
        btnLogin.setBackground(new Color(2, 192, 154)); // green color
        btnLogin.setForeground(Color.WHITE);
        btnLogin.addActionListener(this);              // listen for clicks
        add(btnLogin);

        // --- Clear Button ---
        btnClear = new JButton("Clear");
        btnClear.setBounds(210, 190, 90, 30);
        btnClear.setBackground(new Color(200, 80, 80)); // red color
        btnClear.setForeground(Color.WHITE);
        btnClear.addActionListener(this);
        add(btnClear);

        setVisible(true); // show the window
    }

    // --- This method runs when any button is clicked ---
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnLogin) {
            checkLogin();         // call the login checking method
        }

        if (e.getSource() == btnClear) {
            txtUsername.setText("");        // clear username field
            txtPassword.setText("");        // clear password field
        }
    }

    // --- This method checks username/password against the database ---
    private void checkLogin() {

        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim(); // get password as String

        // Basic validation — fields must not be empty
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Step 1: Get connection to database
            Connection con = DBConnection.getConnection();

            // Step 2: Write SQL query with ? placeholders (safe — prevents SQL injection)
            String sql = "SELECT * FROM login WHERE username = ? AND password = ?";

            // Step 3: Prepare the query
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user); // replace first  ? with username
            ps.setString(2, pass); // replace second ? with password

            // Step 4: Run the query and get results
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Login successful — a matching row was found
                JOptionPane.showMessageDialog(this, "Login Successful! Welcome " + user, "Success", JOptionPane.INFORMATION_MESSAGE);
                new DashboardFrame();  // open the main dashboard
                dispose();             // close this login window

            } else {
                // No matching row found — wrong credentials
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            con.close(); // always close the connection when done

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

}