package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton que gestiona la conexión JDBC con la base de datos SQLite.
 * Crea y inicializa el schema automáticamente si no existe.
 * @author Fatima Roman
 * @version 3.0
 */
public class DatabaseConnection {

    private static final String DB_URL;

    static {
        try {
            // Busca la carpeta raíz del proyecto (donde está el .classpath)
            // y pone el .db en src/resources/
            String projectDir = System.getProperty("user.dir");
            Path dbPath = Paths.get(projectDir, "src", "resources", "monsterhigh.db");

            // Si no existe el archivo, lo crea vacío (SQLite lo inicializa solo)
            if (!Files.exists(dbPath)) {
                Files.createDirectories(dbPath.getParent());
                Files.createFile(dbPath);
                //System.out.println("[DB] Archivo .db creado en: " + dbPath);
            }

            DB_URL = "jdbc:sqlite:" + dbPath.toAbsolutePath().toString();
            //System.out.println("[DB] URL: " + DB_URL);

        } catch (Exception e) {
            throw new RuntimeException("[DB] No se pudo preparar la base de datos: " + e.getMessage());
        }
    }

    private static DatabaseConnection instance;
    private Connection connection;
    private static boolean schemaInitialized = false;

    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(DB_URL);

        // Solo inicializa el schema la primera vez en toda la ejecución
        if (!schemaInitialized) {
            initializeSchema();
            //initializeData();
            schemaInitialized = true;
        }
    }

    /**
     * Lee schema.sql y ejecuta las sentencias CREATE TABLE IF NOT EXISTS.
     */
    private void initializeSchema() {
        URL schemaUrl = DatabaseConnection.class
                .getClassLoader()
                .getResource("resources/schema.sql");

        // Si no lo encuentra por classpath, lo busca por ruta directa
        if (schemaUrl == null) {
            try {
                String projectDir = System.getProperty("user.dir");
                Path schemaPath = Paths.get(projectDir, "src", "resources", "schema.sql");
                schemaUrl = schemaPath.toUri().toURL();
            } catch (Exception e) {
                System.err.println("[DB] No se encontró schema.sql: " + e.getMessage());
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
            System.out.println("[DB] Schema inicializado correctamente.");
            //initializeData();

        } catch (Exception e) {
            System.err.println("[DB] Error al inicializar schema: " + e.getMessage());
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
            System.err.println("[DB] Error al cerrar: " + e.getMessage());
        } finally {
            instance = null;
        }
    }
    
    /**
     * Inserta datos iniciales solo si las tablas están vacías.
     */
    /*private void initializeData() {
        try {
            try (Statement checkStmt = connection.createStatement();
                 ResultSet rs = checkStmt.executeQuery("SELECT COUNT(*) FROM STUDENT")) {
                if (rs.getInt(1) > 0) {
                    System.out.println("[DB] Datos ya existentes, no se insertan de nuevo.");
                    return;
                }
            }

            URL dataUrl = DatabaseConnection.class
                    .getClassLoader()
                    .getResource("resources/data.sql");

            if (dataUrl == null) {
                String projectDir = System.getProperty("user.dir");
                Path dataPath = Paths.get(projectDir, "src", "resources", "data.sql");
                dataUrl = dataPath.toUri().toURL();
            }

            StringBuilder sql = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(dataUrl.openStream()))) {
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
            System.out.println("[DB] Datos iniciales insertados correctamente.");

        } catch (Exception e) {
            System.err.println("[DB] Error al insertar datos: " + e.getMessage());
        }
    }*/
}