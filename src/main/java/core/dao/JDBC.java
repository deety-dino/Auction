package core.dao;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBC {
    private static final String SQLITE_PREFIX = "jdbc:sqlite:";
    private static final String DEFAULT_DB_PATH = "data/auction.db";

    private Connection connection;
    private String databasePath = DEFAULT_DB_PATH;
    private Path resolvedDbPath;
    private static JDBC instance;
    
    private JDBC() {
    }

    public static JDBC getInstance() {
        if (instance == null) {
            instance = new JDBC();
        }
        return instance;
    }
    
    public synchronized void connect() throws SQLException {
        connect(this.databasePath);
    }

    public synchronized void connect(String databasePath) throws SQLException {
        if (databasePath != null && !databasePath.isBlank()) {
            this.databasePath = databasePath;
        }

        if (connection != null && !connection.isClosed()) {
            return;
        }

        resolvedDbPath = resolveDatabasePath(this.databasePath);
        createDatabaseParentDirectory(resolvedDbPath);
        connection = DriverManager.getConnection(buildUrl(resolvedDbPath));
        initializeDatabase();
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    public synchronized PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    private String buildUrl(Path dbPath) {
        return SQLITE_PREFIX + dbPath;
    }

    public ResultSet query(String q) throws SQLException {
        if (q == null || q.trim().isEmpty()) {
            throw new IllegalArgumentException("Query cannot be null or empty");
        }

        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is not open. Call connect() first.");
        }

        Statement statement = connection.createStatement();
        return statement.executeQuery(q);
    }
    
    public synchronized void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public synchronized void initializeDatabase() throws SQLException {
        Connection currentConnection = getConnection();
        try (Statement statement = currentConnection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");

            // Auth table used by login/signup module.
            statement.execute(
                "CREATE TABLE IF NOT EXISTS users ("
                    + "user_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "username TEXT NOT NULL UNIQUE, "
                    + "email TEXT NOT NULL UNIQUE, "
                    + "password TEXT NOT NULL"
                    + ")"
            );

            statement.execute(
                "CREATE TABLE IF NOT EXISTS ingredients ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, "
                    + "category TEXT, "
                    + "unit TEXT NOT NULL"
                    + ")"
            );

            statement.execute(
                "CREATE TABLE IF NOT EXISTS ingredient_imports ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "ingredient_id INTEGER NOT NULL, "
                    + "import_date TEXT NOT NULL, "
                    + "quantity REAL NOT NULL, "
                    + "unit_price REAL NOT NULL, "
                    + "FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE"
                    + ")"
            );

            statement.execute(
                "CREATE TABLE IF NOT EXISTS products ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT NOT NULL, "
                    + "description TEXT, "
                    + "selling_price REAL NOT NULL"
                    + ")"
            );

            statement.execute(
                "CREATE TABLE IF NOT EXISTS recipes ("
                    + "product_id INTEGER NOT NULL, "
                    + "ingredient_id INTEGER NOT NULL, "
                    + "quantity_needed REAL NOT NULL, "
                    + "PRIMARY KEY (product_id, ingredient_id), "
                    + "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE"
                    + ")"
            );

            statement.execute(
                "CREATE TABLE IF NOT EXISTS daily_productions ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "product_id INTEGER NOT NULL, "
                    + "production_date TEXT NOT NULL, "
                    + "quantity_produced INTEGER NOT NULL, "
                    + "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE"
                    + ")"
            );

            statement.execute(
                "CREATE TABLE IF NOT EXISTS daily_sales ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "product_id INTEGER NOT NULL, "
                    + "sale_date TEXT NOT NULL, "
                    + "quantity_sold INTEGER NOT NULL, "
                    + "actual_selling_price REAL NOT NULL, "
                    + "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE"
                    + ")"
            );

            statement.execute(
                "CREATE TABLE IF NOT EXISTS fixed_costs ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "expense_name TEXT NOT NULL, "
                    + "expense_date TEXT NOT NULL, "
                    + "amount REAL NOT NULL"
                    + ")"
            );
        }
    }

    public synchronized String getResolvedDatabasePath() {
        if (resolvedDbPath == null) {
            resolvedDbPath = resolveDatabasePath(databasePath);
        }
        return resolvedDbPath.toString();
    }

    private Path resolveDatabasePath(String configuredPath) {
        String rawPath = (configuredPath == null || configuredPath.isBlank()) ? DEFAULT_DB_PATH : configuredPath;
        Path candidate = Paths.get(rawPath);
        if (!candidate.isAbsolute()) {
            candidate = Paths.get(System.getProperty("user.dir")).resolve(candidate);
        }
        return candidate.normalize();
    }

    private void createDatabaseParentDirectory(Path dbPath) throws SQLException {
        try {
            Path parent = dbPath.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (Exception exception) {
            throw new SQLException("Cannot create SQLite data directory: " + exception.getMessage(), exception);
        }
    }
    
}
