package com.istarvin.view;

import com.istarvin.controller.FilmController;
import com.istarvin.model.Category;
import com.istarvin.model.Film;
import com.istarvin.model.Language;
import com.istarvin.util.SakilaException;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Film Management Panel for creating and editing films
 */
public class FilmManagementPanel extends JPanel {
    private final FilmController controller;

    // Form fields
    private JTextField filmIdField;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JSpinner releaseYearSpinner;
    private JComboBox<Language> languageCombo;
    private JSpinner rentalDurationSpinner;
    private JTextField rentalRateField;
    private JSpinner lengthSpinner;
    private JTextField replacementCostField;
    private JComboBox<Film.Rating> ratingCombo;
    private JComboBox<Category> categoryCombo;

    // Special features checkboxes
    private JCheckBox trailersCheckBox;
    private JCheckBox commentariesCheckBox;
    private JCheckBox deletedScenesCheckBox;
    private JCheckBox behindScenesCheckBox;

    // Buttons
    private JButton saveButton;
    private JButton clearButton;
    private JButton newButton;

    // Status label
    private JLabel statusLabel;

    // Current film (null for new film)
    private Film currentFilm;

    public FilmManagementPanel(FilmController controller) {
        this.controller = controller;
        initializeComponents();
        layoutComponents();
        loadComboBoxData();
        clearForm();
    }

