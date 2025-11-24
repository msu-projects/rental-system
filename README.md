# Sakila Film Rental Management System

A professional Java Swing GUI application for managing film rentals, built with the MySQL Sakila sample database.

## Features

### Core Functionality
- **Film Browser** - Browse, search, and filter films with a sortable table view
- **Film Management** - Create and update films using an intuitive form interface
- **Dashboard** - Real-time statistics with interactive charts and visualizations
- **Database Views & Stored Procedures** - Optimized database operations

### Advanced Features
- **JDBC Connections** - Standard java.sql for database connectivity (no external dependencies!)
- **Auto-Refresh** - Automatic data refresh at configurable intervals
- **CSV Export** - Export film data to CSV format
- **Dark/Light Theme** - Toggle between themes for user preference
- **Color-Coded Status** - Visual indicators for film availability
- **Input Validation** - Client-side validation with helpful error messages
- **Error Handling** - Comprehensive error handling and logging

## Architecture

The application follows the **MVC (Model-View-Controller)** pattern:

```
src/
├── com/sakila/
│   ├── model/              # Data models and DAOs
│   │   ├── Film.java
│   │   ├── Category.java
│   │   ├── Language.java
│   │   ├── DashboardStats.java
│   │   ├── FilmDAO.java
│   │   ├── CategoryDAO.java
│   │   ├── LanguageDAO.java
│   │   └── DashboardDAO.java
│   ├── view/               # GUI components
│   │   ├── MainWindow.java
│   │   ├── DashboardPanel.java
│   │   ├── FilmBrowserPanel.java
│   │   ├── FilmManagementPanel.java
│   │   ├── FilmTableModel.java
│   │   ├── ConnectionDialog.java
│   │   └── SplashScreen.java
│   ├── controller/         # Business logic
│   │   └── FilmController.java
│   ├── util/               # Utility classes
│   │   ├── DatabaseConfig.java
│   │   ├── DatabaseConnectionPool.java
│   │   ├── ValidationUtil.java
│   │   ├── SakilaException.java
│   │   └── AppLogger.java
│   └── SakilaApplication.java  # Main entry point
└── sql/
    ├── 01_create_view.sql
    └── 02_create_stored_procedure.sql
```

## Prerequisites

### Software Requirements
- **Java JDK 8 or higher**
- **MySQL 5.7 or higher**
- **Sakila Sample Database** (installed on MySQL)
- **IDE** (IntelliJ IDEA, Eclipse, or NetBeans recommended)

### Required Libraries
- **MySQL Connector/J** (JDBC driver) - Version 8.0 or higher
- **No other dependencies** - Uses standard java.sql (built into Java)

## Setup Instructions

### 1. Install Sakila Database

If you haven't installed the Sakila database, download it from:
https://dev.mysql.com/doc/sakila/en/

```bash
mysql -u root -p < sakila-schema.sql
mysql -u root -p < sakila-data.sql
```

### 2. Run SQL Scripts

Execute the custom VIEW and Stored Procedure scripts:

```bash
mysql -u root -p sakila < sql/01_create_view.sql
mysql -u root -p sakila < sql/02_create_stored_procedure.sql
```

Or manually in MySQL:
```sql
source /path/to/sql/01_create_view.sql;
source /path/to/sql/02_create_stored_procedure.sql;
```

### 3. Add Dependencies

#### Option A: Using Maven (Recommended)

Create a `pom.xml` file in the project root:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.sakila</groupId>
    <artifactId>rental-system</artifactId>
    <version>1.0.0</version>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

    <dependencies>
        <!-- MySQL Connector - ONLY dependency needed! -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.sakila.SakilaApplication</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

Then run:
```bash
mvn clean install
mvn exec:java -Dexec.mainClass="com.sakila.SakilaApplication"
```

#### Option B: Manual JAR Download

Download only the MySQL Connector JAR and add it to your project classpath:

1. **MySQL Connector/J** (Only dependency needed!)
   - Download from: https://dev.mysql.com/downloads/connector/j/
   - File: `mysql-connector-java-8.0.33.jar`
   - That's it! No other JARs required.

### 4. Configure Database Connection

On first run, the application will prompt you for database connection details:

- **Host**: localhost
- **Port**: 3306
- **Database**: sakila
- **Username**: root (or your MySQL username)
- **Password**: your MySQL password

The configuration will be saved to `database.properties` for future runs.

