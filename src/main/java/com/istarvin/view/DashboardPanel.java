package com.istarvin.view;

import com.istarvin.controller.FilmController;
import com.istarvin.model.DashboardStats;
import com.istarvin.util.SakilaException;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * Dashboard Panel showing real-time statistics and charts
 */
public class DashboardPanel extends JPanel {
    private final FilmController controller;

    // Stat labels
    private JLabel totalFilmsLabel;
    private JLabel totalRentalsLabel;
    private JLabel activeRentalsLabel;
    private JLabel totalRevenueLabel;

    // Inventory progress bar
    private JProgressBar inventoryBar;
    private JLabel inventoryLabel;

    // Top films panel
    private JTextArea topFilmsArea;

    // Category chart
    private CategoryChartPanel chartPanel;

    // Refresh button
    private JButton refreshButton;
    private JLabel statusLabel;

    public DashboardPanel(FilmController controller) {
        this.controller = controller;
        initializeComponents();
        layoutComponents();
        loadDashboardData();
    }

    private void initializeComponents() {
        // Stat labels
        totalFilmsLabel = createStatLabel("0");
        totalRentalsLabel = createStatLabel("0");
        activeRentalsLabel = createStatLabel("0");
        totalRevenueLabel = createStatLabel("$0.00");

        // Inventory bar
        inventoryBar = new JProgressBar(0, 100);
        inventoryBar.setStringPainted(true);
        inventoryBar.setPreferredSize(new Dimension(300, 30));
        inventoryLabel = new JLabel("Inventory Utilization: 0%");

        // Top films area
        topFilmsArea = new JTextArea(10, 30);
        topFilmsArea.setEditable(false);
        topFilmsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Category chart
        chartPanel = new CategoryChartPanel();
        chartPanel.setPreferredSize(new Dimension(400, 300));

        // Refresh button
        refreshButton = new JButton("Refresh Dashboard");
        refreshButton.addActionListener(e -> loadDashboardData());

        // Status label
        statusLabel = new JLabel("Ready");
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with statistics cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 10));

        statsPanel.add(createStatCard("Total Films", totalFilmsLabel, new Color(100, 150, 255)));
        statsPanel.add(createStatCard("Total Rentals", totalRentalsLabel, new Color(100, 200, 150)));
        statsPanel.add(createStatCard("Active Rentals", activeRentalsLabel, new Color(255, 180, 100)));
        statsPanel.add(createStatCard("Total Revenue", totalRevenueLabel, new Color(200, 100, 200)));

        // Middle panel with inventory and top films
        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Inventory panel
        JPanel inventoryPanel = new JPanel(new BorderLayout(5, 5));
        inventoryPanel.setBorder(BorderFactory.createTitledBorder("Inventory Status"));
        inventoryPanel.add(inventoryLabel, BorderLayout.NORTH);
        inventoryPanel.add(inventoryBar, BorderLayout.CENTER);

        JPanel inventoryWrapper = new JPanel(new BorderLayout());
        inventoryWrapper.add(inventoryPanel, BorderLayout.NORTH);

        // Top films panel
        JPanel topFilmsPanel = new JPanel(new BorderLayout());
        topFilmsPanel.setBorder(BorderFactory.createTitledBorder("Top 5 Rented Films"));
        JScrollPane topFilmsScroll = new JScrollPane(topFilmsArea);
        topFilmsPanel.add(topFilmsScroll, BorderLayout.CENTER);

        middlePanel.add(inventoryWrapper);
        middlePanel.add(topFilmsPanel);

        // Bottom panel with chart
        JPanel chartWrapperPanel = new JPanel(new BorderLayout());
        chartWrapperPanel.setBorder(BorderFactory.createTitledBorder("Films by Category"));
        chartWrapperPanel.add(chartPanel, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.add(refreshButton);
        controlPanel.add(statusLabel);

        // Add all to main panel
        add(statsPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(chartWrapperPanel, BorderLayout.SOUTH);
        add(controlPanel, BorderLayout.PAGE_END);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createLineBorder(color, 3));
        card.setBackground(color.brighter());

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void loadDashboardData() {
        SwingWorker<DashboardStats, Void> worker = new SwingWorker<DashboardStats, Void>() {
            @Override
            protected DashboardStats doInBackground() throws Exception {
                statusLabel.setText("Loading dashboard data...");
                return controller.getDashboardStats();
            }

            @Override
            protected void done() {
                try {
                    DashboardStats stats = get();
                    updateDashboard(stats);
                    statusLabel.setText("Dashboard updated at: " + java.time.LocalTime.now().format(
                            java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
                } catch (Exception e) {
                    FilmController.showError(DashboardPanel.this, "Failed to load dashboard", e);
                    statusLabel.setText("Error loading dashboard");
                }
            }
        };
        worker.execute();
    }

    private void updateDashboard(DashboardStats stats) {
        DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

        // Update stat labels
        totalFilmsLabel.setText(String.valueOf(stats.getTotalFilms()));
        totalRentalsLabel.setText(String.valueOf(stats.getTotalRentals()));
        activeRentalsLabel.setText(String.valueOf(stats.getActiveRentals()));
        totalRevenueLabel.setText(currencyFormat.format(stats.getTotalRevenue()));

        // Update inventory bar
        double utilization = stats.getInventoryUtilization();
        inventoryBar.setValue((int) utilization);
        inventoryBar.setString(String.format("%.1f%%", utilization));
        inventoryLabel.setText(String.format("Inventory Utilization: %d rented / %d available",
                stats.getRentedInventory(), stats.getAvailableInventory()));

        // Set bar color based on utilization
        if (utilization < 50) {
            inventoryBar.setForeground(new Color(100, 200, 100));
        } else if (utilization < 80) {
            inventoryBar.setForeground(new Color(255, 200, 100));
        } else {
            inventoryBar.setForeground(new Color(255, 100, 100));
        }

        // Update top films
        StringBuilder topFilmsText = new StringBuilder();
        topFilmsText.append(String.format("%-4s %-30s %8s %10s\n", "Rank", "Title", "Rentals", "Revenue"));
        topFilmsText.append("-".repeat(60)).append("\n");

        int rank = 1;
        for (DashboardStats.FilmStats filmStats : stats.getTopRentedFilms()) {
            String title = filmStats.getTitle();
            if (title.length() > 28) {
                title = title.substring(0, 25) + "...";
            }
            topFilmsText.append(String.format("%-4d %-30s %8d %10s\n",
                    rank++, title, filmStats.getRentalCount(),
                    currencyFormat.format(filmStats.getRevenue())));
        }

        topFilmsArea.setText(topFilmsText.toString());

        // Update category chart
        chartPanel.setData(stats.getCategoryDistribution());
    }

    /**
     * Custom panel for displaying category distribution chart
     */
    private static class CategoryChartPanel extends JPanel {
        private Map<String, Integer> data;
        private int maxValue = 0;

        public void setData(Map<String, Integer> data) {
            this.data = data;
            if (data != null && !data.isEmpty()) {
                this.maxValue = data.values().stream().max(Integer::compareTo).orElse(0);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (data == null || data.isEmpty()) {
                g.drawString("No data available", getWidth() / 2 - 50, getHeight() / 2);
                return;
            }

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int barHeight = 30;
            int spacing = 10;
            int leftMargin = 120;
            int topMargin = 20;

            int numCategories = data.size();
            int chartHeight = numCategories * (barHeight + spacing);

            int y = topMargin;

            // Color palette for bars
            Color[] colors = {
                    new Color(100, 150, 255), new Color(100, 200, 150), new Color(255, 180, 100),
                    new Color(200, 100, 200), new Color(255, 150, 150), new Color(150, 255, 150),
                    new Color(150, 150, 255), new Color(255, 200, 150), new Color(200, 150, 255),
                    new Color(150, 255, 255), new Color(255, 255, 150), new Color(255, 150, 255),
                    new Color(150, 200, 200), new Color(200, 200, 150), new Color(200, 150, 200),
                    new Color(150, 150, 200)
            };

            int colorIndex = 0;

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                String category = entry.getKey();
                int count = entry.getValue();

                // Calculate bar width
                int barWidth = maxValue > 0 ? (int) (((double) count / maxValue) * (width - leftMargin - 50)) : 0;

                // Draw category name
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.drawString(category, 10, y + barHeight / 2 + 5);

                // Draw bar
                Color barColor = colors[colorIndex % colors.length];
                g2d.setColor(barColor);
                g2d.fillRect(leftMargin, y, barWidth, barHeight);

                // Draw border
                g2d.setColor(barColor.darker());
                g2d.drawRect(leftMargin, y, barWidth, barHeight);

                // Draw count
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.drawString(String.valueOf(count), leftMargin + barWidth + 5, y + barHeight / 2 + 5);

                y += barHeight + spacing;
                colorIndex++;
            }
        }

        @Override
        public Dimension getPreferredSize() {
            if (data != null && !data.isEmpty()) {
                int height = data.size() * 40 + 40;
                return new Dimension(400, height);
            }
            return new Dimension(400, 300);
        }
    }

    /**
     * Refresh the dashboard
     */
    public void refresh() {
        loadDashboardData();
    }
}
