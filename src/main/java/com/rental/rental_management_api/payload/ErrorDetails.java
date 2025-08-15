package com.rental.rental_management_api.payload;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ErrorDetails {
    private LocalDateTime timestamp;
    private String message;
    private Object details;
}