package com.istarvin.util;

import java.io.IOException;
import java.util.logging.*;

/**
 * Application logging utility
 * Logs to both file and console
 */
public class AppLogger {
    private static Logger logger;
    private static boolean initialized = false;

    /**
     * Initialize the logger
     */
    public static void initialize() {
        if (initialized) {
            return;
        }

        logger = Logger.getLogger("SakilaApp");
        logger.setLevel(Level.ALL);

        try {
            // File handler - logs to file
            FileHandler fileHandler = new FileHandler("sakila_app.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // Console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            initialized = true;
            logger.info("Logger initialized successfully");
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    /**
     * Get the logger instance
     */
    public static Logger getLogger() {
        if (!initialized) {
            initialize();
        }
        return logger;
    }

    /**
     * Log info message
     */
    public static void info(String message) {
        getLogger().info(message);
    }

    /**
     * Log warning message
     */
    public static void warning(String message) {
        getLogger().warning(message);
    }

    /**
     * Log error message
     */
    public static void error(String message, Throwable throwable) {
        getLogger().log(Level.SEVERE, message, throwable);
    }

    /**
     * Log debug message
     */
    public static void debug(String message) {
        getLogger().fine(message);
    }
}
