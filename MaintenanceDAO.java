
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MaintenanceDAO {

    // Save a new maintenance issue
    public void addIssue(String description) throws SQLException {

        String sql =
                "INSERT INTO maintenance_issues (description) VALUES (?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, description);
            statement.executeUpdate();

            System.out.println("Maintenance issue saved!");
        }
    }

    // Display all maintenance issues
    public void displayIssues() throws SQLException {

        String sql =
                "SELECT * FROM maintenance_issues ORDER BY id DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                System.out.println(
                        "Issue ID: " + result.getInt("id")
                        + ", Description: "
                        + result.getString("description")
                        + ", Status: "
                        + result.getString("status"));
            }
        }
    }

    // Mark an issue as Fixed using its database ID
    public void fixIssue(int issueId) throws SQLException {

        String sql =
                "UPDATE maintenance_issues SET status = 'Resolved' WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, issueId);
            int updated = statement.executeUpdate();

            if (updated > 0) {
                System.out.println("Issue marked as Resolved.");
            } else {
                System.out.println("Issue ID not found.");
            }
        }
    }

    // Resolve the latest open issue matching its description
    public boolean fixIssueByDescription(String description)
            throws SQLException {

        String sql =
                "UPDATE maintenance_issues SET status = 'Resolved' " +
                "WHERE id = (" +
                "SELECT id FROM (" +
                "SELECT id FROM maintenance_issues " +
                "WHERE description = ? AND status = 'Open' " +
                "ORDER BY id DESC LIMIT 1" +
                ") AS matching_issue)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, description);

            int updated = statement.executeUpdate();

            return updated > 0;
        }
    }
}
