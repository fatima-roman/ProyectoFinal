============================================================
  MONSTER HIGH INSTITUTE MANAGER
  Final Project — Programming Module, 1st DAM
  IES Nervión · 2025-2026
  Author: Fátima Román
============================================================

--- OVERVIEW ---

Monster High Institute Manager is a Java desktop application
for managing a fictional school. It handles students, teachers,
subjects, groups, enrolments, timetables and monster types.

Data is saved in CSV files and loaded into a SQLite database
on every startup.

------------------------------------------------------------

--- IDE AND JAVA VERSION ---

  IDE:   Eclipse IDE (2024)
  Java:  Java 21

------------------------------------------------------------

--- EXTERNAL DEPENDENCIES ---

Two libraries are required. Both are already included in the
lib/ folder of the project.

  1. JavaFX SDK (for the graphical interface)
     Folder: lib/javafx-sdk/

  2. SQLite JDBC Driver (for the database)
     File: lib/sqlite-jdbc.jar

No Maven or Gradle. Dependencies are managed manually.

------------------------------------------------------------

--- HOW TO SET UP IN ECLIPSE ---

1. Open Eclipse and go to:
   File > Import > General > Existing Projects into Workspace
   Select the project root folder and click Finish.

2. Add the libraries to the Build Path:
   Right-click the project > Build Path > Configure Build Path
   Go to the Libraries tab > Add JARs
   Add: lib/sqlite-jdbc.jar
   Add all JARs inside: lib/javafx-sdk/lib/

3. Configure the VM arguments for JavaFX:
   Run > Run Configurations > select your Main class
   Go to the Arguments tab > VM arguments
   Paste this:

   --module-path lib/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml

4. Click Run.

------------------------------------------------------------

--- DATA FILES ---

The application reads and writes CSV files from the resources/ folder.
These files are the persistent data source.

  resources/students.csv
  resources/teachers.csv
  resources/subjects.csv
  resources/subjects.csv
  resources/monster_types.csv
  resources/groups.csv
  resources/enrollments.csv
  resources/schedules.csv

On startup:   CSV files are read and loaded into SQLite.
On exit:      All data is saved back to the CSV files.

IMPORTANT: always close the application using the Exit button
or option 0 in the console menu. If you force-close the window,
the latest changes may not be saved to the CSV files.

------------------------------------------------------------

--- DATABASE ---

The application uses SQLite. No installation or configuration
is needed. The database file is created automatically on the
first run at:

  resources/monster_high.db

The tables are recreated every time the application starts,
based on the CSV files. You do not need to touch the .db file
directly.

------------------------------------------------------------

--- PROJECT STRUCTURE ---

  src/
  ├── model/          Domain classes (Student, Teacher, Subject...)
  ├── repository/     DAOs and GenericRepositoryBD
  ├── service/        Business logic layer
  ├── ui/             JavaFX controllers and console interface
  ├── exceptions/     Custom exceptions
  ├── util/           DatabaseConnection (Singleton) and CsvUtil
  └── Main.java       Entry point

  lib/
  ├── javafx-sdk/     JavaFX SDK
  └── sqlite-jdbc.jar SQLite driver

  resources/           CSV data files
  doc/                Javadoc HTML (generated)

------------------------------------------------------------

  If something does not work, check that:
  - The VM arguments are set correctly in Run Configurations
  - Both libraries are on the Build Path
  - The resources/ folder exists and has the CSV files

============================================================