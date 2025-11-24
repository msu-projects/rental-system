# Quick Start Guide

Get up and running with the Sakila Film Rental Management System in 5 minutes!

## Prerequisites Checklist

- [ ] Java JDK 8+ installed
- [ ] MySQL installed and running
- [ ] Sakila database installed
- [ ] Maven installed (or use IDE with Maven support)

## 3-Step Setup

### Step 1: Database Setup (2 minutes)

```bash
# If Sakila is not installed, download and install it first:
# https://dev.mysql.com/doc/sakila/en/

# Run the custom SQL scripts
cd rental-system
mysql -u root -p sakila < sql/01_create_view.sql
mysql -u root -p sakila < sql/02_create_stored_procedure.sql
```

### Step 2: Build the Application (1 minute)

#### Using Maven:
```bash
mvn clean install
```

#### Using IntelliJ IDEA:
1. Open the project folder
2. Maven dependencies will auto-import
3. Wait for indexing to complete

#### Using Eclipse:
1. Import as "Existing Maven Project"
2. Right-click project → Maven → Update Project

### Step 3: Run the Application (30 seconds)

#### Using Maven:
```bash
mvn exec:java -Dexec.mainClass="com.sakila.SakilaApplication"
```

#### Using IDE:
1. Navigate to `src/com/sakila/SakilaApplication.java`
2. Right-click → Run

#### Using JAR:
```bash
# After building
java -jar target/rental-system-1.0.0-jar-with-dependencies.jar
```

## First Run Configuration

When the application starts for the first time:

1. A splash screen appears
2. Connection dialog opens
3. Enter your database credentials:
   - **Host**: `localhost`
   - **Port**: `3306`
   - **Database**: `sakila`
   - **Username**: `root` (or your MySQL username)
   - **Password**: (your MySQL password)
4. Click "Connect"

The configuration is saved for future runs!

## Test the Application

### 1. Dashboard
- Open the application
- You should see the Dashboard tab with statistics
- Verify data is loading correctly

### 2. Browse Films
- Click "Browse Films" tab
- You should see a table with all films
- Try searching for "ACADEMY"
- Filter by category "Action"

### 3. Manage Films
- Click "Manage Films" tab
- Fill in the form to create a new film
- Click "Save Film"
- Check that it appears in the Browse tab

## Common Issues

### Can't connect to database
```bash
# Check MySQL is running
mysql -u root -p -e "SELECT 1"

# Verify Sakila exists
mysql -u root -p -e "SHOW DATABASES LIKE 'sakila'"
```

### Missing dependencies
```bash
# Clean and rebuild
mvn clean install -U

# Or download JARs manually (see README)
```

### View or Procedure not found
```bash
# Re-run SQL scripts
mysql -u root -p sakila < sql/01_create_view.sql
mysql -u root -p sakila < sql/02_create_stored_procedure.sql

# Verify they exist
mysql -u root -p sakila -e "SHOW FULL TABLES WHERE Table_type = 'VIEW'"
mysql -u root -p sakila -e "SHOW PROCEDURE STATUS WHERE Db = 'sakila'"
```

## Quick Feature Tour

### Dashboard Features:
- ✅ Real-time film and rental statistics
- ✅ Inventory utilization progress bar
- ✅ Top 5 rented films
- ✅ Category distribution chart

### Browse Films Features:
- ✅ Search by title/description
- ✅ Filter by category and rating
- ✅ Sortable columns
- ✅ Color-coded availability status
- ✅ Double-click for details

### Manage Films Features:
- ✅ Create new films
- ✅ Edit existing films
- ✅ Form validation
- ✅ Special features selection

### Menu Features:
- ✅ CSV Export (File → Export to CSV)
- ✅ Auto-refresh (View → Auto-Refresh)
- ✅ Dark/Light theme (View → Toggle Theme)
- ✅ Database reconnection (Tools → Database Connection)

## Build Options

### Regular JAR (requires lib folder):
```bash
mvn package
java -jar target/rental-system-1.0.0.jar
```

### Fat JAR (all dependencies included):
```bash
mvn package
java -jar target/rental-system-1.0.0-jar-with-dependencies.jar
```

### Run without building:
```bash
mvn exec:java
```

## Next Steps

1. **Read the README** for detailed documentation
2. **Explore the code** - well-organized MVC structure
3. **Customize** - add your own features
4. **Extend** - add customer management, rental processing, etc.

## Support

- Check `sakila_app.log` for error details
- See README.md for comprehensive documentation
- Review SQL scripts in `sql/` directory

---

**Enjoy using the Sakila Film Rental Management System!** 🎬
