package com.rental.rental_management_api.exception;

public class ImmutableFieldException extends RuntimeException {
    public ImmutableFieldException(String fieldName, String entityName) {
        super("Cannot change " + fieldName + " field of " + entityName);
    }
}