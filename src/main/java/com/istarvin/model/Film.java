package com.istarvin.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

/**
 * Film entity representing a film in the Sakila database
 */
public class Film {
    private int filmId;
    private String title;
    private String description;
    private Integer releaseYear;
    private int languageId;
    private String languageName;
    private Integer rentalDuration;
    private BigDecimal rentalRate;
    private Integer length;
    private BigDecimal replacementCost;
    private Rating rating;
    private Set<SpecialFeature> specialFeatures;
    private Timestamp lastUpdate;

    // From VIEW - additional fields
    private String categoryName;
    private Integer categoryId;
    private int rentalCount;
    private int totalCopies;
    private int rentedCopies;
    private int availableCopies;
    private String leadActorName;
    private BigDecimal totalRevenue;
    private Timestamp lastRentalDate;

    /**
     * Film rating enum
     */
    public enum Rating {
        G, PG, PG_13("PG-13"), R, NC_17("NC-17");

        private final String displayName;

        Rating() {
            this.displayName = this.name().replace("_", "-");
        }

        Rating(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }

        public static Rating fromString(String value) {
            if (value == null) return G;
            for (Rating r : Rating.values()) {
                if (r.displayName.equals(value) || r.name().equals(value)) {
                    return r;
                }
            }
            return G;
        }
    }

    /**
     * Special features enum
     */
    public enum SpecialFeature {
        TRAILERS("Trailers"),
        COMMENTARIES("Commentaries"),
        DELETED_SCENES("Deleted Scenes"),
        BEHIND_THE_SCENES("Behind the Scenes");

        private final String displayName;

        SpecialFeature(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }

        public static SpecialFeature fromString(String value) {
            for (SpecialFeature sf : SpecialFeature.values()) {
                if (sf.displayName.equals(value)) {
                    return sf;
                }
            }
            return null;
        }
    }

    // Constructors
    public Film() {
    }

    public Film(int filmId, String title) {
        this.filmId = filmId;
        this.title = title;
    }

    // Getters and setters
    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public Integer getRentalDuration() {
        return rentalDuration;
    }

    public void setRentalDuration(Integer rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    public BigDecimal getRentalRate() {
        return rentalRate;
    }

    public void setRentalRate(BigDecimal rentalRate) {
        this.rentalRate = rentalRate;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public BigDecimal getReplacementCost() {
        return replacementCost;
    }

    public void setReplacementCost(BigDecimal replacementCost) {
        this.replacementCost = replacementCost;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Set<SpecialFeature> getSpecialFeatures() {
        return specialFeatures;
    }

    public void setSpecialFeatures(Set<SpecialFeature> specialFeatures) {
        this.specialFeatures = specialFeatures;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public int getRentalCount() {
        return rentalCount;
    }

    public void setRentalCount(int rentalCount) {
        this.rentalCount = rentalCount;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getRentedCopies() {
        return rentedCopies;
    }

    public void setRentedCopies(int rentedCopies) {
        this.rentedCopies = rentedCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public String getLeadActorName() {
        return leadActorName;
    }

    public void setLeadActorName(String leadActorName) {
        this.leadActorName = leadActorName;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Timestamp getLastRentalDate() {
        return lastRentalDate;
    }

    public void setLastRentalDate(Timestamp lastRentalDate) {
        this.lastRentalDate = lastRentalDate;
    }

    /**
     * Check if film has copies available for rent
     */
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    /**
     * Get availability status as string
     */
    public String getAvailabilityStatus() {
        if (totalCopies == 0) {
            return "No Inventory";
        } else if (availableCopies == 0) {
            return "All Rented";
        } else if (availableCopies < totalCopies * 0.3) {
            return "Low Availability";
        } else {
            return "Available";
        }
    }

    @Override
    public String toString() {
        return String.format("Film{id=%d, title='%s', category='%s', rating=%s}",
                filmId, title, categoryName, rating);
    }
}
