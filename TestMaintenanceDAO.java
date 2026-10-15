
import java.sql.SQLException;

public class TestMaintenanceDAO {

    public static void main(String[] args) {

        MaintenanceDAO dao = new MaintenanceDAO();

        try {
            dao.addIssue("Obstacle sensor needs inspection");
            dao.addIssue("Navigation system requires testing");

            System.out.println("\nMaintenance records:");
            dao.displayIssues();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}