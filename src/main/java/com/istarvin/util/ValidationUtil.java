package com.istarvin.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtil {

    private static final Pattern TITLE_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\-:',!.&]+$");
    private static final int MIN_TITLE_LENGTH = 1;
    private static final int MAX_TITLE_LENGTH = 255;

    /**
     * Validate film title
     */
    public static ValidationResult validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return new ValidationResult(false, "Title is required");
        }

        if (title.length() < MIN_TITLE_LENGTH || title.length() > MAX_TITLE_LENGTH) {
            return new ValidationResult(false,
                    String.format("Title must be between %d and %d characters", MIN_TITLE_LENGTH, MAX_TITLE_LENGTH));
        }

        if (!TITLE_PATTERN.matcher(title).matches()) {
            return new ValidationResult(false, "Title contains invalid characters");
        }

        return new ValidationResult(true, "Valid");
    }

    /**
     * Validate release year
     */
    public static ValidationResult validateYear(Integer year) {
        if (year == null) {
            return new ValidationResult(false, "Release year is required");
        }

        int currentYear = java.time.Year.now().getValue();
        if (year < 1888 || year > currentYear + 2) {
            return new ValidationResult(false,
                    String.format("Year must be between 1888 and %d", currentYear + 2));
        }

        return new ValidationResult(true, "Valid");
    }

    /**
     * Validate rental rate
     */
    public static ValidationResult validateRentalRate(BigDecimal rate) {
        if (rate == null) {
            return new ValidationResult(false, "Rental rate is required");
        }

        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            return new ValidationResult(false, "Rental rate must be greater than 0");
        }

        if (rate.compareTo(new BigDecimal("99.99")) > 0) {
            return new ValidationResult(false, "Rental rate cannot exceed 99.99");
        }

        return new ValidationResult(true, "Valid");
    }

    /**
     * Validate film length (in minutes)
     */
    public static ValidationResult validateLength(Integer length) {
        if (length == null || length <= 0) {
            return new ValidationResult(false, "Film length must be greater than 0");
        }

        if (length > 1000) {
            return new ValidationResult(false, "Film length seems unrealistic (max 1000 minutes)");
        }

        return new ValidationResult(true, "Valid");
    }

    /**
     * Validate rental duration (in days)
     */
    public static ValidationResult validateRentalDuration(Integer days) {
        if (days == null || days <= 0) {
            return new ValidationResult(false, "Rental duration must be at least 1 day");
        }

        if (days > 30) {
            return new ValidationResult(false, "Rental duration cannot exceed 30 days");
        }

        return new ValidationResult(true, "Valid");
    }

    /**
     * Result class for validation
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
