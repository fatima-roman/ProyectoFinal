============================================================
  Monster High Institute Manager — README
  Author : Fatima Roman
  Module : Programación — 1º DAM — IES Nervión
  Year   : 2025–2026
============================================================

UML --> https://drive.google.com/file/d/14yvfAPpQYevNp9f3-d4-moki_XGgbyw-/view?usp=sharing

------------------------------------------------------------
1. REQUIREMENTS
------------------------------------------------------------
- Java 21 (JDK 21 LTS)
- Eclipse IDE (2023-09 or newer recommended)
- JavaFX SDK 21  →  https://gluonhq.com/products/javafx/
    · Choose: JavaFX 21 LTS · SDK · Windows x64
    · Extract to a path WITHOUT spaces, e.g.  C:\javafx-sdk-21
- SQLite JDBC driver (already included in src/lib/)

------------------------------------------------------------
2. PROJECT STRUCTURE
------------------------------------------------------------
ProyectoFinal/
├── src/
│   ├── exceptions/       Custom exception classes
│   ├── model/            Domain entities (Student, Teacher, etc.)
│   │   └── interfaces/   Buscable, Evaluable, Exportable, Identifiable
│   ├── repository/       DAOs — JDBC access to SQLite
│   ├── service/          Business logic layer
│   ├── ui/
│   │   ├── MainMenu.java          Console entry point  ← run this
│   │   ├── MainMenu*.java         Console sub-menus
│   │   └── javafx/
│   │       ├── MonsterHighApp.java  JavaFX entry point (standalone)
│   │       ├── DashboardView.java
│   │       ├── StudentView.java
│   │       ├── TeacherView.java
│   │       ├── SubjectView.java
│   │       ├── EnrollmentView.java
│   │       └── MonsterHighStyles.java
│   ├── util/
│   │   ├── DatabaseConnection.java  SQLite singleton
│   │   └── CsvUtil.java             CSV import / export
│   └── resources/
│       ├── monsterhigh.db           SQLite database (auto-created)
│       ├── schema.sql
│       └── initial/                 Seed CSV files
└── README.txt  ← this file


------------------------------------------------------------
3. SETTING UP JAVAFX IN ECLIPSE  (do this once)
------------------------------------------------------------

STEP 1 — Add the JavaFX JARs to the Build Path
  1. Right-click the project → Build Path → Configure Build Path…
  2. Open the "Libraries" tab → "Classpath" node → Add External JARs…
  3. Navigate to  C:\javafx-sdk-21\lib\
  4. Select ALL of these JARs (Ctrl+click each):
       javafx.base.jar
       javafx.controls.jar
       javafx.fxml.jar
       javafx.graphics.jar
       javafx.media.jar
       javafx.swing.jar
  5. Click "Apply and Close"

  NOTE: If you already have old entries pointing to a different path
  (e.g. C:\Users\Windows\Downloads\openjfx-...), remove them first
  and re-add from the new location.

STEP 2 — Add the VM Argument to the Run Configuration
  This step is MANDATORY — without it JavaFX crashes at runtime
  even if the JARs are on the classpath.

  1. Right-click project → Run As → Run Configurations…
  2. Select the "MainMenu" Java Application entry
     (or create one: Main class = ui.MainMenu)
  3. Go to the "Arguments" tab
  4. In the "VM arguments" box paste this line
     (replace the path if your SDK is elsewhere):

     --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base

  5. Click "Apply" then "Run"


------------------------------------------------------------
4. HOW TO COMPILE AND RUN
------------------------------------------------------------

  OPTION A — Run from Eclipse (recommended)
  ------------------------------------------
  1. Complete Section 3 above.
  2. Right-click  src/ui/MainMenu.java  → Run As → Java Application
  3. The console menu appears. Choose option 9 to open the JavaFX GUI.

  OPTION B — Run JavaFX standalone (without the console menu)
  -----------------------------------------------------------
  1. Right-click  src/ui/javafx/MonsterHighApp.java → Run As → Run Configurations…
  2. Set Main class to:  ui.javafx.MonsterHighApp
  3. Add the same VM argument from Step 2 above.
  4. Click Run.

------------------------------------------------------------
5. DATABASE
------------------------------------------------------------
- Engine  : SQLite (no installation required — file-based)
- Driver  : sqlite-jdbc-3.53.1.0.jar  (already in src/lib/)
- Location: src/resources/monsterhigh.db
            Created automatically on first launch.
- Schema  : src/resources/schema.sql  (applied automatically)
- No credentials needed.

On first run, if the database is empty, the application loads
seed data automatically from the CSV files in src/resources/initial/.


------------------------------------------------------------
6. NAVIGATION IN THE JAVAFX GUI
------------------------------------------------------------
The GUI opens on the Dashboard, which shows summary cards for
students, teachers, subjects and enrollments.

Left sidebar buttons navigate between views:
  🏠 Dashboard   — overview cards with live counts
  🧟 Students    — full CRUD: add, edit, delete, search
  🧙 Teachers    — full CRUD: add, edit, delete, search
  📚 Subjects    — full CRUD: add, edit, delete
  📋 Enrollments — add / delete; colour-coded grade badges

IMPORTANT: option 9 in the console menu launches the JavaFX
window. Once you close that window, you CANNOT reopen it from
the same console session (JavaFX can only be launched once
per JVM process). Restart the app to open the GUI again.


------------------------------------------------------------
7. CSV PERSISTENCE
------------------------------------------------------------
On exit (console option 0 — "Exit"), the application saves
the current state to three CSV files:
  bin/students.csv
  bin/subjects.csv
  bin/teachers.csv

These files are human-readable and can be opened in any
text editor or spreadsheet application.


------------------------------------------------------------
8. COMMON ERRORS AND SOLUTIONS
------------------------------------------------------------

  Error: "JavaFX runtime components are missing"
  → You forgot the VM argument. See Section 3, Step 2.

  Error: "ClassNotFoundException: javafx.application.Application"
  → The JavaFX JARs are not on the classpath. See Section 3, Step 1.

  Error: "IllegalStateException: Application launch must not be
          called more than once"
  → You opened the JavaFX window (option 9) more than once in the
    same session. Restart the application.

  Error: "No suitable driver found for jdbc:sqlite:..."
  → The SQLite JAR is missing from the classpath.
    In Eclipse: Build Path → Add External JARs → select
    src/lib/sqlite-jdbc-3.53.1.0.jar

  The database file is missing / tables do not exist
  → Delete  src/resources/monsterhigh.db  and restart.
    The app re-creates the schema automatically.


------------------------------------------------------------
9. EXTERNAL DEPENDENCIES
------------------------------------------------------------
  Library                        Version   Location
  ────────────────────────────── ───────── ──────────────────
  sqlite-jdbc                    3.53.1.0  src/lib/  (bundled)
  JavaFX SDK                     21 LTS    external — see §3
  JUnit 5 (optional, for tests)  5.x       not included


