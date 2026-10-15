
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;

public class Main extends JFrame {

    private static final String DATA_FILE = "self_driving_car_data.dat";
    private static final String LOG_FILE = "activity_log.txt";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final JTextArea activityLog = new JTextArea();

    private int speed = 0;
    private int maxSpeed = 120;
    private boolean safeMode = true;
    private String direction = "STRAIGHT";
    private String carStatus = "READY";

    private JTextField destinationField, distanceField;
    private JTextField routeAField, routeBField;
    private JLabel navResultLabel;
    private JLabel statusLabel, speedLabel, directionLabel;

    private final ArrayList<SystemUser> users = new ArrayList<>();
    private final ArrayList<MaintenanceIssue> issues = new ArrayList<>();

    private final CarDatabaseService databaseService = new CarDatabaseService();

    private JTextArea adminReport, maintenanceReport;
    private JLabel settingsLabel, maintenanceStatus;

    private String softwareVersion = "1.0";
    private boolean updateAvailable = true;

    static class SystemUser implements Serializable {
        private static final long serialVersionUID = 1L;

        String name;
        String role;
        boolean active = true;

        SystemUser(String name, String role) {
            this.name = name;
            this.role = role;
        }

        public String toString() {
            return name + " | " + role + " | "
                    + (active ? "Active" : "Inactive");
        }
    }

    static class MaintenanceIssue implements Serializable {
        private static final long serialVersionUID = 1L;

        int id;
        String component;
        String description;
        String severity;
        String status;

        MaintenanceIssue(int id, String component,
                         String description, String severity) {
            this.id = id;
            this.component = component;
            this.description = description;
            this.severity = severity;
            this.status = "Open";
        }

        public String toString() {
            return "Issue #" + id
                    + " | Component: " + component
                    + " | Severity: " + severity
                    + " | Status: " + status
                    + "\nDescription: " + description + "\n";
        }
    }

    static class SystemData implements Serializable {
        private static final long serialVersionUID = 1L;

        ArrayList<SystemUser> users;
        ArrayList<MaintenanceIssue> issues;
        int maxSpeed;
        boolean safeMode;
        String softwareVersion;
        boolean updateAvailable;
        int speed;
        String direction;
        String carStatus;
    }

    public Main() {
        setTitle("Self-Driving Car Control System");
        setSize(1100, 720);
        setMinimumSize(new Dimension(850, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        activityLog.setEditable(false);
        activityLog.setFont(new Font("Monospaced", Font.PLAIN, 12));

        loadData();
        loadActivityLog();

        if (users.isEmpty()) {
            users.add(new SystemUser("Admin", "Admin"));
            users.add(new SystemUser("Driver One", "Driver"));
            users.add(new SystemUser("Technician One",
                    "Maintenance Technician"));
            saveData();
        }

        createSidebar();
        createPages();

        log("System started.");
        refreshDashboard();
        refreshAdminReport();
        refreshMaintenanceReport();

        setVisible(true);
    }

    // ---------------- FILE STORAGE ----------------

    private void saveData() {
        SystemData data = new SystemData();
        data.users = new ArrayList<>(users);
        data.issues = new ArrayList<>(issues);
        data.maxSpeed = maxSpeed;
        data.safeMode = safeMode;
        data.softwareVersion = softwareVersion;
        data.updateAvailable = updateAvailable;
        data.speed = speed;
        data.direction = direction;
        data.carStatus = carStatus;

        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(DATA_FILE))) {
            out.writeObject(data);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save data: " + ex.getMessage(),
                    "Storage Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream in =
                     new ObjectInputStream(new FileInputStream(file))) {

            SystemData data = (SystemData) in.readObject();

            if (data.users != null) {
                users.addAll(data.users);
            }

            if (data.issues != null) {
                issues.addAll(data.issues);
            }

            maxSpeed = data.maxSpeed;
            safeMode = data.safeMode;
            softwareVersion = data.softwareVersion;
            updateAvailable = data.updateAvailable;
            speed = data.speed;
            direction = data.direction;
            carStatus = data.carStatus;

        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    "Saved data could not be loaded.\n"
                            + ex.getMessage(),
                    "Storage Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ---------------- ACTIVITY LOG ----------------

    private void loadActivityLog() {
        File file = new File(LOG_FILE);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                activityLog.append(line + "\n");
            }

            activityLog.setCaretPosition(
                    activityLog.getDocument().getLength());

        } catch (IOException ex) {
            System.out.println("Could not load activity log: "
                    + ex.getMessage());
        }
    }

