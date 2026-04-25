package core.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBC {
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";

    private DBUser dbUser;
    private Connection connection;
    private String databaseName = "";
    private static JDBC instance;
    
    private JDBC() {
        dbUser = DBUser.getInstance();
    }
    public static JDBC getInstance() {
        if (instance == null) {
            instance = new JDBC();
        }
        return instance;
    }
    
    public synchronized void connect() throws SQLException {
        connect(this.databaseName);
    }

    public synchronized void connect(String databaseName) throws SQLException {
        if (databaseName != null) {
            this.databaseName = databaseName;
        }

        if (connection != null && !connection.isClosed()) {
            return;
        }

        connection = DriverManager.getConnection(buildUrl(), dbUser.getUsername(), dbUser.getPassword());
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

    private String buildUrl() {
        if (databaseName == null || databaseName.isBlank()) {
            return BASE_URL;
        }
        return BASE_URL + databaseName;
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
    
}
