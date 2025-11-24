package com.istarvin.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Simple database connection manager using standard JDBC (java.sql)
 * No external dependencies required - uses only built-in Java libraries
 */
public class DatabaseConnectionPool {
    private static DatabaseConfig config;
    private static boolean isInitialized = false;

    /**
     * Initialize with database configuration
     */
    public static void initialize(DatabaseConfig dbConfig) throws SQLException {
        config = dbConfig;

        // Load MySQL driver (optional in Java 8+, but good practice)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            isInitialized = true;
            System.out.println("Database connection initialized successfully (using standard JDBC)");
        } catch (ClassNotFoundException e) {
            // Try older MySQL driver name
            try {
                Class.forName("com.mysql.jdbc.Driver");
                isInitialized = true;
                System.out.println("Database connection initialized successfully (using legacy driver)");
            } catch (ClassNotFoundException ex) {
                isInitialized = false;
                throw new SQLException("MySQL JDBC driver not found. Add mysql-connector-java to classpath.", ex);
            }
        }
    }

    /**
     * Get a new database connection
     * Note: Each call creates a new connection - remember to close it!
     */
    public static Connection getConnection() throws SQLException {
        if (!isInitialized || config == null) {
            throw new SQLException("Connection manager is not initialized");
        }

        // Create and return a new connection
        return DriverManager.getConnection(
            config.getJdbcUrl(),
            config.getUsername(),
            config.getPassword()
        );
    }

    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Close - cleanup resources
     * Note: With standard JDBC, individual connections are closed by the caller
     */
    public static void close() {
        isInitialized = false;
        System.out.println("Database connection manager closed");
    }

    /**
     * Check if initialized
     */
    public static boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Get connection statistics
     * Note: Standard JDBC doesn't track connections like a pool does
     */
    public static String getPoolStats() {
        if (isInitialized) {
            return "Using standard JDBC connections (DriverManager)\n" +
                   "Note: Each getConnection() creates a new database connection";
        }
        return "Connection manager not initialized";
    }
}
