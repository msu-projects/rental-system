package com.istarvin.util;

/**
 * Custom exception class for Sakila application
 */
public class SakilaException extends Exception {

    private final ErrorType errorType;

    public enum ErrorType {
        DATABASE_CONNECTION,
        DATABASE_QUERY,
        VALIDATION,
        NOT_FOUND,
        BUSINESS_LOGIC,
        SYSTEM
    }

    public SakilaException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public SakilaException(String message, Throwable cause, ErrorType errorType) {
        super(message, cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", errorType, getMessage());
    }
}