    private void initializeComponents() {
        // Film ID (read-only)
        filmIdField = new JTextField(10);
        filmIdField.setEditable(false);
        filmIdField.setBackground(Color.LIGHT_GRAY);

        // Title
        titleField = new JTextField(30);

        // Description
        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        // Release year
        int currentYear = java.time.Year.now().getValue();
        releaseYearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, 1888, currentYear + 2, 1));

        // Language
        languageCombo = new JComboBox<>();

        // Rental duration
        rentalDurationSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 30, 1));

        // Rental rate
        rentalRateField = new JTextField(10);
        rentalRateField.setText("4.99");

        // Length
        lengthSpinner = new JSpinner(new SpinnerNumberModel(120, 1, 1000, 1));

        // Replacement cost
        replacementCostField = new JTextField(10);
        replacementCostField.setText("19.99");

        // Rating
        ratingCombo = new JComboBox<>(Film.Rating.values());

        // Category
        categoryCombo = new JComboBox<>();

        // Special features
        trailersCheckBox = new JCheckBox("Trailers");
        commentariesCheckBox = new JCheckBox("Commentaries");
        deletedScenesCheckBox = new JCheckBox("Deleted Scenes");
        behindScenesCheckBox = new JCheckBox("Behind the Scenes");

        // Buttons
        saveButton = new JButton("Save Film");
        clearButton = new JButton("Clear");
        newButton = new JButton("New Film");

        // Status label
        statusLabel = new JLabel("Ready");

        // Event handlers
        saveButton.addActionListener(e -> saveFilm());
        clearButton.addActionListener(e -> clearForm());
        newButton.addActionListener(e -> newFilm());
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Film Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Film ID
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Film ID:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(filmIdField, gbc);

        // Title
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("*Title:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(titleField, gbc);
        gbc.gridwidth = 1;

        // Description
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll, gbc);
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Release Year and Language (same row)
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Release Year:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(releaseYearSpinner, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("  *Language:"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(languageCombo, gbc);

        // Rental Duration and Rate
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Rental Duration (days):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(rentalDurationSpinner, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("  *Rental Rate ($):"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(rentalRateField, gbc);

        // Length and Replacement Cost
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Length (minutes):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(lengthSpinner, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("  Replacement Cost ($):"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(replacementCostField, gbc);

        // Rating and Category
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Rating:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(ratingCombo, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("  Category:"), gbc);
        gbc.gridx = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(categoryCombo, gbc);

        // Special Features
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Special Features:"), gbc);

        JPanel featuresPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        featuresPanel.add(trailersCheckBox);
        featuresPanel.add(commentariesCheckBox);
        featuresPanel.add(deletedScenesCheckBox);
        featuresPanel.add(behindScenesCheckBox);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        formPanel.add(featuresPanel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(newButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);

        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);

        // Add to main panel
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusPanel, BorderLayout.NORTH);
    }

    private void loadComboBoxData() {
        try {
            // Load languages
            List<Language> languages = controller.getAllLanguages();
            for (Language language : languages) {
                languageCombo.addItem(language);
            }

            // Load categories
            List<Category> categories = controller.getAllCategories();
            for (Category category : categories) {
                categoryCombo.addItem(category);
            }
        } catch (SakilaException e) {
            FilmController.showError(this, "Failed to load form data", e);
        }
    }

    private void saveFilm() {
        try {
            // Validate required fields
            if (titleField.getText().trim().isEmpty()) {
                FilmController.showWarning(this, "Title is required");
                titleField.requestFocus();
                return;
            }

            if (languageCombo.getSelectedItem() == null) {
                FilmController.showWarning(this, "Language is required");
                return;
            }

            // Create or update film
            Film film = currentFilm != null ? currentFilm : new Film();

            film.setFilmId(currentFilm != null ? currentFilm.getFilmId() : 0);
            film.setTitle(titleField.getText().trim());
            film.setDescription(descriptionArea.getText().trim());
            film.setReleaseYear((Integer) releaseYearSpinner.getValue());

            Language selectedLanguage = (Language) languageCombo.getSelectedItem();
            film.setLanguageId(selectedLanguage.getLanguageId());

            film.setRentalDuration((Integer) rentalDurationSpinner.getValue());

            try {
                film.setRentalRate(new BigDecimal(rentalRateField.getText()));
            } catch (NumberFormatException e) {
                FilmController.showWarning(this, "Invalid rental rate format");
                rentalRateField.requestFocus();
                return;
            }

            film.setLength((Integer) lengthSpinner.getValue());

            try {
                film.setReplacementCost(new BigDecimal(replacementCostField.getText()));
            } catch (NumberFormatException e) {
                FilmController.showWarning(this, "Invalid replacement cost format");
                replacementCostField.requestFocus();
                return;
            }

            film.setRating((Film.Rating) ratingCombo.getSelectedItem());

            // Category
            Category selectedCategory = (Category) categoryCombo.getSelectedItem();
            if (selectedCategory != null) {
                film.setCategoryId(selectedCategory.getCategoryId());
            }

            // Special features
            Set<Film.SpecialFeature> features = new HashSet<>();
            if (trailersCheckBox.isSelected()) features.add(Film.SpecialFeature.TRAILERS);
            if (commentariesCheckBox.isSelected()) features.add(Film.SpecialFeature.COMMENTARIES);
            if (deletedScenesCheckBox.isSelected()) features.add(Film.SpecialFeature.DELETED_SCENES);
            if (behindScenesCheckBox.isSelected()) features.add(Film.SpecialFeature.BEHIND_THE_SCENES);
            film.setSpecialFeatures(features);

            // Save to database
            int resultId = controller.saveFilm(film);

            String message = currentFilm != null ?
                    "Film updated successfully!" :
                    "Film created successfully with ID: " + resultId;

            FilmController.showSuccess(this, message);
            statusLabel.setText("Film saved successfully");

            // Load the saved film
            currentFilm = controller.getFilmById(resultId);
            loadFilm(currentFilm);

        } catch (SakilaException e) {
            FilmController.showError(this, "Failed to save film", e);
            statusLabel.setText("Save failed");
        }
    }

    private void clearForm() {
        currentFilm = null;
        filmIdField.setText("New Film");
        titleField.setText("");
        descriptionArea.setText("");
        releaseYearSpinner.setValue(java.time.Year.now().getValue());
        if (languageCombo.getItemCount() > 0) {
            languageCombo.setSelectedIndex(0);
        }
        rentalDurationSpinner.setValue(3);
        rentalRateField.setText("4.99");
        lengthSpinner.setValue(120);
        replacementCostField.setText("19.99");
        ratingCombo.setSelectedItem(Film.Rating.G);
        if (categoryCombo.getItemCount() > 0) {
            categoryCombo.setSelectedIndex(0);
        }
        trailersCheckBox.setSelected(false);
        commentariesCheckBox.setSelected(false);
        deletedScenesCheckBox.setSelected(false);
        behindScenesCheckBox.setSelected(false);
        statusLabel.setText("Ready for new film");
    }

    private void newFilm() {
        clearForm();
    }

    /**
     * Load a film into the form for editing
     */
    public void loadFilm(Film film) {
        currentFilm = film;
        filmIdField.setText(String.valueOf(film.getFilmId()));
        titleField.setText(film.getTitle());
        descriptionArea.setText(film.getDescription());

        if (film.getReleaseYear() != null) {
            releaseYearSpinner.setValue(film.getReleaseYear());
        }

        // Select language
        for (int i = 0; i < languageCombo.getItemCount(); i++) {
            if (languageCombo.getItemAt(i).getLanguageId() == film.getLanguageId()) {
                languageCombo.setSelectedIndex(i);
                break;
            }
        }

        if (film.getRentalDuration() != null) {
            rentalDurationSpinner.setValue(film.getRentalDuration());
        }

        rentalRateField.setText(film.getRentalRate().toString());

        if (film.getLength() != null) {
            lengthSpinner.setValue(film.getLength());
        }

        replacementCostField.setText(film.getReplacementCost().toString());

        if (film.getRating() != null) {
            ratingCombo.setSelectedItem(film.getRating());
        }

        // Select category
        if (film.getCategoryId() != null) {
            for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                if (categoryCombo.getItemAt(i).getCategoryId() == film.getCategoryId()) {
                    categoryCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Special features
        Set<Film.SpecialFeature> features = film.getSpecialFeatures();
        if (features != null) {
            trailersCheckBox.setSelected(features.contains(Film.SpecialFeature.TRAILERS));
            commentariesCheckBox.setSelected(features.contains(Film.SpecialFeature.COMMENTARIES));
            deletedScenesCheckBox.setSelected(features.contains(Film.SpecialFeature.DELETED_SCENES));
            behindScenesCheckBox.setSelected(features.contains(Film.SpecialFeature.BEHIND_THE_SCENES));
        }

        statusLabel.setText("Loaded film: " + film.getTitle());
    }
}
