package com.istarvin.view;

import com.istarvin.util.DatabaseConfig;
import com.istarvin.util.DatabaseConnectionPool;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Connection configuration dialog
 */
public class ConnectionDialog extends JDialog {
    private JTextField hostField;
    private JTextField portField;
    private JTextField databaseField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton connectButton;
    private JButton cancelButton;
    private JLabel statusLabel;

    private DatabaseConfig config;
    private boolean connected = false;

    public ConnectionDialog(Frame parent) {
        super(parent, "Database Connection", true);
        config = DatabaseConfig.loadFromFile();
        initializeComponents();
        layoutComponents();
        loadConfig();
        setSize(450, 350);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        hostField = new JTextField(20);
        portField = new JTextField(10);
        databaseField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        connectButton = new JButton("Connect");
        cancelButton = new JButton("Cancel");

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);

        // Event handlers
        connectButton.addActionListener(e -> connect());
        cancelButton.addActionListener(e -> cancel());
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Host:"));
        formPanel.add(hostField);

        formPanel.add(new JLabel("Port:"));
        formPanel.add(portField);

        formPanel.add(new JLabel("Database:"));
        formPanel.add(databaseField);

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(connectButton);
        buttonPanel.add(cancelButton);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.NORTH);
    }

    private void loadConfig() {
        hostField.setText(config.getHost());
        portField.setText(String.valueOf(config.getPort()));
        databaseField.setText(config.getDatabase());
        usernameField.setText(config.getUsername());
        passwordField.setText(config.getPassword());
    }

    private void connect() {
        connectButton.setEnabled(false);
        statusLabel.setText("Connecting...");
        statusLabel.setForeground(Color.BLUE);

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            String errorMessage = null;

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // Update config
                    config.setHost(hostField.getText().trim());
                    config.setPort(Integer.parseInt(portField.getText().trim()));
                    config.setDatabase(databaseField.getText().trim());
                    config.setUsername(usernameField.getText().trim());
                    config.setPassword(new String(passwordField.getPassword()));

                    // Initialize connection pool
                    DatabaseConnectionPool.initialize(config);

                    // Test connection
                    boolean success = DatabaseConnectionPool.testConnection();

                    if (success) {
                        // Save config
                        config.saveToFile();
                    }

                    return success;
                } catch (NumberFormatException e) {
                    errorMessage = "Invalid port number";
                    return false;
                } catch (SQLException e) {
                    errorMessage = e.getMessage();
                    return false;
                } catch (Exception e) {
                    errorMessage = "Connection failed: " + e.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    if (success) {
                        statusLabel.setText("Connected successfully!");
                        statusLabel.setForeground(new Color(0, 150, 0));
                        connected = true;

                        // Close dialog after short delay
                        Timer timer = new Timer(500, e -> dispose());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        statusLabel.setText("Connection failed: " + (errorMessage != null ? errorMessage : "Unknown error"));
                        statusLabel.setForeground(Color.RED);
                        connectButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Connection failed: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    connectButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void cancel() {
        connected = false;
        dispose();
    }

    public boolean isConnected() {
        return connected;
    }

    public DatabaseConfig getConfig() {
        return config;
    }
}
