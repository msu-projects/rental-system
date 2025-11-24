# Project Structure

Complete overview of the Sakila Film Rental Management System

## Directory Structure

```
rental-system/
│
├── src/
│   └── com/sakila/
│       │
│       ├── SakilaApplication.java          # Main entry point
│       │
│       ├── model/                          # Data Layer
│       │   ├── Film.java                   # Film entity with enums
│       │   ├── Category.java               # Category entity
│       │   ├── Language.java               # Language entity
│       │   ├── DashboardStats.java         # Dashboard statistics model
│       │   ├── FilmDAO.java                # Film data access object
│       │   ├── CategoryDAO.java            # Category data access object
│       │   ├── LanguageDAO.java            # Language data access object
│       │   └── DashboardDAO.java           # Dashboard data access object
│       │
│       ├── view/                           # Presentation Layer
│       │   ├── MainWindow.java             # Main application window
│       │   ├── DashboardPanel.java         # Dashboard with statistics
│       │   ├── FilmBrowserPanel.java       # Film browsing and search
│       │   ├── FilmManagementPanel.java    # Film CRUD operations
│       │   ├── FilmTableModel.java         # Custom table model
│       │   ├── ConnectionDialog.java       # Database connection dialog
│       │   └── SplashScreen.java           # Startup splash screen
│       │
│       ├── controller/                     # Business Logic Layer
│       │   └── FilmController.java         # Main controller
│       │
│       └── util/                           # Utility Classes
│           ├── DatabaseConfig.java         # Database configuration
│           ├── DatabaseConnectionPool.java # JDBC connection manager
│           ├── ValidationUtil.java         # Input validation
│           ├── SakilaException.java        # Custom exception
│           └── AppLogger.java              # Logging utility
│
├── sql/                                    # Database Scripts
│   ├── 01_create_view.sql                 # vw_film_rental_details VIEW
│   └── 02_create_stored_procedure.sql     # sp_manage_film PROCEDURE
│
├── pom.xml                                 # Maven configuration
├── README.md                               # Main documentation
├── QUICKSTART.md                           # Quick start guide
└── PROJECT_STRUCTURE.md                   # This file
```

## Component Responsibilities

### SakilaApplication.java
- **Purpose**: Application entry point
- **Responsibilities**:
  - Initialize logger
  - Show splash screen
  - Load database configuration
  - Initialize connection pool
  - Launch main window
  - Handle startup errors

### Model Layer

#### Entities
- **Film.java**: Film entity with Rating and SpecialFeature enums
- **Category.java**: Film category entity
- **Language.java**: Film language entity
- **DashboardStats.java**: Statistics data structure with FilmStats inner class

#### Data Access Objects (DAOs)
- **FilmDAO.java**:
  - getAllFilms()
  - searchFilms()
  - getFilmById()
  - saveFilm() - uses stored procedure
  - getDistinctCategories/Ratings()

- **CategoryDAO.java**:
  - getAllCategories()
  - getCategoryById()
  - getCategoryDistribution()

- **LanguageDAO.java**:
  - getAllLanguages()
  - getLanguageById()

- **DashboardDAO.java**:
  - getDashboardStats() - aggregates all statistics
  - getTotalFilms/Rentals/Revenue()
  - getInventoryStats()
  - getTopRentedFilms()

### View Layer

#### MainWindow.java
- **Purpose**: Main application container
- **Components**:
  - JTabbedPane with 3 tabs
  - Menu bar (File, View, Tools, Help)
  - Status bar with connection indicator
  - Auto-refresh timer
- **Features**:
  - Theme toggle (dark/light)
  - CSV export
  - Connection management
  - Panel refresh

#### DashboardPanel.java
- **Purpose**: Statistics and visualizations
- **Components**:
  - Statistics cards (films, rentals, revenue)
  - Inventory progress bar
  - Top films text area
  - CategoryChartPanel (custom bar chart)
- **Features**:
  - Real-time data
  - Color-coded indicators
  - Refresh button

#### FilmBrowserPanel.java
- **Purpose**: Browse and search films
- **Components**:
  - Search field
  - Category and rating filters
  - JTable with FilmTableModel
  - TableRowSorter for sorting
- **Features**:
  - Full-text search
  - Multiple filters
  - Color-coded status column
  - Double-click for details
  - Custom cell renderer

#### FilmManagementPanel.java
- **Purpose**: Create and edit films
- **Components**:
  - Form fields for all film properties
  - JComboBoxes for dropdowns
  - JSpinners for numeric values
  - Special features checkboxes
- **Features**:
  - Input validation
  - Visual feedback
  - Load existing films
  - Save/Clear/New buttons

#### FilmTableModel.java
- **Purpose**: Custom AbstractTableModel for JTable
- **Methods**:
  - getRowCount/getColumnCount()
  - getValueAt() - maps Film to table cells
  - setFilms/addFilm/updateFilm/removeFilm()

#### ConnectionDialog.java
- **Purpose**: Database connection configuration
- **Features**:
  - Form fields for connection parameters
  - Test connection
  - Save configuration
  - Visual feedback

#### SplashScreen.java
- **Purpose**: Startup splash screen
- **Features**:
  - Progress bar
  - Status messages
  - Branded appearance

### Controller Layer

#### FilmController.java
- **Purpose**: Business logic coordinator
- **Responsibilities**:
  - Coordinate between View and Model
  - Validate input before saving
  - Provide helper methods for dialogs
  - Error message formatting
- **Methods**:
  - getAllFilms/searchFilms/getFilmById()
  - saveFilm() - with validation
  - getAllCategories/Languages()
  - getDashboardStats()
  - Static helper methods (showError, showSuccess, etc.)

### Utility Layer

