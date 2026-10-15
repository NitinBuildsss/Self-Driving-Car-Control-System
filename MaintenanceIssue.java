
public class MaintenanceIssue {
    private int issueId;
    private String description;
    private String status;

    public MaintenanceIssue(int issueId, String description) {
        this.issueId = issueId;
        this.description = description;
        this.status = "Open";
    }

    public int getIssueId() {
        return issueId;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void fixIssue() {
        status = "Fixed";
    }

    public void displayIssue() {
        System.out.println("Issue ID: " + issueId);
        System.out.println("Description: " + description);
        System.out.println("Status: " + status);
        System.out.println("----------------------");
    }
}