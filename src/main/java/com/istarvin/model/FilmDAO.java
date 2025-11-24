package com.istarvin.model;

import com.istarvin.util.AppLogger;
import com.istarvin.util.DatabaseConnectionPool;
import com.istarvin.util.SakilaException;

import java.sql.*;
import java.util.*;

/**
 * Data Access Object for Film operations
 * Uses the vw_film_rental_details VIEW and sp_manage_film stored procedure
 */
public class FilmDAO {

    /**
     * Get all films from the VIEW
     */
    public List<Film> getAllFilms() throws SakilaException {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT * FROM vw_film_rental_details ORDER BY title";

        try (Connection conn = DatabaseConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                films.add(extractFilmFromResultSet(rs));
            }

            AppLogger.info("Retrieved " + films.size() + " films from database");
        } catch (SQLException e) {
            AppLogger.error("Error retrieving films", e);
            throw new SakilaException("Failed to retrieve films: " + e.getMessage(), e,
                    SakilaException.ErrorType.DATABASE_QUERY);
        }

        return films;
    }

    /**
     * Search films by title, category, or rating
     */
    public List<Film> searchFilms(String searchTerm, String category, String rating) throws SakilaException {
        List<Film> films = new ArrayList<>();
        StringBuilder sql = getStringBuilder(searchTerm, category, rating);

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchPattern = "%" + searchTerm + "%";
                pstmt.setString(paramIndex++, searchPattern);
                pstmt.setString(paramIndex++, searchPattern);
            }
            if (category != null && !category.isEmpty() && !category.equals("All")) {
                pstmt.setString(paramIndex++, category);
            }
            if (rating != null && !rating.isEmpty() && !rating.equals("All")) {
                pstmt.setString(paramIndex++, rating);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                films.add(extractFilmFromResultSet(rs));
            }

            AppLogger.info("Search returned " + films.size() + " films");
        } catch (SQLException e) {
            AppLogger.error("Error searching films", e);
            throw new SakilaException("Failed to search films: " + e.getMessage(), e,
                    SakilaException.ErrorType.DATABASE_QUERY);
        }

        return films;
    }

    private static StringBuilder getStringBuilder(String searchTerm, String category, String rating) {
        StringBuilder sql = new StringBuilder("SELECT * FROM vw_film_rental_details WHERE 1=1");

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND (title LIKE ? OR description LIKE ?)");
        }
        if (category != null && !category.isEmpty() && !category.equals("All")) {
            sql.append(" AND category_name = ?");
        }
        if (rating != null && !rating.isEmpty() && !rating.equals("All")) {
            sql.append(" AND rating = ?");
        }

        sql.append(" ORDER BY title");
        return sql;
    }

    /**
     * Get a single film by ID
     */
    public Film getFilmById(int filmId) throws SakilaException {
        String sql = "SELECT * FROM vw_film_rental_details WHERE film_id = ?";

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, filmId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractFilmFromResultSet(rs);
            } else {
                throw new SakilaException("Film not found with ID: " + filmId,
                        SakilaException.ErrorType.NOT_FOUND);
            }
        } catch (SQLException e) {
            AppLogger.error("Error retrieving film by ID", e);
            throw new SakilaException("Failed to retrieve film: " + e.getMessage(), e,
                    SakilaException.ErrorType.DATABASE_QUERY);
        }
    }

    /**
     * Save film using stored procedure (handles both INSERT and UPDATE)
     */
    public int saveFilm(Film film) throws SakilaException {
        String sql = "{CALL sp_manage_film(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DatabaseConnectionPool.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            // Input parameters
            cstmt.setInt(1, film.getFilmId()); // 0 for INSERT, existing ID for UPDATE
            cstmt.setString(2, film.getTitle());
            cstmt.setString(3, film.getDescription());

            if (film.getReleaseYear() != null) {
                cstmt.setInt(4, film.getReleaseYear());
            } else {
                cstmt.setNull(4, Types.INTEGER);
            }

            cstmt.setInt(5, film.getLanguageId());
            cstmt.setInt(6, film.getRentalDuration() != null ? film.getRentalDuration() : 3);
            cstmt.setBigDecimal(7, film.getRentalRate());

            if (film.getLength() != null) {
                cstmt.setInt(8, film.getLength());
            } else {
                cstmt.setNull(8, Types.INTEGER);
            }

            cstmt.setBigDecimal(9, film.getReplacementCost());
            cstmt.setString(10, film.getRating() != null ? film.getRating().toString() : "G");

            // Convert special features set to comma-separated string
            String specialFeatures = null;
            if (film.getSpecialFeatures() != null && !film.getSpecialFeatures().isEmpty()) {
                StringJoiner joiner = new StringJoiner(",");
                for (Film.SpecialFeature sf : film.getSpecialFeatures()) {
                    joiner.add(sf.toString());
                }
                specialFeatures = joiner.toString();
            }
            cstmt.setString(11, specialFeatures);

            if (film.getCategoryId() != null) {
                cstmt.setInt(12, film.getCategoryId());
            } else {
                cstmt.setNull(12, Types.INTEGER);
            }

            // Output parameters
            cstmt.registerOutParameter(13, Types.INTEGER); // p_result_film_id
            cstmt.registerOutParameter(14, Types.VARCHAR); // p_result_message

            cstmt.execute();

            int resultFilmId = cstmt.getInt(13);
            String resultMessage = cstmt.getString(14);

            if (resultFilmId == -1) {
                throw new SakilaException(resultMessage, SakilaException.ErrorType.DATABASE_QUERY);
            }

            AppLogger.info("Film saved: " + resultMessage);
            return resultFilmId;

        } catch (SQLException e) {
            AppLogger.error("Error saving film", e);
            throw new SakilaException("Failed to save film: " + e.getMessage(), e,
                    SakilaException.ErrorType.DATABASE_QUERY);
        }
    }

    /**
     * Extract Film object from ResultSet
     */
    private Film extractFilmFromResultSet(ResultSet rs) throws SQLException {
        Film film = new Film();

        film.setFilmId(rs.getInt("film_id"));
        film.setTitle(rs.getString("title"));
        film.setDescription(rs.getString("description"));

        int year = rs.getInt("release_year");
        film.setReleaseYear(rs.wasNull() ? null : year);

        film.setRentalRate(rs.getBigDecimal("rental_rate"));
        film.setRating(Film.Rating.fromString(rs.getString("rating")));

        int length = rs.getInt("length");
        film.setLength(rs.wasNull() ? null : length);

        film.setReplacementCost(rs.getBigDecimal("replacement_cost"));

        // Parse special features
        String specialFeaturesStr = rs.getString("special_features");
        if (specialFeaturesStr != null && !specialFeaturesStr.isEmpty()) {
            Set<Film.SpecialFeature> features = new HashSet<>();
            for (String feature : specialFeaturesStr.split(",")) {
                Film.SpecialFeature sf = Film.SpecialFeature.fromString(feature.trim());
                if (sf != null) {
                    features.add(sf);
                }
            }
            film.setSpecialFeatures(features);
        }

        film.setCategoryName(rs.getString("category_name"));

        int categoryId = rs.getInt("category_id");
        film.setCategoryId(rs.wasNull() ? null : categoryId);

        film.setLanguageName(rs.getString("language_name"));
        film.setLanguageId(rs.getInt("language_id"));

        // VIEW-specific fields
        film.setRentalCount(rs.getInt("rental_count"));
        film.setTotalCopies(rs.getInt("total_copies"));
        film.setRentedCopies(rs.getInt("rented_copies"));
        film.setAvailableCopies(rs.getInt("available_copies"));
        film.setLeadActorName(rs.getString("lead_actor_name"));
        film.setTotalRevenue(rs.getBigDecimal("total_revenue"));
        film.setLastRentalDate(rs.getTimestamp("last_rental_date"));

        return film;
    }

    /**
     * Get distinct categories for filtering
     */
    public List<String> getDistinctCategories() throws SakilaException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category_name FROM vw_film_rental_details WHERE category_name IS NOT NULL ORDER BY category_name";

        try (Connection conn = DatabaseConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            AppLogger.error("Error retrieving categories", e);
            throw new SakilaException("Failed to retrieve categories: " + e.getMessage(), e,
                    SakilaException.ErrorType.DATABASE_QUERY);
        }

        return categories;
    }

    /**
     * Get distinct ratings for filtering
     */
    public List<String> getDistinctRatings() throws SakilaException {
        List<String> ratings = new ArrayList<>();
        String sql = "SELECT DISTINCT rating FROM vw_film_rental_details WHERE rating IS NOT NULL ORDER BY rating";

        try (Connection conn = DatabaseConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ratings.add(rs.getString("rating"));
            }
        } catch (SQLException e) {
            AppLogger.error("Error retrieving ratings", e);
            throw new SakilaException("Failed to retrieve ratings: " + e.getMessage(), e,
                    SakilaException.ErrorType.DATABASE_QUERY);
        }

        return ratings;
    }
}
