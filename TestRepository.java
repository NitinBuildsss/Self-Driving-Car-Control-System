
public class TestRepository {

    public static void main(String[] args) {

        Repository<MaintenanceIssue> issues =
                new Repository<>();

        issues.add(new MaintenanceIssue(
                1, "Obstacle sensor error"));

        issues.add(new MaintenanceIssue(
                2, "Navigation system error"));

        System.out.println(
                "Total issues: " + issues.count());

        for (MaintenanceIssue issue : issues.getAll()) {
            issue.displayIssue();
        }
    }
}