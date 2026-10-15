

import java.sql.SQLException;

public class TestUserDAO {

    public static void main(String[] args) {

        UserDAO userDAO = new UserDAO();

        try {
            // Add sample users to the database
            userDAO.addUser("Nitin", "Admin");
            userDAO.addUser("Rahul", "Driver");
            userDAO.addUser("Aman", "Maintenance Technician");

            System.out.println("\nUsers stored in database:");
            userDAO.displayUsers();

        } catch (SQLException e) {
            System.out.println("Database operation failed: "
                    + e.getMessage());
        }
    }
}