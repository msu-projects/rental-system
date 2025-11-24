package com.istarvin.model;

import com.istarvin.util.AppLogger;
import com.istarvin.util.DatabaseConnectionPool;
import com.istarvin.util.SakilaException;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Dashboard statistics
 */
public class DashboardDAO {

    /**
     * Get complete dashboard statistics
     */
    public DashboardStats getDashboardStats() throws SakilaException {
        DashboardStats stats = new DashboardStats();

        try (Connection conn = DatabaseConnectionPool.getConnection()) {
            // Get total films
            stats.setTotalFilms(getTotalFilms(conn));

            // Get total categories
            stats.setTotalCategories(getTotalCategories(conn));

            // Get rental statistics
            stats.setTotalRentals(getTotalRentals(conn));
            stats.setActiveRentals(getActiveRentals(conn));

            // Get revenue
            stats.setTotalRevenue(getTotalRevenue(conn));

            // Get inventory statistics
            int[] inventory = getInventoryStats(conn);
            stats.setAvailableInventory(inventory[0]);
            stats.setRentedInventory(inventory[1]);

            // Get top rented films
            stats.setTopRentedFilms(getTopRentedFilms(conn, 5));

            // Get category distribution
            stats.setCategoryDistribution(new CategoryDAO().getCategoryDistribution());

            AppLogger.info("Dashboard statistics retrieved successfully");
        } catch (SQLException e) {
            AppLogger.error("Error retrieving dashboard statistics", e);
            throw new SakilaException("Failed to retrieve dashboard statistics: " + e.getMessage(), e,
                    SakilaException.ErrorType.DATABASE_QUERY);
        }

        return stats;
    }

    /**
     * Get total number of films
     */
    private int getTotalFilms(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM film";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Get total number of categories
     */
    private int getTotalCategories(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM category";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Get total number of rentals
     */
    private int getTotalRentals(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rental";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Get number of active rentals (not returned yet)
     */
    private int getActiveRentals(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM rental WHERE return_date IS NULL";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Get total revenue
     */
    private BigDecimal getTotalRevenue(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM payment";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Get inventory statistics [available, rented]
     */
    private int[] getInventoryStats(Connection conn) throws SQLException {
        int[] stats = new int[2];

        String sql = "SELECT " +
                "COUNT(CASE WHEN r.return_date IS NOT NULL OR r.rental_id IS NULL THEN 1 END) as available, " +
                "COUNT(CASE WHEN r.return_date IS NULL AND r.rental_id IS NOT NULL THEN 1 END) as rented " +
                "FROM inventory i " +
                "LEFT JOIN rental r ON i.inventory_id = r.inventory_id " +
                "AND r.rental_id = (SELECT MAX(r2.rental_id) FROM rental r2 WHERE r2.inventory_id = i.inventory_id)";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                stats[0] = rs.getInt("available");
                stats[1] = rs.getInt("rented");
            }
        }

        return stats;
    }

    /**
     * Get top rented films
     */
    private List<DashboardStats.FilmStats> getTopRentedFilms(Connection conn, int limit) throws SQLException {
        List<DashboardStats.FilmStats> topFilms = new ArrayList<>();

        String sql = "SELECT f.film_id, f.title, COUNT(r.rental_id) as rental_count, " +
                "COALESCE(SUM(p.amount), 0) as revenue " +
                "FROM film f " +
                "INNER JOIN inventory i ON f.film_id = i.film_id " +
                "INNER JOIN rental r ON i.inventory_id = r.inventory_id " +
                "LEFT JOIN payment p ON r.rental_id = p.rental_id " +
                "GROUP BY f.film_id, f.title " +
                "ORDER BY rental_count DESC " +
                "LIMIT ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                topFilms.add(new DashboardStats.FilmStats(
                        rs.getInt("film_id"),
                        rs.getString("title"),
                        rs.getInt("rental_count"),
                        rs.getBigDecimal("revenue")
                ));
            }
        }

        return topFilms;
    }
}
