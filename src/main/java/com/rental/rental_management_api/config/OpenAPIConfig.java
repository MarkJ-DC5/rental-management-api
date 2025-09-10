package com.rental.rental_management_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Mark Jayson"
//                        email = "markjaysondc5@gmail.com"
                ),
                title = "Rental Management System REST API",
                description = "REST API for managing apartment buildings, tenants, and payments.",
                version = "0.0"

        ),
        servers = {
                @Server(
                        description = "Local Env",
                        url = "http://localhost:8080"
                )
        }
)
public class OpenAPIConfig {
}