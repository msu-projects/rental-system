package com.istarvin.model;

import java.sql.Timestamp;

/**
 * Language entity representing a film language
 */
public class Language {
    private int languageId;
    private String name;
    private Timestamp lastUpdate;

    public Language() {
    }

    public Language(int languageId, String name) {
        this.languageId = languageId;
        this.name = name;
    }

    // Getters and setters
    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return languageId == language.languageId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(languageId);
    }
}
