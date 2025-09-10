package com.rental.rental_management_api.security.auth.payload;

import com.rental.rental_management_api.security.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private UserRole role;
    private String firstName;
    private String lastName;
}