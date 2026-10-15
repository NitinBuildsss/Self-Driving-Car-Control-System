
import java.sql.SQLException;

public class CarDatabaseService {

    private final UserDAO userDAO = new UserDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final MaintenanceDAO maintenanceDAO = new MaintenanceDAO();

    // Register a user
    public void registerUser(String name, String role)
            throws SQLException {
        userDAO.addUser(name, role);
    }

    // Save vehicle status
    public void saveVehicleStatus(
            int speed, String direction, String status)
            throws SQLException {
        vehicleDAO.saveStatus(speed, direction, status);
    }

    // Report a maintenance issue
    public void reportIssue(String description)
            throws SQLException {
        maintenanceDAO.addIssue(description);
    }

    // Resolve issue using database ID
    public void resolveIssue(int issueId)
            throws SQLException {
        maintenanceDAO.fixIssue(issueId);
    }

    // Resolve issue using its description
    public boolean resolveIssueByDescription(String description)
            throws SQLException {
        return maintenanceDAO.fixIssueByDescription(description);
    }

    // Display all users
    public void displayAllUsers() throws SQLException {
        userDAO.displayUsers();
    }

    // Display vehicle history
    public void displayVehicleHistory() throws SQLException {
        vehicleDAO.displayStatus();
    }

    // Display maintenance issues
    public void displayMaintenanceIssues() throws SQLException {
        maintenanceDAO.displayIssues();
    }
}
