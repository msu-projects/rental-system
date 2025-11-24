package com.istarvin.view;

import com.istarvin.controller.FilmController;
import com.istarvin.model.Film;
import com.istarvin.util.SakilaException;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Film Browser Panel with search and filter capabilities
 */
public class FilmBrowserPanel extends JPanel {
    private final FilmController controller;
    private JTable filmTable;
    private FilmTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JComboBox<String> ratingFilter;
    private JButton searchButton;
    private JButton resetButton;
    private JButton refreshButton;
    private JButton editButton;
    private JLabel statusLabel;

    // Listener for edit film action
    private FilmEditListener editListener;

    public FilmBrowserPanel(FilmController controller) {
        this.controller = controller;
        initializeComponents();
        layoutComponents();
        setupTableRenderer();
        loadData();
    }

    /**
     * Interface for film edit callback
     */
    public interface FilmEditListener {
        void onEditFilm(Film film);
    }

    /**
     * Set the edit listener
     */
    public void setEditListener(FilmEditListener listener) {
        this.editListener = listener;
    }

    private void initializeComponents() {
        // Search components
        searchField = new JTextField(20);
        searchField.setToolTipText("Search by title or description");

        categoryFilter = new JComboBox<>();
        categoryFilter.addItem("All");
        categoryFilter.setToolTipText("Filter by category");

        ratingFilter = new JComboBox<>();
        ratingFilter.addItem("All");
        ratingFilter.setToolTipText("Filter by rating");

        searchButton = new JButton("Search");
        searchButton.setToolTipText("Search films");

        resetButton = new JButton("Reset");
        resetButton.setToolTipText("Reset filters");

        refreshButton = new JButton("Refresh");
        refreshButton.setToolTipText("Refresh data");

        editButton = new JButton("Edit Selected Film");
        editButton.setToolTipText("Edit the selected film");

        // Table
        tableModel = new FilmTableModel();
        filmTable = new JTable(tableModel);
        filmTable.setFillsViewportHeight(true);
        filmTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        filmTable.setAutoCreateRowSorter(true);

        TableRowSorter<FilmTableModel> sorter = new TableRowSorter<>(tableModel);
        filmTable.setRowSorter(sorter);

        // Status label
        statusLabel = new JLabel("Ready");

        // Load filter options
        loadFilterOptions();

        // Event handlers
        searchButton.addActionListener(_ -> performSearch());
        resetButton.addActionListener(_ -> resetFilters());
        refreshButton.addActionListener(_ -> loadData());
        editButton.addActionListener(_ -> editSelectedFilm());

        // Enter key in search field
        searchField.addActionListener(_ -> performSearch());

        // Double-click to view details
        filmTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewFilmDetails();
                }
            }
        });
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Category:"));
        searchPanel.add(categoryFilter);
        searchPanel.add(new JLabel("Rating:"));
        searchPanel.add(ratingFilter);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);
        searchPanel.add(refreshButton);
        searchPanel.add(editButton);

        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(filmTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Films"));

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);

        // Add to main panel
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private void setupTableRenderer() {
        // Custom renderer for status column with color coding
        filmTable.getColumnModel().getColumn(10).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String status = (String) value;
                    switch (status) {
                        case "Available" -> c.setBackground(new Color(200, 255, 200)); // Light green
                        case "Low Availability" -> c.setBackground(new Color(255, 255, 200)); // Light yellow
                        case "All Rented" -> c.setBackground(new Color(255, 200, 200)); // Light red
                        case null, default -> c.setBackground(new Color(220, 220, 220)); // Light gray
                    }
                }

                return c;
            }
        });

        // Set column widths
        filmTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        filmTable.getColumnModel().getColumn(1).setPreferredWidth(200);  // Title
        filmTable.getColumnModel().getColumn(2).setPreferredWidth(100);  // Category
        filmTable.getColumnModel().getColumn(3).setPreferredWidth(70);   // Rating
        filmTable.getColumnModel().getColumn(4).setPreferredWidth(70);   // Length
        filmTable.getColumnModel().getColumn(5).setPreferredWidth(90);   // Rental Rate
        filmTable.getColumnModel().getColumn(6).setPreferredWidth(80);   // Available
        filmTable.getColumnModel().getColumn(7).setPreferredWidth(80);   // Total Copies
        filmTable.getColumnModel().getColumn(8).setPreferredWidth(70);   // Rentals
        filmTable.getColumnModel().getColumn(9).setPreferredWidth(90);   // Revenue
        filmTable.getColumnModel().getColumn(10).setPreferredWidth(120); // Status
    }

    private void loadFilterOptions() {
        try {
            // Load categories
            List<String> categories = controller.getDistinctCategories();
            for (String category : categories) {
                categoryFilter.addItem(category);
            }

            // Load ratings
            List<String> ratings = controller.getDistinctRatings();
            for (String rating : ratings) {
                ratingFilter.addItem(rating);
            }
        } catch (SakilaException e) {
            FilmController.showError(this, "Failed to load filter options", e);
        }
    }

    private void loadData() {
        SwingWorker<List<Film>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Film> doInBackground() throws Exception {
                statusLabel.setText("Loading films...");
                return controller.getAllFilms();
            }

            @Override
            protected void done() {
                try {
                    List<Film> films = get();
                    tableModel.setFilms(films);
                    statusLabel.setText(String.format("Loaded %d films", films.size()));
                } catch (Exception e) {
                    FilmController.showError(FilmBrowserPanel.this, "Failed to load films", e);
                    statusLabel.setText("Error loading films");
                }
            }
        };
        worker.execute();
    }

    private void performSearch() {
        SwingWorker<List<Film>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Film> doInBackground() throws Exception {
                statusLabel.setText("Searching...");
                String searchTerm = searchField.getText().trim();
                String category = (String) categoryFilter.getSelectedItem();
                String rating = (String) ratingFilter.getSelectedItem();
                return controller.searchFilms(searchTerm, category, rating);
            }

            @Override
            protected void done() {
                try {
                    List<Film> films = get();
                    tableModel.setFilms(films);
                    statusLabel.setText(String.format("Found %d films", films.size()));
                } catch (Exception e) {
                    FilmController.showError(FilmBrowserPanel.this, "Search failed", e);
                    statusLabel.setText("Search error");
                }
            }
        };
        worker.execute();
    }

    private void resetFilters() {
        searchField.setText("");
        categoryFilter.setSelectedIndex(0);
        ratingFilter.setSelectedIndex(0);
        loadData();
    }

    /**
     * Edit the selected film
     */
    private void editSelectedFilm() {
        int selectedRow = filmTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = filmTable.convertRowIndexToModel(selectedRow);
            Film film = tableModel.getFilmAt(modelRow);
            if (film != null) {
                // Call the listener to edit the film
                if (editListener != null) {
                    editListener.onEditFilm(film);
                } else {
                    FilmController.showWarning(this, "Edit functionality not configured");
                }
            }
        } else {
            FilmController.showWarning(this, "Please select a film to edit");
        }
    }

    private void viewFilmDetails() {
        int selectedRow = filmTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = filmTable.convertRowIndexToModel(selectedRow);
            Film film = tableModel.getFilmAt(modelRow);
            if (film != null) {
                showFilmDetailsDialog(film);
            }
        }
    }

    private void showFilmDetailsDialog(Film film) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Film Details", true);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        detailsPanel.add(new JLabel("Title:"));
        detailsPanel.add(new JLabel(film.getTitle()));

        detailsPanel.add(new JLabel("Category:"));
        detailsPanel.add(new JLabel(film.getCategoryName()));

        detailsPanel.add(new JLabel("Rating:"));
        detailsPanel.add(new JLabel(film.getRating() != null ? film.getRating().toString() : "N/A"));

        detailsPanel.add(new JLabel("Release Year:"));
        detailsPanel.add(new JLabel(film.getReleaseYear() != null ? film.getReleaseYear().toString() : "N/A"));

        detailsPanel.add(new JLabel("Length:"));
        detailsPanel.add(new JLabel(film.getLength() != null ? film.getLength() + " minutes" : "N/A"));

        detailsPanel.add(new JLabel("Rental Rate:"));
        detailsPanel.add(new JLabel("$" + film.getRentalRate()));

        detailsPanel.add(new JLabel("Language:"));
        detailsPanel.add(new JLabel(film.getLanguageName()));

        detailsPanel.add(new JLabel("Lead Actor:"));
        detailsPanel.add(new JLabel(film.getLeadActorName() != null ? film.getLeadActorName() : "N/A"));

        detailsPanel.add(new JLabel("Total Rentals:"));
        detailsPanel.add(new JLabel(String.valueOf(film.getRentalCount())));

        detailsPanel.add(new JLabel("Total Revenue:"));
        detailsPanel.add(new JLabel("$" + film.getTotalRevenue()));

        detailsPanel.add(new JLabel("Available Copies:"));
        detailsPanel.add(new JLabel(film.getAvailableCopies() + " / " + film.getTotalCopies()));

        detailsPanel.add(new JLabel("Status:"));
        detailsPanel.add(new JLabel(film.getAvailabilityStatus()));

        // Description in text area
        JTextArea descriptionArea = new JTextArea(film.getDescription());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBorder(BorderFactory.createTitledBorder("Description"));
        descriptionArea.setRows(4);

        JScrollPane descScroll = new JScrollPane(descriptionArea);

        JButton editFilmButton = new JButton("Edit Film");
        editFilmButton.addActionListener(_ -> {
            dialog.dispose();
            if (editListener != null) {
                editListener.onEditFilm(film);
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(_ -> dialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(editFilmButton);
        buttonPanel.add(closeButton);

        dialog.add(detailsPanel, BorderLayout.CENTER);
        dialog.add(descScroll, BorderLayout.NORTH);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Get selected film
     */
    public Film getSelectedFilm() {
        int selectedRow = filmTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = filmTable.convertRowIndexToModel(selectedRow);
            return tableModel.getFilmAt(modelRow);
        }
        return null;
    }

    /**
     * Refresh the table
     */
    public void refresh() {
        loadData();
    }
}
