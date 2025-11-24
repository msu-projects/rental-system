package com.istarvin.model;

import com.istarvin.util.AppLogger;
import com.istarvin.util.DatabaseConnectionPool;
import com.istarvin.util.SakilaException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Language operations
 */
public class LanguageDAO {

    /**
     * Get all languages
     */
    public List<Language> getAllLanguages() throws SakilaException {
        List<Language> languages = new ArrayList<>();
        String sql = "SELECT language_id, name, last_update FROM language ORDER BY name";

        try (Connection conn = DatabaseConnectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Language language = new Language();
                language.setLanguageId(rs.getInt("language_id"));
                language.setName(rs.getString("name"));
                language.setLastUpdate(rs.getTimestamp("last_update"));
                languages.add(language);
            }

            AppLogger.info("Retrieved " + languages.size() + " languages");
        } catch (SQLException e) {
            AppLogger.error("Error retrieving languages", e);
            throw new SakilaException("Failed to retrieve languages: " + e.getMessage(), e,
                    SakilaException.ErrorType.DATABASE_QUERY);
        }

        return languages;
    }

    /**
     * Get language by ID
     */
    public Language getLanguageById(int languageId) throws SakilaException {
        String sql = "SELECT language_id, name, last_update FROM language WHERE language_id = ?";

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, languageId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Language language = new Language();
                language.setLanguageId(rs.getInt("language_id"));
                language.setName(rs.getString("name"));
                language.setLastUpdate(rs.getTimestamp("last_update"));
                return language;
            } else {
                throw new SakilaException("Language not found with ID: " + languageId,
                        SakilaException.ErrorType.NOT_FOUND);
            }
        } catch (SQLException e) {
            AppLogger.error("Error retrieving language", e);
            throw new SakilaException("Failed to retrieve language: " + e.getMessage(), e,
                    SakilaException.ErrorType.DATABASE_QUERY);
        }
    }
}
