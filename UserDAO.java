
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Save a user into the MySQL database
    public void addUser(String name, String role)
            throws SQLException {

        String sql =
                "INSERT INTO users (name, role) VALUES (?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, role);

            statement.executeUpdate();
            System.out.println("User saved successfully!");
        }
    }

    // Display all users stored in the database
    public void displayUsers() throws SQLException {

        String sql = "SELECT * FROM users";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                System.out.println(
                        "ID: " + result.getInt("id")
                        + ", Name: " + result.getString("name")
                        + ", Role: " + result.getString("role"));
            }
        }
    }
}