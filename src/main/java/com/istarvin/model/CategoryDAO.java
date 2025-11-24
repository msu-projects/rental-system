package com.istarvin.model;

import com.istarvin.util.AppLogger;
import com.istarvin.util.DatabaseConnectionPool;
import com.istarvin.util.SakilaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Category operations
 */
public class CategoryDAO {

    /**
     * Get all categories
     */
    public List<Category> getAllCategories() throws SakilaException {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT category_id, name, last_update FROM category ORDER BY name";

        try (Connection conn = DatabaseConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("category_id"));
                category.setName(rs.getString("name"));
                category.setLastUpdate(rs.getTimestamp("last_update"));
                categories.add(category);
            }

            AppLogger.info("Retrieved " + categories.size() + " categories");
        } catch (SQLException e) {
            AppLogger.error("Error retrieving categories", e);
            throw new SakilaException("Failed to retrieve categories: " + e.getMessage(), e,
                    SakilaException.ErrorType.DATABASE_QUERY);
        }

        return categories;
    }

    /**
     * Get category by ID
     */
    public Category getCategoryById(int categoryId) throws SakilaException {
        String sql = "SELECT category_id, name, last_update FROM category WHERE category_id = ?";

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("category_id"));
                category.setName(rs.getString("name"));
                category.setLastUpdate(rs.getTimestamp("last_update"));
                return category;
            } else {
                throw new SakilaException("Category not found with ID: " + categoryId,
                        SakilaException.ErrorType.NOT_FOUND);
            }
        } catch (SQLException e) {
            AppLogger.error("Error retrieving category", e);
            throw new SakilaException("Failed to retrieve category: " + e.getMessage(), e,
                    SakilaException.ErrorType.DATABASE_QUERY);
        }
    }

    /**
     * Get category distribution (for dashboard)
     */
    public java.util.Map<String, Integer> getCategoryDistribution() throws SakilaException {
        java.util.Map<String, Integer> distribution = new java.util.HashMap<>();
        String sql = "SELECT c.name, COUNT(*) as film_count " +
                "FROM category c " +
                "INNER JOIN film_category fc ON c.category_id = fc.category_id " +
                "GROUP BY c.category_id, c.name " +
                "ORDER BY film_count DESC";

        try (Connection conn = DatabaseConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                distribution.put(rs.getString("name"), rs.getInt("film_count"));
            }

            AppLogger.info("Retrieved category distribution");
        } catch (SQLException e) {
            AppLogger.error("Error retrieving category distribution", e);
            throw new SakilaException("Failed to retrieve category distribution: " + e.getMessage(), e,
                    SakilaException.ErrorType.DATABASE_QUERY);
        }

        return distribution;
    }
}
