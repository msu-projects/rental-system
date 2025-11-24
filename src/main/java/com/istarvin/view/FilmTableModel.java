package com.istarvin.view;

import com.istarvin.model.Film;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom table model for Film data
 */
public class FilmTableModel extends AbstractTableModel {
    private static final String[] COLUMN_NAMES = {
            "ID", "Title", "Category", "Rating", "Length", "Rental Rate",
            "Available", "Total Copies", "Rentals", "Revenue", "Status"
    };

    private static final Class<?>[] COLUMN_CLASSES = {
            Integer.class, String.class, String.class, String.class, Integer.class,
            BigDecimal.class, Integer.class, Integer.class, Integer.class, BigDecimal.class, String.class
    };

    private List<Film> films;

    public FilmTableModel() {
        this.films = new ArrayList<>();
    }

    public FilmTableModel(List<Film> films) {
        this.films = films != null ? films : new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return films.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return COLUMN_CLASSES[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= films.size()) {
            return null;
        }

        Film film = films.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return film.getFilmId();
            case 1:
                return film.getTitle();
            case 2:
                return film.getCategoryName();
            case 3:
                return film.getRating() != null ? film.getRating().toString() : "";
            case 4:
                return film.getLength();
            case 5:
                return film.getRentalRate();
            case 6:
                return film.getAvailableCopies();
            case 7:
                return film.getTotalCopies();
            case 8:
                return film.getRentalCount();
            case 9:
                return film.getTotalRevenue();
            case 10:
                return film.getAvailabilityStatus();
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Get film at specific row
     */
    public Film getFilmAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < films.size()) {
            return films.get(rowIndex);
        }
        return null;
    }

    /**
     * Update data
     */
    public void setFilms(List<Film> films) {
        this.films = films != null ? films : new ArrayList<>();
        fireTableDataChanged();
    }

    /**
     * Add film
     */
    public void addFilm(Film film) {
        films.add(film);
        fireTableRowsInserted(films.size() - 1, films.size() - 1);
    }

    /**
     * Update film
     */
    public void updateFilm(int rowIndex, Film film) {
        if (rowIndex >= 0 && rowIndex < films.size()) {
            films.set(rowIndex, film);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }

    /**
     * Remove film
     */
    public void removeFilm(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < films.size()) {
            films.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    /**
     * Clear all data
     */
    public void clear() {
        films.clear();
        fireTableDataChanged();
    }

    /**
     * Get all films
     */
    public List<Film> getFilms() {
        return new ArrayList<>(films);
    }
}
