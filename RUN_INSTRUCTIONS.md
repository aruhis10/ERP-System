# How to Run the ERP System Application

## Prerequisites

1. **Java 24** (or compatible version) installed
2. **Maven** installed (optional, but recommended)
3. **MariaDB/MySQL** server running on `localhost:3306`
4. **Database setup**:
   - Database `university_auth_db` created
   - Database `university_erp_db` created
   - Tables created (see database schema)
   - Database credentials in `DatabaseManager.java` match your MySQL root password

## Step 1: Seed the Database (First Time Only)

Before running the application for the first time, you need to seed the database with initial data (users, students, courses, etc.).

### Option A: Using Maven
```bash
mvn exec:java -Dexec.mainClass="edu.univ.erp.data.SeederRunner"
```

### Option B: Using IDE
1. Open `src/main/java/edu/univ/erp/data/SeederRunner.java`
2. Right-click → Run `SeederRunner.main()`

### Option C: Using Java directly
```bash
# Compile first
mvn compile

# Run seeder
java -cp "target/classes;target/dependency/*" edu.univ.erp.data.SeederRunner
```

**Expected Output:**
```
Starting data seeding...
✅ Initial data seeding successful! Accounts are ready to use.
   Admin: admin1 / adminpass | Instructor: inst1 / instpass
   Student 1: stu1 / stu1pass | Student 2: stu2 / stu2pass
```

## Step 2: Run the Application

### Option A: Using Maven (Recommended)
```bash
mvn exec:java -Dexec.mainClass="edu.univ.erp.ui.App"
```

Or simply:
```bash
mvn exec:java
```

### Option B: Using IntelliJ IDEA
1. Open the project in IntelliJ IDEA
2. Navigate to `src/main/java/edu/univ/erp/ui/App.java`
3. Right-click on the `App` class
4. Select **Run 'App.main()'**

### Option C: Using Eclipse
1. Open the project in Eclipse
2. Right-click on `src/main/java/edu/univ/erp/ui/App.java`
3. Select **Run As** → **Java Application**

### Option D: Using Java directly
```bash
# Compile the project
mvn compile

# Run the application
java -cp "target/classes;target/dependency/*" edu.univ.erp.ui.App
```

**Note:** For the `-cp` option, use `:` instead of `;` on Linux/Mac:
```bash
java -cp "target/classes:target/dependency/*" edu.univ.erp.ui.App
```

## Step 3: Login

Once the login window appears, use one of these test accounts:

- **Admin**: `admin1` / `adminpass`
- **Instructor**: `inst1` / `instpass`
- **Student 1**: `stu1` / `stu1pass`
- **Student 2**: `stu2` / `stu2pass`

## Troubleshooting

### Database Connection Error
- Ensure MariaDB/MySQL is running
- Check that the databases `university_auth_db` and `university_erp_db` exist
- Verify the password in `DatabaseManager.java` matches your MySQL root password
- Test connection: `mysql -u root -p` and check databases exist

### ClassNotFoundException
- Run `mvn clean compile` to rebuild the project
- Ensure all dependencies are downloaded: `mvn dependency:resolve`

### Port Already in Use
- Check if another instance of the application is running
- Close any existing instances

### Login Fails
- Make sure you've run the seeder first (Step 1)
- Verify the database has data: `SELECT * FROM university_auth_db.users_auth;`

## Quick Start Script (Windows)

Create a `run.bat` file:
```batch
@echo off
echo Seeding database...
mvn exec:java -Dexec.mainClass="edu.univ.erp.data.SeederRunner"
echo.
echo Starting application...
mvn exec:java -Dexec.mainClass="edu.univ.erp.ui.App"
pause
```

## Quick Start Script (Linux/Mac)

Create a `run.sh` file:
```bash
#!/bin/bash
echo "Seeding database..."
mvn exec:java -Dexec.mainClass="edu.univ.erp.data.SeederRunner"
echo ""
echo "Starting application..."
mvn exec:java -Dexec.mainClass="edu.univ.erp.ui.App"
```

Make it executable: `chmod +x run.sh`

