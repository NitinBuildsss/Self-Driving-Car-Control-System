# Self-Driving Car Control System

## Project Description
The Self-Driving Car Control System is a Java-based desktop application that simulates vehicle control and monitoring. It provides a graphical interface for vehicle operations, navigation, user management, and maintenance issue tracking. The project uses Java Swing for the GUI and JDBC with MySQL for database operations.

**Note:** This is a simulation project. It does not control a real vehicle.

## Technologies Used
- Java
- Java Swing (Graphical User Interface)
- JDBC (Java Database Connectivity)
- MySQL
- MySQL Connector/J

## Main Features
- **Driver Dashboard:** Monitor vehicle status and operate simulated driving controls.
- **Navigation:** Manage destinations and compare route options.
- **Admin Dashboard:** Manage users and system settings.
- **Maintenance Dashboard:** Report, view, and resolve maintenance issues.
- **Database Integration:** Store and retrieve maintenance and other supported records using MySQL.
- **Background Monitoring:** Demonstrate multithreading for vehicle monitoring.

## User Roles
1. **Admin:** Manages users and system settings.
2. **Driver:** Monitors vehicle status and uses driving and navigation controls.
3. **Maintenance Technician:** Diagnoses issues and performs maintenance-related operations.

## Requirements
- Java Development Kit (JDK)
- MySQL Server
- MySQL Connector/J
- Visual Studio Code or another Java IDE

## Database Setup
1. Start MySQL Server.
2. Create a database named `self_driving_car`.
3. Create the required tables: `users`, `vehicle_status`, and `maintenance_issues`.
4. Configure your own MySQL username and password.
5. Set the `DB_PASSWORD` environment variable before running the application.

The database tables must use the column names expected by the DAO classes in this project.

## How to Run

Open the project folder in VS Code and open its terminal.

Compile the Java files:

```powershell
javac -cp ".;lib\mysql-connector-j-26.7.0.jar" *.java
```

Run the database connection test:

```powershell
java -cp ".;lib\mysql-connector-j-26.7.0.jar" DatabaseConnection
```

Launch the application:

```powershell
java -cp ".;lib\mysql-connector-j-26.7.0.jar" Main
```

Make sure MySQL is running, the database and required tables exist, and `DB_PASSWORD` is set in the same terminal before running the commands.

## Project Structure
- `Main.java` — Main graphical application.
- `DatabaseConnection.java` — Establishes the MySQL connection.
- `UserDAO.java` — Database operations for users.
- `VehicleDAO.java` — Database operations for vehicle status.
- `MaintenanceDAO.java` — Database operations for maintenance issues.
- `CarDatabaseService.java` — Coordinates database-related services.
- `CarMonitor.java` — Background vehicle monitoring.
- `Repository.java` — Generic repository implementation.
- `lib/` — MySQL Connector/J library.

## Testing
The application can be tested by launching the GUI, trying the available role dashboards and vehicle controls, and reporting or resolving a maintenance issue. Database features require a correctly configured MySQL instance.

## Future Improvements
- Integration with real maps and navigation services.
- Sensor-based obstacle detection.
- Improved authentication and access control.
- Automated testing and reporting.

## Author
Nitin Kumar

## GitHub Repository
https://github.com/NitinBuildsss/Self-Driving-Car-Control-System
