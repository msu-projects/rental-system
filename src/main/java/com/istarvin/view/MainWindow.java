package com.istarvin.view;

import com.istarvin.controller.FilmController;
import com.istarvin.model.Film;
import com.istarvin.util.AppLogger;
import com.istarvin.util.DatabaseConnectionPool;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window with tabbed interface
 */
public class MainWindow extends JFrame {
    private final FilmController controller;

    private JTabbedPane tabbedPane;
    private DashboardPanel dashboardPanel;
    private FilmBrowserPanel browserPanel;
    private FilmManagementPanel managementPanel;

    private JLabel connectionStatusLabel;
    private JLabel statusLabel;

    private Timer autoRefreshTimer;
    private boolean isDarkTheme = false;

    public MainWindow() {
        this.controller = new FilmController();
        initializeComponents();
        layoutComponents();
        setupMenuBar();
        setupStatusBar();
        setupAutoRefresh();

        setTitle("Sakila Film Rental Management System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        AppLogger.info("Main window initialized");
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();

        // Create panels
        dashboardPanel = new DashboardPanel(controller);
        browserPanel = new FilmBrowserPanel(controller);
        managementPanel = new FilmManagementPanel(controller);

        // Set up edit listener for browser panel
        browserPanel.setEditListener(film -> {
            loadFilmForEditing(film);
        });

        // Add panels to tabbed pane
        tabbedPane.addTab("Dashboard", new ImageIcon(), dashboardPanel, "View statistics and reports");
        tabbedPane.addTab("Browse Films", new ImageIcon(), browserPanel, "Browse and search films");
        tabbedPane.addTab("Manage Films", new ImageIcon(), managementPanel, "Create and edit films");

        // Connection status indicator
        connectionStatusLabel = new JLabel();
        updateConnectionStatus();

        statusLabel = new JLabel("Ready");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        JMenuItem exportItem = new JMenuItem("Export to CSV...");
        exportItem.setMnemonic('E');
        exportItem.addActionListener(e -> exportToCSV());
        fileMenu.add(exportItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        exitItem.addActionListener(e -> exitApplication());
        fileMenu.add(exitItem);

        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');

        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.setMnemonic('R');
        refreshItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        refreshItem.addActionListener(e -> refreshCurrentPanel());
        viewMenu.add(refreshItem);

        JCheckBoxMenuItem autoRefreshItem = new JCheckBoxMenuItem("Auto-Refresh (30s)");
        autoRefreshItem.setSelected(false);
        autoRefreshItem.addActionListener(e -> toggleAutoRefresh(autoRefreshItem.isSelected()));
        viewMenu.add(autoRefreshItem);

        viewMenu.addSeparator();

        JMenuItem themeItem = new JMenuItem("Toggle Dark/Light Theme");
        themeItem.setMnemonic('T');
        themeItem.addActionListener(e -> toggleTheme());
        viewMenu.add(themeItem);

        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic('T');

        JMenuItem connectionItem = new JMenuItem("Database Connection...");
        connectionItem.setMnemonic('D');
        connectionItem.addActionListener(e -> showConnectionDialog());
        toolsMenu.add(connectionItem);

        JMenuItem poolStatsItem = new JMenuItem("Connection Pool Statistics");
        poolStatsItem.addActionListener(e -> showPoolStatistics());
        toolsMenu.add(poolStatsItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('A');
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void setupStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(statusLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(new JLabel("Connection: "));
        rightPanel.add(connectionStatusLabel);

        statusBar.add(leftPanel, BorderLayout.WEST);
        statusBar.add(rightPanel, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);
    }

    private void updateConnectionStatus() {
        if (DatabaseConnectionPool.isInitialized() && DatabaseConnectionPool.testConnection()) {
            connectionStatusLabel.setText("\u2022"); // Bullet point
            connectionStatusLabel.setForeground(new Color(0, 200, 0));
            connectionStatusLabel.setFont(new Font("Arial", Font.BOLD, 20));
            connectionStatusLabel.setToolTipText("Connected");
        } else {
            connectionStatusLabel.setText("\u2022");
            connectionStatusLabel.setForeground(Color.RED);
            connectionStatusLabel.setFont(new Font("Arial", Font.BOLD, 20));
            connectionStatusLabel.setToolTipText("Disconnected");
        }
    }

    private void setupAutoRefresh() {
        autoRefreshTimer = new Timer(30000, e -> {
            AppLogger.info("Auto-refresh triggered");
            refreshCurrentPanel();
        });
    }

    private void toggleAutoRefresh(boolean enable) {
        if (enable) {
            autoRefreshTimer.start();
            statusLabel.setText("Auto-refresh enabled (30s)");
            AppLogger.info("Auto-refresh enabled");
        } else {
            autoRefreshTimer.stop();
            statusLabel.setText("Auto-refresh disabled");
            AppLogger.info("Auto-refresh disabled");
        }
    }

    private void refreshCurrentPanel() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        String tabTitle = tabbedPane.getTitleAt(selectedIndex);

        statusLabel.setText("Refreshing " + tabTitle + "...");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                switch (selectedIndex) {
                    case 0:
                        dashboardPanel.refresh();
                        break;
                    case 1:
                        browserPanel.refresh();
                        break;
                }
                return null;
            }

            @Override
            protected void done() {
                statusLabel.setText("Refreshed at: " + java.time.LocalTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
                updateConnectionStatus();
            }
        };

        worker.execute();
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Films to CSV");
        fileChooser.setSelectedFile(new java.io.File("films_export.csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                String errorMessage = null;

                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        java.util.List<Film> films = controller.getAllFilms();

                        try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                            // Write header
                            writer.println("Film ID,Title,Category,Rating,Length,Rental Rate,Available Copies,Total Copies,Rental Count,Revenue");

                            // Write data
                            for (Film film : films) {
                                writer.printf("%d,\"%s\",\"%s\",%s,%d,%.2f,%d,%d,%d,%.2f%n",
                                        film.getFilmId(),
                                        film.getTitle(),
                                        film.getCategoryName() != null ? film.getCategoryName() : "",
                                        film.getRating(),
                                        film.getLength() != null ? film.getLength() : 0,
                                        film.getRentalRate(),
                                        film.getAvailableCopies(),
                                        film.getTotalCopies(),
                                        film.getRentalCount(),
                                        film.getTotalRevenue());
                            }
                        }
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                        throw e;
                    }
                    return null;
                }

                @Override
                protected void done() {
                    if (errorMessage == null) {
                        FilmController.showSuccess(MainWindow.this, "Films exported successfully to:\n" + file.getAbsolutePath());
                        statusLabel.setText("Export completed");
                    } else {
                        FilmController.showError(MainWindow.this, "Export failed", new Exception(errorMessage));
                        statusLabel.setText("Export failed");
                    }
                }
            };

            statusLabel.setText("Exporting films...");
            worker.execute();
        }
    }

    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;

        try {
            if (isDarkTheme) {
                // Set dark theme
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                UIManager.put("control", new Color(50, 50, 50));
                UIManager.put("info", new Color(50, 50, 50));
                UIManager.put("nimbusBase", new Color(18, 30, 49));
                UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
                UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
                UIManager.put("nimbusFocus", new Color(115, 164, 209));
                UIManager.put("nimbusGreen", new Color(176, 179, 50));
                UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
                UIManager.put("nimbusLightBackground", new Color(70, 70, 70));
                UIManager.put("nimbusOrange", new Color(191, 98, 4));
                UIManager.put("nimbusRed", new Color(169, 46, 34));
                UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
                UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
                UIManager.put("text", new Color(230, 230, 230));
            } else {
                // Set light theme
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }

            SwingUtilities.updateComponentTreeUI(this);
            statusLabel.setText(isDarkTheme ? "Dark theme enabled" : "Light theme enabled");
        } catch (Exception e) {
            FilmController.showError(this, "Failed to change theme", e);
        }
    }

    private void showConnectionDialog() {
        ConnectionDialog dialog = new ConnectionDialog(this);
        dialog.setVisible(true);
        updateConnectionStatus();

        if (dialog.isConnected()) {
            statusLabel.setText("Database reconnected");
            refreshCurrentPanel();
        }
    }

    private void showPoolStatistics() {
        String stats = DatabaseConnectionPool.getPoolStats();
        JOptionPane.showMessageDialog(this, stats, "Connection Pool Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAbout() {
        String about = "Sakila Film Rental Management System\n\n" +
                "Version: 1.0.0\n" +
                "Built with Java Swing\n\n" +
                "Features:\n" +
                "- Film browsing and search\n" +
                "- Film management (Create/Update)\n" +
                "- Real-time dashboard with statistics\n" +
                "- Connection pooling with HikariCP\n" +
                "- CSV export functionality\n" +
                "- Auto-refresh capability\n" +
                "- Dark/Light theme toggle\n\n" +
                "Database: MySQL Sakila\n" +
                "Created with Claude Code";

        JOptionPane.showMessageDialog(this, about, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            AppLogger.info("Application closing");
            DatabaseConnectionPool.close();
            System.exit(0);
        }
    }

    /**
     * Load a film into the management panel
     */
    public void loadFilmForEditing(Film film) {
        managementPanel.loadFilm(film);
        tabbedPane.setSelectedIndex(2); // Switch to management tab
    }

    /**
     * Show window
     */
    public void showWindow() {
        setVisible(true);
    }
}
