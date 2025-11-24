package com.istarvin.controller;

import com.istarvin.model.*;
import com.istarvin.util.AppLogger;
import com.istarvin.util.SakilaException;
import com.istarvin.util.ValidationUtil;

import javax.swing.*;
import java.util.List;

/**
 * Controller for Film operations
 * Coordinates between the model (DAOs) and view (GUI)
 */
public class FilmController {
    private final FilmDAO filmDAO;
    private final CategoryDAO categoryDAO;
    private final LanguageDAO languageDAO;
    private final DashboardDAO dashboardDAO;

    public FilmController() {
        this.filmDAO = new FilmDAO();
        this.categoryDAO = new CategoryDAO();
        this.languageDAO = new LanguageDAO();
        this.dashboardDAO = new DashboardDAO();
    }

    /**
     * Get all films
     */
    public List<Film> getAllFilms() throws SakilaException {
        return filmDAO.getAllFilms();
    }

    /**
     * Search films with filters
     */
    public List<Film> searchFilms(String searchTerm, String category, String rating) throws SakilaException {
        return filmDAO.searchFilms(searchTerm, category, rating);
    }

    /**
     * Get film by ID
     */
    public Film getFilmById(int filmId) throws SakilaException {
        return filmDAO.getFilmById(filmId);
    }

    /**
     * Save film with validation
     */
    public int saveFilm(Film film) throws SakilaException {
        // Validate film data
        ValidationUtil.ValidationResult titleResult = ValidationUtil.validateTitle(film.getTitle());
        if (!titleResult.isValid()) {
            throw new SakilaException(titleResult.getMessage(), SakilaException.ErrorType.VALIDATION);
        }

        if (film.getReleaseYear() != null) {
            ValidationUtil.ValidationResult yearResult = ValidationUtil.validateYear(film.getReleaseYear());
            if (!yearResult.isValid()) {
                throw new SakilaException(yearResult.getMessage(), SakilaException.ErrorType.VALIDATION);
            }
        }

        ValidationUtil.ValidationResult rateResult = ValidationUtil.validateRentalRate(film.getRentalRate());
        if (!rateResult.isValid()) {
            throw new SakilaException(rateResult.getMessage(), SakilaException.ErrorType.VALIDATION);
        }

        if (film.getLength() != null) {
            ValidationUtil.ValidationResult lengthResult = ValidationUtil.validateLength(film.getLength());
            if (!lengthResult.isValid()) {
                throw new SakilaException(lengthResult.getMessage(), SakilaException.ErrorType.VALIDATION);
            }
        }

        if (film.getRentalDuration() != null) {
            ValidationUtil.ValidationResult durationResult = ValidationUtil.validateRentalDuration(film.getRentalDuration());
            if (!durationResult.isValid()) {
                throw new SakilaException(durationResult.getMessage(), SakilaException.ErrorType.VALIDATION);
            }
        }

        // Save film
        return filmDAO.saveFilm(film);
    }

    /**
     * Get all categories
     */
    public List<Category> getAllCategories() throws SakilaException {
        return categoryDAO.getAllCategories();
    }

    /**
     * Get all languages
     */
    public List<Language> getAllLanguages() throws SakilaException {
        return languageDAO.getAllLanguages();
    }

    /**
     * Get distinct categories for filter
     */
    public List<String> getDistinctCategories() throws SakilaException {
        return filmDAO.getDistinctCategories();
    }

    /**
     * Get distinct ratings for filter
     */
    public List<String> getDistinctRatings() throws SakilaException {
        return filmDAO.getDistinctRatings();
    }

    /**
     * Get dashboard statistics
     */
    public DashboardStats getDashboardStats() throws SakilaException {
        return dashboardDAO.getDashboardStats();
    }

    /**
     * Show error message dialog
     */
    public static void showError(java.awt.Component parent, String message, Exception e) {
        AppLogger.error(message, e);
        String displayMessage = message;
        if (e != null) {
            displayMessage += "\n\nDetails: " + e.getMessage();
        }
        JOptionPane.showMessageDialog(parent, displayMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show success message dialog
     */
    public static void showSuccess(java.awt.Component parent, String message) {
        AppLogger.info(message);
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show warning message dialog
     */
    public static void showWarning(java.awt.Component parent, String message) {
        AppLogger.warning(message);
        JOptionPane.showMessageDialog(parent, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Confirm action dialog
     */
    public static boolean confirmAction(java.awt.Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}
