package com.rental.rental_management_api.exception;

public class InvalidCredentialsExeception extends RuntimeException {

    // Constructor with tenantID only
    public InvalidCredentialsExeception() {
        this("Invalid username or password"); // calls the second constructor
    }

    // Constructor with tenantID + details
    public InvalidCredentialsExeception(String message) {
        super(message);
    }
}