#### DatabaseConfig.java
- **Purpose**: Database configuration management
- **Features**:
  - Load/save from properties file
  - Default values
  - JDBC URL generation

#### DatabaseConnectionPool.java
- **Purpose**: Standard JDBC connection manager
- **Features**:
  - Connection initialization
  - Connection retrieval using DriverManager
  - Connection testing
  - Simple statistics
  - Graceful shutdown

#### ValidationUtil.java
- **Purpose**: Input validation
- **Methods**:
  - validateTitle/Year/RentalRate/Length/Duration()
  - ValidationResult inner class
- **Features**:
  - Regex validation
  - Range checking
  - Descriptive error messages

#### SakilaException.java
- **Purpose**: Custom exception class
- **Features**:
  - ErrorType enum
  - Categorized errors
  - Formatted toString()

#### AppLogger.java
- **Purpose**: Centralized logging
- **Features**:
  - File and console logging
  - Multiple log levels
  - Automatic initialization
  - Static helper methods

## Database Components

### VIEW: vw_film_rental_details
- **Purpose**: Optimized film data retrieval
- **Combines**:
  - film, film_category, category, language tables
  - inventory, rental, actor, payment tables
- **Provides**:
  - Film details
  - Rental statistics
  - Availability information
  - Revenue data
  - Lead actor

### Stored Procedure: sp_manage_film
- **Purpose**: Handle INSERT and UPDATE operations
- **Parameters**: All film fields + category_id
- **Logic**:
  - IF p_film_id = 0 THEN INSERT ELSE UPDATE
  - Handle film_category mapping
  - Transaction management
- **Output**:
  - p_result_film_id: New or updated ID
  - p_result_message: Status message

## Data Flow

### Film Browsing Flow
```
User clicks "Browse Films"
  ↓
FilmBrowserPanel.loadData()
  ↓
FilmController.getAllFilms()
  ↓
FilmDAO.getAllFilms()
  ↓
Query vw_film_rental_details
  ↓
Returns List<Film>
  ↓
FilmTableModel.setFilms()
  ↓
JTable displays data
```

### Film Save Flow
```
User fills form and clicks "Save"
  ↓
FilmManagementPanel.saveFilm()
  ↓
FilmController.saveFilm()
  ↓
ValidationUtil validates fields
  ↓
FilmDAO.saveFilm()
  ↓
Calls sp_manage_film stored procedure
  ↓
Returns result film_id
  ↓
Shows success message
  ↓
Reloads saved film
```

### Dashboard Refresh Flow
```
User clicks "Refresh" or auto-refresh triggers
  ↓
DashboardPanel.loadDashboardData()
  ↓
SwingWorker.doInBackground()
  ↓
FilmController.getDashboardStats()
  ↓
DashboardDAO.getDashboardStats()
  ↓
Multiple queries for statistics
  ↓
Returns DashboardStats object
  ↓
SwingWorker.done()
  ↓
DashboardPanel.updateDashboard()
  ↓
Updates all UI components
```

## Key Design Patterns

### MVC Pattern
- **Model**: Entities and DAOs
- **View**: All Swing components
- **Controller**: FilmController coordinates

### Singleton Pattern
- DatabaseConnectionPool
- AppLogger

### Factory Pattern
- Film.Rating.fromString()
- Film.SpecialFeature.fromString()

### Observer Pattern
- SwingWorker for background tasks
- TableModel notifies JTable of changes

### DAO Pattern
- Separate data access from business logic
- FilmDAO, CategoryDAO, LanguageDAO

## Dependencies

### External Libraries
- **MySQL Connector/J 8.0.33**: JDBC driver (ONLY external dependency!)

### Java Standard Library
- **javax.swing**: GUI components
- **java.sql**: Database connectivity
- **java.util.logging**: Logging
- **java.math.BigDecimal**: Precise decimal calculations

## Configuration Files

### database.properties
```properties
db.host=localhost
db.port=3306
db.database=sakila
db.username=root
db.password=******
```
*Created on first successful connection*

### pom.xml
- Maven project configuration
- Dependency management
- Build plugins
- Execution configuration

## Build Outputs

### Maven Build
```
target/
├── classes/                              # Compiled .class files
├── lib/                                  # Dependency JARs
├── rental-system-1.0.0.jar              # Regular JAR
└── rental-system-1.0.0-jar-with-dependencies.jar  # Fat JAR
```

### Log Files
- **sakila_app.log**: Application logs (created at runtime)

## Features Summary

### Core Features ✅
- Film CRUD operations
- Advanced search and filtering
- Real-time dashboard
- Database VIEW and Stored Procedure usage
- Standard JDBC connections

### Advanced Features ✅
- Auto-refresh (configurable)
- CSV export
- Dark/Light theme toggle
- Connection dialog
- Splash screen
- Color-coded status indicators
- Input validation
- Error handling and logging
- Pool statistics

### UI Features ✅
- Tabbed interface
- Sortable tables
- Progress bars
- Custom charts
- Modal dialogs
- Context menus
- Keyboard shortcuts
- Tooltips
- Status bar

## Lines of Code (Approximate)

- **Model Layer**: ~1,500 lines
- **View Layer**: ~1,800 lines
- **Controller Layer**: ~200 lines
- **Utility Layer**: ~600 lines
- **SQL Scripts**: ~200 lines
- **Configuration**: ~150 lines
- **Total**: ~4,450 lines

## Extensibility Points

Easy to add:
- New panels (Customers, Rentals, Inventory)
- New reports
- Additional charts
- Export formats (PDF, Excel)
- Email notifications
- User authentication
- Audit logging
- Advanced filtering
- Batch operations

---

**This is a production-ready, portfolio-quality application demonstrating professional Java development practices!** 🎬✨
