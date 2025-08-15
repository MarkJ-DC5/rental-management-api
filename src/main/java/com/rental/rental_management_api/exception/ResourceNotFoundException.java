package com.rental.rental_management_api.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entityName, Object id) {
        super(entityName + " with ID " + id + " not found");
    }
}