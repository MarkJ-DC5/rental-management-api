package com.rental.rental_management_api.exception;

public class BusinessConstraintException extends RuntimeException {
    public BusinessConstraintException(String message) {
        super(message);
    }
}