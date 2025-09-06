package com.rental.rental_management_api.security.auth;

import com.rental.rental_management_api.exception.InvalidCredentialsExeception;
import com.rental.rental_management_api.exception.RegistrationException;
import com.rental.rental_management_api.exception.ResourceNotFoundException;
import com.rental.rental_management_api.security.auth.payload.AuthenticationRequest;
import com.rental.rental_management_api.security.auth.payload.AuthenticationResponse;
import com.rental.rental_management_api.security.auth.payload.RegisterRequest;
import com.rental.rental_management_api.security.config.JwtService;
import com.rental.rental_management_api.security.user.User;
import com.rental.rental_management_api.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        String username = request.getUsername();

        if (repository.existsByUsername(username)) {
            throw new RegistrationException("Username " + username + " already exist");
        }

        log.debug("Creating User Profile for \'" + username +"\"...");
        User user = User.builder()
                .userId(null)
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        log.debug("Generating token...");
        String jwtToken = jwtService.generateToken(user);
        AuthenticationResponse response = AuthenticationResponse.builder().token(jwtToken).build();

        repository.save(user);

        log.info("User \"" + username + "\" have been saved and token was generated");
        return response;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String username = request.getUsername();
        log.debug("Authenticating \"" + username + "\"...");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(), request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsExeception();
        }

        User user = repository.findByUsername(request.getUsername()).get();
        String jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}