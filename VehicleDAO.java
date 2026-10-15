

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VehicleDAO {

    // Save the current vehicle status
    public void saveStatus(int speed, String direction,
                           String status) throws SQLException {

        String sql = "INSERT INTO vehicle_status "
                   + "(speed, direction, status) VALUES (?, ?, ?)";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, speed);
            statement.setString(2, direction);
            statement.setString(3, status);

            statement.executeUpdate();
            System.out.println("Vehicle status saved!");
        }
    }

    // Display saved vehicle status records
    public void displayStatus() throws SQLException {

        String sql = "SELECT * FROM vehicle_status ORDER BY id DESC";

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                System.out.println(
                    "Speed: " + result.getInt("speed")
                    + " km/h, Direction: "
                    + result.getString("direction")
                    + ", Status: "
                    + result.getString("status"));
            }
        }
    }
}