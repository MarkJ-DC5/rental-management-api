package com.rental.rental_management_api.exception;

public class PrimaryTenantConstraintException extends RuntimeException {
    public PrimaryTenantConstraintException(String message) {
        super(message);
    }
}