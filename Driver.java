
public class Driver extends User {

    public Driver(int id, String name) {
        super(id, name, "Driver");
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Driver Details ===");
        super.displayInfo();
        System.out.println("Access: Vehicle Controls and Navigation");
    }
}