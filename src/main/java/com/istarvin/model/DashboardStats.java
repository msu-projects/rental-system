package com.istarvin.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Dashboard statistics model
 */
public class DashboardStats {
    private int totalFilms;
    private int totalCategories;
    private int totalRentals;
    private int activeRentals;
    private BigDecimal totalRevenue;
    private int availableInventory;
    private int rentedInventory;
    private List<FilmStats> topRentedFilms;
    private Map<String, Integer> categoryDistribution;

    /**
     * Inner class for film statistics
     */
    public static class FilmStats {
        private int filmId;
        private String title;
        private int rentalCount;
        private BigDecimal revenue;

        public FilmStats(int filmId, String title, int rentalCount, BigDecimal revenue) {
            this.filmId = filmId;
            this.title = title;
            this.rentalCount = rentalCount;
            this.revenue = revenue;
        }

        public int getFilmId() {
            return filmId;
        }

        public String getTitle() {
            return title;
        }

        public int getRentalCount() {
            return rentalCount;
        }

        public BigDecimal getRevenue() {
            return revenue;
        }
    }

    // Getters and setters
    public int getTotalFilms() {
        return totalFilms;
    }

    public void setTotalFilms(int totalFilms) {
        this.totalFilms = totalFilms;
    }

    public int getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(int totalCategories) {
        this.totalCategories = totalCategories;
    }

    public int getTotalRentals() {
        return totalRentals;
    }

    public void setTotalRentals(int totalRentals) {
        this.totalRentals = totalRentals;
    }

    public int getActiveRentals() {
        return activeRentals;
    }

    public void setActiveRentals(int activeRentals) {
        this.activeRentals = activeRentals;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getAvailableInventory() {
        return availableInventory;
    }

    public void setAvailableInventory(int availableInventory) {
        this.availableInventory = availableInventory;
    }

    public int getRentedInventory() {
        return rentedInventory;
    }

    public void setRentedInventory(int rentedInventory) {
        this.rentedInventory = rentedInventory;
    }

    public List<FilmStats> getTopRentedFilms() {
        return topRentedFilms;
    }

    public void setTopRentedFilms(List<FilmStats> topRentedFilms) {
        this.topRentedFilms = topRentedFilms;
    }

    public Map<String, Integer> getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(Map<String, Integer> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }

    /**
     * Calculate inventory utilization percentage
     */
    public double getInventoryUtilization() {
        int total = availableInventory + rentedInventory;
        if (total == 0) return 0;
        return (double) rentedInventory / total * 100;
    }
}
