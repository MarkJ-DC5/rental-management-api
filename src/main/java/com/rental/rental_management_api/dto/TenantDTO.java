package com.rental.rental_management_api.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TenantDTO {
    private Integer tenantId;
    private boolean isPrimary;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate birthDate;
    private Integer age;
    private String contactNumber;
    private LocalDate dateMovedIn;
    private LocalDate dateMovedOut;
    private Integer roomId;
}