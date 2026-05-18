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

	private static final String DB_URL;

	static {
	    try {
	        java.net.URL resource = DatabaseConnection.class
	            .getClassLoader()
	            .getResource("resources/monsterhigh.db");

	        if (resource == null) {
	            throw new RuntimeException("[DB] No se encontró monsterhigh.db en resources/");
	        }

	        String path = resource.toURI().getPath();
	        DB_URL = "jdbc:sqlite:" + path;

	    } catch (java.net.URISyntaxException e) {
	        throw new RuntimeException("[DB] Error al construir la ruta: " + e.getMessage());
	    }
	}
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
