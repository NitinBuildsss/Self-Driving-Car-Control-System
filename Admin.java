
public class Admin extends User {

    public Admin(int id, String name) {
        super(id, name, "Admin");
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Admin Details ===");
        super.displayInfo();
        System.out.println("Access: User Management and System Settings");
    }

    public void manageUsers() {
        System.out.println("Admin is managing users.");
    }
}