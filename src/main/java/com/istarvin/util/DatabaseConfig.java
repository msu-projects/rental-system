package com.istarvin.util;

import java.io.*;
import java.util.Properties;

/**
 * Database configuration management class
 * Loads and stores database connection parameters
 */
public class DatabaseConfig {
    private static final String CONFIG_FILE = "database.properties";
    private static Properties properties;

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    // Default values
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 3306;
    public static final String DEFAULT_DATABASE = "sakila";
    public static final String DEFAULT_USERNAME = "root";
    public static final String DEFAULT_PASSWORD = "";

    /**
     * Constructor with default values
     */
    public DatabaseConfig() {
        this.host = DEFAULT_HOST;
        this.port = DEFAULT_PORT;
        this.database = DEFAULT_DATABASE;
        this.username = DEFAULT_USERNAME;
        this.password = DEFAULT_PASSWORD;
    }

    /**
     * Constructor with custom values
     */
    public DatabaseConfig(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * Load configuration from properties file
     */
    public static DatabaseConfig loadFromFile() {
        properties = new Properties();
        DatabaseConfig config = new DatabaseConfig();

        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            config.setHost(properties.getProperty("db.host", DEFAULT_HOST));
            config.setPort(Integer.parseInt(properties.getProperty("db.port", String.valueOf(DEFAULT_PORT))));
            config.setDatabase(properties.getProperty("db.database", DEFAULT_DATABASE));
            config.setUsername(properties.getProperty("db.username", DEFAULT_USERNAME));
            config.setPassword(properties.getProperty("db.password", DEFAULT_PASSWORD));
        } catch (IOException e) {
            // File doesn't exist, return default config
            System.out.println("Config file not found, using defaults");
        }

        return config;
    }

    /**
     * Save configuration to properties file
     */
    public void saveToFile() throws IOException {
        properties = new Properties();
        properties.setProperty("db.host", host);
        properties.setProperty("db.port", String.valueOf(port));
        properties.setProperty("db.database", database);
        properties.setProperty("db.username", username);
        properties.setProperty("db.password", password);

        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Sakila Database Configuration");
        }
    }

    /**
     * Get JDBC URL from configuration
     */
    public String getJdbcUrl() {
        return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, database);
    }

    // Getters and setters
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
