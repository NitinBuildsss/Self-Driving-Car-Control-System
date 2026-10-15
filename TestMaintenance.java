
import java.util.ArrayList;

public class TestMaintenance {
    public static void main(String[] args) {

        ArrayList<MaintenanceIssue> issues = new ArrayList<>();

        issues.add(new MaintenanceIssue(1, "Obstacle sensor error"));
        issues.add(new MaintenanceIssue(2, "Navigation system error"));
        issues.add(new MaintenanceIssue(3, "Battery warning"));

        System.out.println("All Maintenance Issues");
        System.out.println("======================");

        for (MaintenanceIssue issue : issues) {
            issue.displayIssue();
        }

        issues.get(0).fixIssue();

        System.out.println("After fixing the first issue:");
        issues.get(0).displayIssue();
    }
}