### 5. Run the Application

#### Using IDE:
- Open the project in your IDE
- Locate `com.sakila.SakilaApplication.java`
- Right-click and select "Run"

#### Using Command Line:
```bash
# Compile
javac -d bin -cp "lib/*" src/com/sakila/**/*.java

# Run
java -cp "bin:lib/*" com.sakila.SakilaApplication
```

## Usage Guide

### Dashboard Tab
- View real-time statistics about films, rentals, and revenue
- See inventory utilization with a progress bar
- Browse top 5 rented films
- View category distribution chart
- Click "Refresh Dashboard" to update data

### Browse Films Tab
- Search films by title or description
- Filter by category and rating
- Sort columns by clicking headers
- Color-coded availability status:
  - **Green**: Available
  - **Yellow**: Low Availability
  - **Red**: All Rented
  - **Gray**: No Inventory
- Double-click any film to view detailed information

### Manage Films Tab
- Create new films with the "New Film" button
- Fill in all required fields (marked with *)
- Select special features using checkboxes
- Click "Save Film" to add or update
- Load existing films from the Browse tab for editing

### Menu Features

**File Menu:**
- Export to CSV - Export all films to a CSV file
- Exit - Close the application

**View Menu:**
- Refresh (F5) - Refresh current panel
- Auto-Refresh - Enable/disable automatic refresh every 30 seconds
- Toggle Dark/Light Theme - Switch between themes

**Tools Menu:**
- Database Connection - Reconfigure database settings
- Connection Pool Statistics - View connection pool metrics

**Help Menu:**
- About - View application information

## Database Schema

### Custom VIEW: `vw_film_rental_details`

Combines data from multiple tables to provide:
- Film information (title, description, rating, etc.)
- Category and language details
- Rental statistics (rental count, available copies)
- Lead actor information
- Revenue data

### Stored Procedure: `sp_manage_film`

Handles both INSERT and UPDATE operations:
- **Parameters**: All film fields + category
- **Logic**: If `p_film_id = 0`, INSERT new film; else UPDATE existing
- **Output**: Result film ID and status message
- **Features**: Transaction management, error handling, validation

## Troubleshooting

### Connection Issues
1. Verify MySQL is running: `sudo service mysql status`
2. Check Sakila database exists: `SHOW DATABASES;`
3. Verify user permissions: `SHOW GRANTS FOR 'username'@'localhost';`
4. Ensure port 3306 is not blocked by firewall

### Missing Dependencies
- Ensure all JAR files are in classpath
- For Maven projects, run `mvn dependency:tree` to check dependencies
- Clear and rebuild project: `mvn clean install`

### SQL Script Errors
- Verify you're connected to the `sakila` database
- Check MySQL version compatibility
- Review error logs in `sakila_app.log`

### UI Issues
- Try toggling theme (View → Toggle Dark/Light Theme)
- Restart application
- Check Java version: `java -version`

## Logging

The application logs to `sakila_app.log` in the application directory:
- **INFO**: Normal operations
- **WARNING**: Non-critical issues
- **SEVERE**: Errors and exceptions

## Performance Tips

1. **Database Connections**: Standard JDBC creates connections on demand - remember to close them!
2. **Auto-Refresh**: Disable if not needed to reduce database load
3. **Large Datasets**: The VIEW is optimized with proper JOINs and indexes
4. **CSV Export**: Exports run in background to avoid UI freezing
5. **Connection Reuse**: Consider adding a connection pool (like HikariCP) for high-traffic scenarios

## Future Enhancements

Potential features for future versions:
- PDF export functionality
- Advanced reporting with JasperReports
- Customer management module
- Rental transaction processing
- Inventory management
- User authentication and roles
- Rental calendar view
- Email notifications

## Credits

- **Database**: MySQL Sakila Sample Database
- **JDBC**: Standard java.sql (built into Java)
- **GUI Framework**: Java Swing
- **Built with**: Claude Code

## License

This project is for educational purposes. The Sakila database is licensed under the New BSD License.

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review logs in `sakila_app.log`
3. Verify database connection using the Tools menu
4. Ensure all SQL scripts have been executed

## Version History

**Version 1.0.0** (Current)
- Initial release
- Film browsing and search
- Film management (Create/Update)
- Dashboard with statistics
- CSV export
- Theme toggle
- Auto-refresh
- Connection pooling
