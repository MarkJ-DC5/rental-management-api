package com.rental.rental_management_api.exception;

public class InvalidPrimaryTenantException extends RuntimeException {

    // Constructor with tenantID only
    public InvalidPrimaryTenantException(Integer tenantID) {
        this(tenantID, ""); // calls the second constructor
    }

    // Constructor with tenantID + details
    public InvalidPrimaryTenantException(Integer tenantID, String details) {
        super(("Tenant ID of " + tenantID + " is invalid. " + details).strip());
    }
}