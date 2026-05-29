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
  Build: Apache Maven

------------------------------------------------------------

--- EXTERNAL DEPENDENCIES ---

Dependencies are managed automatically by Maven.
No manual library setup is required.

  1. JavaFX 21.0.2 (for the graphical interface)
  2. SQLite JDBC 3.53.1.0 (for the database)

Maven downloads them automatically on the first build.
Internet connection is required on first run.

------------------------------------------------------------

--- HOW TO SET UP IN ECLIPSE ---

1. Open Eclipse and go to:
   File > Import > Maven > Existing Maven Projects
   Select the project root folder and click Finish.

2. Let Maven download the dependencies:
   Eclipse will do this automatically after import.
   Wait for the progress bar at the bottom to finish.
   If it does not start, right-click the project and choose:
   Maven > Update Project (Alt+F5) > OK

3. Click Run on MainMenu.java.
   No VM arguments or manual library configuration needed.

------------------------------------------------------------

--- DATA FILES ---

The application reads and writes CSV files from the resources/ folder.
These files are the persistent data source.

  src/resources/students.csv
  src/resources/teachers.csv
  src/resources/subjects.csv
  src/resources/monster_types.csv
  src/resources/groups.csv
  src/resources/enrollments.csv
  src/resources/schedules.csv

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

  src/resources/monsterhigh.db

The tables are recreated every time the application starts,
based on the CSV files. You do not need to touch the .db file
directly.

------------------------------------------------------------

--- PROJECT STRUCTURE ---

  src/
  ├── model/            Domain classes (Student, Teacher, Subject...)
  ├── repository/       DAOs and GenericRepositoryBD
  ├── service/          Business logic layer
  ├── ui/               Console interface menus
  │   └── javafx/       JavaFX graphical interface
  ├── exceptions/       Custom exceptions
  ├── util/             DatabaseConnection (Singleton) and CsvUtil
  ├── resources/        CSV data files and SQLite database
  └── module-info.java  Java module descriptor

  pom.xml               Maven configuration (dependencies and build)

------------------------------------------------------------

--- TROUBLESHOOTING ---

  If the project does not compile:
  - Right-click the project > Maven > Update Project (Alt+F5)
  - Make sure you have an active internet connection on first build
  - Check that Eclipse is using Java 21:
    Window > Preferences > Java > Installed JREs

  If the JavaFX window does not open:
  - Use option 0 to exit and relaunch the application
  - JavaFX can only be launched once per JVM session

  If the CSV files are missing:
  - Check that src/resources/ exists and contains the data files
  - Do not delete the resources/ folder

============================================================