
import java.sql.SQLException;

public class TestVehicleDAO {

    public static void main(String[] args) {

        VehicleDAO vehicleDAO = new VehicleDAO();

        try {
            vehicleDAO.saveStatus(0, "STRAIGHT", "Stopped");
            vehicleDAO.saveStatus(60, "LEFT", "Moving");

            System.out.println("\nSaved vehicle records:");
            vehicleDAO.displayStatus();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}