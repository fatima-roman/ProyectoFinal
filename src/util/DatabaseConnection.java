package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
* Singleton that manages the JDBC connection to the SQLite database.
* Automatically creates and initializes the schema if it doesn't exist.
 * @author Fatima Roman
 * @version 3.0
 */
public class DatabaseConnection {

    private static final String DB_URL;

    static {
        try {
        	// Locate the project's root folder (where the .classpath is located)
        	// and place the .db file in src/resources/
            String projectDir = System.getProperty("user.dir");
            Path dbPath = Paths.get(projectDir, "src", "resources", "monsterhigh.db");

         // If the file does not exist, it creates an empty file (SQLite initializes it automatically)
            if (!Files.exists(dbPath)) {
                Files.createDirectories(dbPath.getParent());
                Files.createFile(dbPath);
            }

            DB_URL = "jdbc:sqlite:" + dbPath.toAbsolutePath().toString();

        } catch (Exception e) {
            throw new RuntimeException("[DB] The database could not be prepared: " + e.getMessage());
        }
    }

    private static DatabaseConnection instance;
    private Connection connection;
    private static boolean schemaInitialized = false;

    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(DB_URL);

        if (!schemaInitialized) {
            initializeSchema();
            schemaInitialized = true;
        }
    }

    /**
     * Read schema.sql and execute the statements CREATE TABLE .
     */
    private void initializeSchema() {
        URL schemaUrl = DatabaseConnection.class
                .getClassLoader()
                .getResource("resources/schema.sql");

     // If it cannot be found by classpath, it searches by direct path
        if (schemaUrl == null) {
            try {
                String projectDir = System.getProperty("user.dir");
                Path schemaPath = Paths.get(projectDir, "src", "resources", "schema.sql");
                schemaUrl = schemaPath.toUri().toURL();
            } catch (Exception e) {
                System.err.println("[DB] schema.sql not found: " + e.getMessage());
                return;
            }
        }

        try {
            StringBuilder sql = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(schemaUrl.openStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("--") || trimmed.isEmpty()) continue;
                    sql.append(trimmed).append(" ");
                }
            }

            for (String statement : sql.toString().split(";")) {
                String s = statement.trim();
                if (!s.isEmpty()) {
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute(s);
                    }
                }
            }
            //System.out.println("[DB] Schema initialized correctly.");

        } catch (Exception e) {
            System.err.println("[DB] Error initializing schema: " + e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            System.err.println("[DB] Error closing: " + e.getMessage());
        } finally {
            instance = null;
        }
    }
    
   
}