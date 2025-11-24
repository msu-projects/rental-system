package com.istarvin;

import com.istarvin.util.AppLogger;
import com.istarvin.util.DatabaseConfig;
import com.istarvin.util.DatabaseConnectionPool;
import com.istarvin.view.ConnectionDialog;
import com.istarvin.view.MainWindow;
import com.istarvin.view.SplashScreen;

import javax.swing.*;

/**
 * Main application class
 * Entry point for the Sakila Film Rental Management System
 */
public class SakilaApplication {

    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }

        // Initialize logger
        AppLogger.initialize();
        AppLogger.info("=== Sakila Application Starting ===");

        // Show splash screen
        SplashScreen splash = new SplashScreen();
        splash.setVisible(true);

        // Initialize application in background
        SwingWorker<Boolean, Integer> initWorker = new SwingWorker<Boolean, Integer>() {
            private String errorMessage = null;

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // Step 1: Load configuration
                    publish(20);
                    splash.setProgress(20, "Loading configuration...");
                    Thread.sleep(300);

                    DatabaseConfig config = DatabaseConfig.loadFromFile();

                    // Step 2: Initialize connection pool
                    publish(40);
                    splash.setProgress(40, "Initializing database connection...");
                    Thread.sleep(300);

                    try {
                        DatabaseConnectionPool.initialize(config);
                    } catch (Exception e) {
                        AppLogger.warning("Initial connection failed, will show connection dialog");
                    }

                    // Step 3: Test connection
                    publish(60);
                    splash.setProgress(60, "Testing database connection...");
                    Thread.sleep(300);

                    boolean connected = DatabaseConnectionPool.testConnection();

                    // Step 4: Load application components
                    publish(80);
                    splash.setProgress(80, "Loading application...");
                    Thread.sleep(300);

                    // Step 5: Complete
                    publish(100);
                    splash.setProgress(100, "Ready!");
                    Thread.sleep(200);

                    return connected;

                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    AppLogger.error("Initialization failed", e);
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    splash.close();

                    Boolean connected = get();

                    // Show connection dialog if not connected
                    if (!connected) {
                        AppLogger.info("Showing connection dialog");

                        ConnectionDialog connectionDialog = new ConnectionDialog(null);
                        connectionDialog.setVisible(true);

                        if (!connectionDialog.isConnected()) {
                            AppLogger.warning("User cancelled connection, exiting application");
                            JOptionPane.showMessageDialog(null,
                                    "Database connection is required to run the application.",
                                    "Connection Required",
                                    JOptionPane.WARNING_MESSAGE);
                            System.exit(0);
                            return;
                        }
                    }

                    // Launch main window
                    AppLogger.info("Launching main window");
                    SwingUtilities.invokeLater(() -> {
                        MainWindow mainWindow = new MainWindow();
                        mainWindow.showWindow();
                        AppLogger.info("Application started successfully");
                    });

                } catch (Exception e) {
                    AppLogger.error("Failed to start application", e);
                    JOptionPane.showMessageDialog(null,
                            "Failed to start application:\n" + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        };

        initWorker.execute();
    }
}
