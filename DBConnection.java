import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {


    private static final String URL ="jdbc:mysql://localhost:3306/student_management";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123abc"; // <-- change this to your MySQL password


    public static Connection getConnection() {
        Connection con = null;
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            // Actually connect to the database using URL, username, password
            con = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        } catch (ClassNotFoundException e) {

            System.out.println("MySQL Driver not found! Add mysql-connector-java.jar to your project.");
            e.printStackTrace();

        } catch (SQLException e) {

            System.out.println("Connection failed! Check username/password and make sure MySQL is running.");
            e.printStackTrace();
        }
        return con;
    }
}
