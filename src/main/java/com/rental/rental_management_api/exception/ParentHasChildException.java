package com.rental.rental_management_api.exception;

public class ParentHasChildException extends RuntimeException {
    public ParentHasChildException(String parentEntity, String childEntity) {
        super(parentEntity + " cannot be deleted because it still has associated " + childEntity + ".");
    }
}