    private void log(String message) {
        String time = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String entry = "[" + time + "] " + message;

        activityLog.append(entry + "\n");
        activityLog.setCaretPosition(
                activityLog.getDocument().getLength());

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(LOG_FILE, true))) {
            writer.write(entry);
            writer.newLine();
        } catch (IOException ex) {
            System.out.println("Could not save activity log: "
                    + ex.getMessage());
        }
    }

    // ---------------- SIDEBAR AND PAGES ----------------

    private void createSidebar() {
        JPanel sidebar = new JPanel(new GridLayout(0, 1, 5, 8));
        sidebar.setPreferredSize(new Dimension(205, 0));
        sidebar.setBackground(new Color(28, 39, 56));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));

        JLabel heading = new JLabel(
                "<html><center>SELF-DRIVING<br>CAR SYSTEM</center></html>",
                SwingConstants.CENTER);
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("Arial", Font.BOLD, 18));
        sidebar.add(heading);

        addNavButton(sidebar, "Dashboard", "Dashboard");
        addNavButton(sidebar, "Driver Controls", "Driver");
        addNavButton(sidebar, "Navigation", "Navigation");
        addNavButton(sidebar, "Admin Dashboard", "Admin");
        addNavButton(sidebar, "Maintenance", "Maintenance");
        addNavButton(sidebar, "Activity History", "Activity");

        add(sidebar, BorderLayout.WEST);
    }

    private void addNavButton(JPanel panel, String title, String page) {
        JButton button = new JButton(title);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(48, 63, 85));
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            if ("Activity".equals(page)) {
                refreshActivityPage();
            }
            cardLayout.show(mainPanel, page);
        });
        panel.add(button);
    }

    private void createPages() {
        mainPanel.add(createDashboard(), "Dashboard");
        mainPanel.add(createDriverPage(), "Driver");
        mainPanel.add(createNavigationPage(), "Navigation");
        mainPanel.add(createAdminPage(), "Admin");
        mainPanel.add(createMaintenancePage(), "Maintenance");
        mainPanel.add(createActivityPage(), "Activity");

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createPage(String title) {
        JPanel page = new JPanel(new BorderLayout(12, 12));
        page.setBackground(new Color(242, 245, 249));
        page.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("Arial", Font.BOLD, 25));
        page.add(heading, BorderLayout.NORTH);
        return page;
    }

    private void addActionButton(
            JPanel panel, String title, Runnable action) {
        JButton button = new JButton(title);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());
        panel.add(button);
    }

    // ---------------- DASHBOARD ----------------

    private JPanel createDashboard() {
        JPanel page = createPage("System Dashboard");
        JPanel content = new JPanel(new BorderLayout(10, 15));
        content.setOpaque(false);

        JPanel cards = new JPanel(new GridLayout(1, 3, 12, 12));
        cards.setOpaque(false);

        statusLabel = new JLabel();
        speedLabel = new JLabel();
        directionLabel = new JLabel();

        cards.add(createInfoCard("Car Status", statusLabel));
        cards.add(createInfoCard("Current Speed", speedLabel));
        cards.add(createInfoCard("Direction", directionLabel));

        JPanel logPanel = new JPanel(new BorderLayout(5, 5));
        logPanel.setOpaque(false);
        logPanel.add(new JLabel("Recent Activity"), BorderLayout.NORTH);
        logPanel.add(new JScrollPane(activityLog), BorderLayout.CENTER);

        content.add(cards, BorderLayout.NORTH);
        content.add(logPanel, BorderLayout.CENTER);
        page.add(content, BorderLayout.CENTER);
        return page;
    }

    private JPanel createInfoCard(String title, JLabel value) {
        JPanel card = new JPanel(new GridLayout(2, 1, 5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));

        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        value.setFont(new Font("Arial", Font.BOLD, 22));

        card.add(label);
        card.add(value);
        return card;
    }

    // ---------------- DRIVER ----------------

    private JPanel createDriverPage() {
        JPanel page = createPage("Driver Controls");
        JPanel controls = new JPanel(new GridLayout(0, 2, 12, 12));
        controls.setOpaque(false);

        addActionButton(controls, "Accelerate", () -> {
            int increase = safeMode ? 5 : 10;
            speed = Math.min(maxSpeed, speed + increase);
            carStatus = speed == 0 ? "READY" : "MOVING";
            log("Acceleration requested. Speed: " + speed + " km/h.");
            refreshDashboard();
            saveData();
        });

        addActionButton(controls, "Brake", () -> {
            speed = Math.max(0, speed - 10);
            carStatus = speed == 0 ? "READY" : "MOVING";
            log("Brakes applied. Speed: " + speed + " km/h.");
            refreshDashboard();
            saveData();
        });

        addActionButton(controls, "Turn Left", () -> turn("LEFT"));
        addActionButton(controls, "Turn Right", () -> turn("RIGHT"));

        addActionButton(controls, "Stop Car", () -> {
            speed = 0;
            carStatus = "STOPPED";
            log("Car stopped.");
            refreshDashboard();
            saveData();
        });

        addActionButton(controls, "Override Control", () -> {
            speed = 0;
            carStatus = "MANUAL OVERRIDE";
            log("Manual override activated.");
            refreshDashboard();
            saveData();
            JOptionPane.showMessageDialog(this,
                    "Manual override activated.\nSpeed set to zero.");
        });

        addActionButton(controls, "Check Car Status", () ->
                JOptionPane.showMessageDialog(this,
                        "Status: " + carStatus
                                + "\nSpeed: " + speed + " km/h"
                                + "\nDirection: " + direction
                                + "\nMaximum speed: " + maxSpeed + " km/h"));

        addActionButton(controls, "Simulate Obstacle", this::detectObstacle);

        page.add(controls, BorderLayout.CENTER);
        return page;
    }

    private void turn(String newDirection) {
        direction = newDirection;
        log("Car turned " + newDirection + ".");
        refreshDashboard();
        saveData();
    }

    private void detectObstacle() {
        speed = 0;
        carStatus = "OBSTACLE DETECTED";
        log("Simulated obstacle detected. Car stopped.");
        refreshDashboard();
        saveData();
        JOptionPane.showMessageDialog(this,
                "Obstacle detected!\nThe simulated car has stopped.");
    }

    // ---------------- NAVIGATION ----------------

    private JPanel createNavigationPage() {
        JPanel page = createPage("Navigation and Route Management");
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 12));
        form.setOpaque(false);

        destinationField = new JTextField();
        distanceField = new JTextField();
        routeAField = new JTextField();
        routeBField = new JTextField();

        form.add(new JLabel("Destination:"));
        form.add(destinationField);
        form.add(new JLabel("Distance to destination (km):"));
        form.add(distanceField);

        addActionButton(form, "Calculate ETA", this::calculateETA);
        form.add(new JLabel("Estimated speed: 40 km/h"));

        form.add(new JLabel("Route A distance (km):"));
        form.add(routeAField);
        form.add(new JLabel("Route B distance (km):"));
        form.add(routeBField);

        addActionButton(form, "Compare Routes", this::compareRoutes);
        addActionButton(form, "Simulate Obstacle", this::detectObstacle);

        navResultLabel = new JLabel("Enter a destination or compare routes.");

        JPanel content = new JPanel(new BorderLayout(10, 15));
        content.setOpaque(false);
        content.add(form, BorderLayout.NORTH);
        content.add(navResultLabel, BorderLayout.CENTER);

        page.add(content, BorderLayout.CENTER);
        return page;
    }

    private void calculateETA() {
        try {
            String destination = destinationField.getText().trim();
            double distance =
                    Double.parseDouble(distanceField.getText().trim());

            if (destination.isEmpty() || distance < 0) {
                showError("Enter a destination and non-negative distance.");
                return;
            }

            int minutes = (int) Math.round(distance / 40.0 * 60);
            navResultLabel.setText("<html>Destination: " + destination
                    + " | Distance: " + distance
                    + " km | Estimated time: " + minutes
                    + " minutes</html>");

            log("Destination set to " + destination + ".");
        } catch (NumberFormatException ex) {
            showError("Enter a valid distance.");
        }
    }

    private void compareRoutes() {
        try {
            double a = Double.parseDouble(routeAField.getText().trim());
            double b = Double.parseDouble(routeBField.getText().trim());

            if (a < 0 || b < 0) {
                showError("Route distances cannot be negative.");
                return;
            }

            String result;
            if (a < b) {
                result = "Route A is shorter by " + (b - a) + " km.";
            } else if (b < a) {
                result = "Route B is shorter by " + (a - b) + " km.";
            } else {
                result = "Both routes have the same distance.";
            }

            navResultLabel.setText(result);
            log("Route comparison: " + result);
        } catch (NumberFormatException ex) {
            showError("Enter valid distances for both routes.");
        }
    }

    // ---------------- ADMIN ----------------

    private JPanel createAdminPage() {
        JPanel page = createPage("Admin Dashboard");
        JPanel content = new JPanel(new BorderLayout(10, 12));
        content.setOpaque(false);

        JPanel buttons = new JPanel(new GridLayout(0, 2, 10, 10));
        buttons.setOpaque(false);

        addActionButton(buttons, "Add User", this::addUser);
        addActionButton(buttons, "View Users", this::viewUsers);
        addActionButton(buttons, "Update User", this::updateUser);
        addActionButton(buttons, "Delete User", this::deleteUser);
        addActionButton(buttons, "Change Maximum Speed", this::changeMaxSpeed);
        addActionButton(buttons, "Toggle Safe Mode", this::toggleSafeMode);
        addActionButton(buttons, "Check Software Updates", this::checkUpdates);
        addActionButton(buttons, "Install Software Update", this::installUpdate);

        adminReport = new JTextArea(9, 30);
        adminReport.setEditable(false);
        settingsLabel = new JLabel();

        JPanel reportPanel = new JPanel(new BorderLayout(5, 5));
        reportPanel.setOpaque(false);
        reportPanel.add(new JLabel("System Configuration and Users"),
                BorderLayout.NORTH);
        reportPanel.add(new JScrollPane(adminReport), BorderLayout.CENTER);

        content.add(buttons, BorderLayout.NORTH);
        content.add(reportPanel, BorderLayout.CENTER);
        content.add(settingsLabel, BorderLayout.SOUTH);

        page.add(content, BorderLayout.CENTER);
        return page;
    }

    private void addUser() {
        JTextField nameField = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{
                "Admin", "Driver", "Maintenance Technician"
        });

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("User name:"));
        form.add(nameField);
        form.add(new JLabel("Role:"));
        form.add(roleBox);

        int result = JOptionPane.showConfirmDialog(this, form,
                "Add User", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return;

        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("User name cannot be empty.");
            return;
        }

        users.add(new SystemUser(name, (String) roleBox.getSelectedItem()));
        log("Admin added user: " + name);
        refreshAdminReport();
        saveData();
    }

    private void viewUsers() {
        refreshAdminReport();
        JTextArea text = new JTextArea(adminReport.getText(), 15, 35);
        text.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(text),
                "Users and Settings", JOptionPane.INFORMATION_MESSAGE);
    }

    private SystemUser selectUser(String title) {
        if (users.isEmpty()) {
            showError("No users are registered.");
            return null;
        }

        return (SystemUser) JOptionPane.showInputDialog(
                this, title, "User Management",
                JOptionPane.PLAIN_MESSAGE, null,
                users.toArray(new SystemUser[0]), users.get(0));
    }

    private void updateUser() {
        SystemUser user = selectUser("Select a user to update");
        if (user == null) return;

        JTextField nameField = new JTextField(user.name);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{
                "Admin", "Driver", "Maintenance Technician"
        });
        roleBox.setSelectedItem(user.role);

        JComboBox<String> accessBox =
                new JComboBox<>(new String[]{"Active", "Inactive"});
        accessBox.setSelectedItem(user.active ? "Active" : "Inactive");

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Role:"));
        form.add(roleBox);
        form.add(new JLabel("Access:"));
        form.add(accessBox);

        int result = JOptionPane.showConfirmDialog(this, form,
                "Update User", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return;

        if (nameField.getText().trim().isEmpty()) {
            showError("Name cannot be empty.");
            return;
        }

        user.name = nameField.getText().trim();
        user.role = (String) roleBox.getSelectedItem();
        user.active = "Active".equals(accessBox.getSelectedItem());

        log("Admin updated user: " + user.name);
        refreshAdminReport();
        saveData();
    }

    private void deleteUser() {
        SystemUser user = selectUser("Select a user to delete");
        if (user == null) return;

        int answer = JOptionPane.showConfirmDialog(this,
                "Delete " + user.name + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            users.remove(user);
            log("Admin deleted user: " + user.name);
            refreshAdminReport();
            saveData();
        }
    }

    private void changeMaxSpeed() {
        String input = JOptionPane.showInputDialog(this,
                "Enter maximum speed (20-200 km/h):", maxSpeed);
        if (input == null) return;

        try {
            int value = Integer.parseInt(input.trim());
            if (value < 20 || value > 200) {
                showError("Speed must be between 20 and 200.");
                return;
            }

            maxSpeed = value;
            if (speed > maxSpeed) speed = maxSpeed;

            log("Maximum speed changed to " + maxSpeed + " km/h.");
            refreshDashboard();
            refreshAdminReport();
            saveData();
        } catch (NumberFormatException ex) {
            showError("Enter a valid whole number.");
        }
    }

    private void toggleSafeMode() {
        safeMode = !safeMode;
        log("Safe Mode set to " + (safeMode ? "ON" : "OFF") + ".");
        refreshAdminReport();
        saveData();

        JOptionPane.showMessageDialog(this,
                "Safe Mode: " + (safeMode ? "ON" : "OFF"));
    }

    private void checkUpdates() {
        String message = updateAvailable
                ? "Version 1.1 is available. Current version: " + softwareVersion
                : "Software is up to date. Current version: " + softwareVersion;

        JOptionPane.showMessageDialog(this, message);
        log("Software update check completed.");
        refreshAdminReport();
    }

    private void installUpdate() {
        if (!updateAvailable) {
            JOptionPane.showMessageDialog(this, "No updates available.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this,
                "Install simulated update 1.1?",
                "Software Update", JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            softwareVersion = "1.1";
            updateAvailable = false;
            log("Simulated update installed: version 1.1.");
            refreshAdminReport();
            saveData();
            JOptionPane.showMessageDialog(this, "Update installed.");
        }
    }

    private void refreshAdminReport() {
        if (adminReport == null || settingsLabel == null) return;

        StringBuilder text = new StringBuilder();
        text.append("SYSTEM SETTINGS\n");
        text.append("Maximum speed: ").append(maxSpeed).append(" km/h\n");
        text.append("Safe Mode: ").append(safeMode ? "ON" : "OFF").append("\n");
        text.append("Software version: ").append(softwareVersion).append("\n");
        text.append("Update available: ").append(updateAvailable ? "Yes" : "No");
        text.append("\n\nREGISTERED USERS\n");

        for (SystemUser user : users) {
            text.append(user).append("\n");
        }

        adminReport.setText(text.toString());
        settingsLabel.setText("Safe Mode: " + (safeMode ? "ON" : "OFF")
                + " | Maximum speed: " + maxSpeed
                + " km/h | Version: " + softwareVersion);
    }

    // ---------------- MAINTENANCE ----------------

    private JPanel createMaintenancePage() {
        JPanel page = createPage("Maintenance Dashboard");
        JPanel content = new JPanel(new BorderLayout(10, 12));
        content.setOpaque(false);

        JPanel buttons = new JPanel(new GridLayout(0, 2, 10, 10));
        buttons.setOpaque(false);

        addActionButton(buttons, "Report a Fault", this::reportFault);
        addActionButton(buttons, "Run Diagnostics", this::runDiagnostics);
        addActionButton(buttons, "Resolve an Issue", this::resolveIssue);
        addActionButton(buttons, "View Maintenance History",
                this::viewMaintenanceHistory);
        addActionButton(buttons, "Run System Tests", this::runSystemTests);

        maintenanceStatus = new JLabel("Maintenance system ready.");
        maintenanceReport = new JTextArea(15, 40);
        maintenanceReport.setEditable(false);
        maintenanceReport.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JPanel reportPanel = new JPanel(new BorderLayout(5, 5));
        reportPanel.setOpaque(false);
        reportPanel.add(new JLabel("Maintenance Issues and History"),
                BorderLayout.NORTH);
        reportPanel.add(new JScrollPane(maintenanceReport),
                BorderLayout.CENTER);

        content.add(buttons, BorderLayout.NORTH);
        content.add(reportPanel, BorderLayout.CENTER);
        content.add(maintenanceStatus, BorderLayout.SOUTH);

        page.add(content, BorderLayout.CENTER);
        return page;
    }

    
private void reportFault() {
    JTextField componentField = new JTextField();

    JComboBox<String> severityBox = new JComboBox<>(
            new String[]{"Low", "Medium", "High", "Critical"});

    JTextArea descriptionField = new JTextArea(4, 25);
    descriptionField.setLineWrap(true);
    descriptionField.setWrapStyleWord(true);

    JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
    form.add(new JLabel("Component:"));
    form.add(componentField);
    form.add(new JLabel("Severity:"));
    form.add(severityBox);
    form.add(new JLabel("Describe the fault:"));
    form.add(new JScrollPane(descriptionField));

    int result = JOptionPane.showConfirmDialog(
            this, form, "Report a Fault",
            JOptionPane.OK_CANCEL_OPTION);

    if (result != JOptionPane.OK_OPTION) {
        return;
    }

    String component = componentField.getText().trim();
    String description = descriptionField.getText().trim();
    String severity = (String) severityBox.getSelectedItem();

    if (component.isEmpty() || description.isEmpty()) {
        showError("Enter both the component and fault description.");
        return;
    }

    int id = 1;

    for (MaintenanceIssue issue : issues) {
        id = Math.max(id, issue.id + 1);
    }

    MaintenanceIssue newIssue = new MaintenanceIssue(
            id, component, description, severity);

    // Save the issue in the existing local storage.
    issues.add(newIssue);
    saveData();

    // Try to save the same issue in MySQL.
    boolean databaseSaved = false;

    try {
        databaseService.reportIssue(
                component + " [" + severity + "]: " + description);

        databaseSaved = true;

    } catch (java.sql.SQLException ex) {
        System.err.println(
                "MySQL maintenance save failed: " + ex.getMessage());
    }

    log("Fault reported: Issue #" + id + " (" + component + ").");

    if (databaseSaved) {
        maintenanceStatus.setText(
                "Issue #" + id + " saved locally and to MySQL.");
    } else {
        maintenanceStatus.setText(
                "Issue #" + id + " saved locally only.");
    }

    refreshMaintenanceReport();

    if (databaseSaved) {
        JOptionPane.showMessageDialog(this,
                "Fault reported successfully!\n"
                + "Issue ID: " + id
                + "\nSaved to local storage and MySQL.");
    } else {
        JOptionPane.showMessageDialog(this,
                "Fault saved locally, but MySQL saving failed.\n"
                + "Check your database connection and terminal.",
                "Database Warning",
                JOptionPane.WARNING_MESSAGE);
    }
}


    private void runDiagnostics() {
        String[] components = {
                "Speed Controller", "Brake System",
                "Obstacle Sensors", "Navigation System"
        };

        StringBuilder result = new StringBuilder("SIMULATED DIAGNOSTICS\n\n");

        for (String component : components) {
            boolean found = false;

            for (MaintenanceIssue issue : issues) {
                if (issue.status.equals("Open")
                        && issue.component.equalsIgnoreCase(component)) {
                    found = true;
                    break;
                }
            }

            result.append(component).append(": ")
                    .append(found ? "WARNING - Open fault reported"
                                  : "No reported faults")
                    .append("\n");
        }

        result.append("\nChecks are based on reported issues only.");
        maintenanceStatus.setText("Diagnostics completed.");
        log("Simulated diagnostics completed.");

        JOptionPane.showMessageDialog(this, result.toString(),
                "Diagnostic Results", JOptionPane.INFORMATION_MESSAGE);
    }

    
private void resolveIssue() {

    if (issues.isEmpty()) {
        JOptionPane.showMessageDialog(
                this,
                "No maintenance issues available."
        );
        return;
    }

    MaintenanceIssue selected = (MaintenanceIssue)
            JOptionPane.showInputDialog(
                    this,
                    "Select the issue to resolve:",
                    "Resolve Maintenance Issue",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    issues.toArray(),
                    issues.get(0)
            );

    if (selected == null) {
        return;
    }

    if ("Resolved".equalsIgnoreCase(selected.status)) {
        JOptionPane.showMessageDialog(
                this,
                "This issue is already resolved."
        );
        return;
    }

    int answer = JOptionPane.showConfirmDialog(
            this,
            "Resolve issue #" + selected.id + "?\n"
                    + selected.component + ": "
                    + selected.description,
            "Confirm Resolution",
            JOptionPane.YES_NO_OPTION
    );

    if (answer != JOptionPane.YES_OPTION) {
        return;
    }

    String databaseDescription =
            selected.component + " [" + selected.severity
                    + "]: " + selected.description;

    try {
        boolean databaseUpdated =
                databaseService.resolveIssueByDescription(
                        databaseDescription
                );

        selected.status = "Resolved";
        saveData();

        log("Maintenance issue #" + selected.id + " resolved.");

        if (databaseUpdated) {
            maintenanceStatus.setText(
                    "Issue #" + selected.id
                            + " resolved locally and in MySQL."
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Issue resolved successfully in local storage and MySQL."
            );
        } else {
            maintenanceStatus.setText(
                    "Issue resolved locally; no matching open MySQL issue found."
            );

            JOptionPane.showMessageDialog(
                    this,
                    "The local issue was resolved, but no matching open "
                            + "database issue was found.",
                    "Database Warning",
                    JOptionPane.WARNING_MESSAGE
            );
        }

    } catch (java.sql.SQLException ex) {
        JOptionPane.showMessageDialog(
                this,
                "Database error: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    refreshMaintenanceReport();
}


    private void viewMaintenanceHistory() {
        refreshMaintenanceReport();

        JTextArea history = new JTextArea(maintenanceReport.getText(), 18, 45);
        history.setEditable(false);
        history.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JOptionPane.showMessageDialog(this, new JScrollPane(history),
                "Maintenance History", JOptionPane.INFORMATION_MESSAGE);
    }

    private void runSystemTests() {
        long openCount = issues.stream()
                .filter(issue -> issue.status.equals("Open"))
                .count();

        String result = "SIMULATED SYSTEM TESTS\n\n"
                + "Car state: PASS\n"
                + "Speed limit setting: "
                + (maxSpeed >= 20 && maxSpeed <= 200 ? "PASS" : "FAIL") + "\n"
                + "Navigation interface: PASS\n"
                + "Maintenance tracking: PASS\n"
                + "Open issues: " + openCount + "\n\n"
                + "These are software demonstration tests, "
                + "not real vehicle safety tests.";

        maintenanceStatus.setText("System tests completed.");
        log("Simulated system tests completed.");

        JOptionPane.showMessageDialog(this, result,
                "System Test Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshMaintenanceReport() {
        if (maintenanceReport == null) return;

        long open = issues.stream()
                .filter(issue -> issue.status.equals("Open"))
                .count();

        StringBuilder text = new StringBuilder();
        text.append("MAINTENANCE SUMMARY\n");
        text.append("==============================\n");
        text.append("Total issues: ").append(issues.size()).append("\n");
        text.append("Open issues: ").append(open).append("\n");
        text.append("Resolved issues: ").append(issues.size() - open).append("\n\n");
        text.append("ISSUE DETAILS\n");
        text.append("==============================\n");

        if (issues.isEmpty()) {
            text.append("No maintenance issues reported yet.\n");
        } else {
            for (MaintenanceIssue issue : issues) {
                text.append(issue).append("\n");
            }
        }

        maintenanceReport.setText(text.toString());
    }

    // ---------------- ACTIVITY HISTORY PAGE ----------------

    private JPanel createActivityPage() {
        JPanel page = createPage("Activity History");

        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JButton refreshButton = new JButton("Refresh History");
        refreshButton.addActionListener(e -> {
            refreshActivityPage(historyArea);
        });

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.add(refreshButton, BorderLayout.NORTH);
        content.add(new JScrollPane(historyArea), BorderLayout.CENTER);

        page.add(content, BorderLayout.CENTER);
        refreshActivityPage(historyArea);
        return page;
    }

    private void refreshActivityPage() {
        // The page refreshes its history when it is opened.
        cardLayout.show(mainPanel, "Activity");
    }

    private void refreshActivityPage(JTextArea historyArea) {
        historyArea.setText("");

        File file = new File(LOG_FILE);
        if (!file.exists()) {
            historyArea.setText("No activity recorded yet.");
            return;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                historyArea.append(line + "\n");
            }
            historyArea.setCaretPosition(0);
        } catch (IOException ex) {
            historyArea.setText("Could not read activity history.");
        }
    }

    // ---------------- COMMON METHODS ----------------

    private void refreshDashboard() {
        if (statusLabel != null) {
            statusLabel.setText(carStatus);
            speedLabel.setText(speed + " km/h");
            directionLabel.setText(direction);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}