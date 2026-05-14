package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton that manages the JDBC connection to the SQLite database.
 * @author Fatima Roman
 * @version 1.0
 */
public class DatabaseConnection {

	//FIX with help (claude) 
	static String dbPath = System.getProperty("user.dir") + "/src/resources/monsterhigh.db";
	private static final String DB_URL = "jdbc:sqlite:" + dbPath;
	private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(DB_URL);
    }

    public static synchronized DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() { return connection; }

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
