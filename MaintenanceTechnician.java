
public class MaintenanceTechnician extends User
        implements DiagnosticService {

    public MaintenanceTechnician(int id, String name) {
        super(id, name, "Maintenance Technician");
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Maintenance Technician ===");
        super.displayInfo();
        System.out.println("Access: Diagnostics and Repairs");
    }

    @Override
    public void diagnoseIssue() {
        System.out.println("Vehicle diagnostics completed.");
    }

    @Override
    public void runSystemTest() {
        System.out.println("System test completed successfully.");
    }
}