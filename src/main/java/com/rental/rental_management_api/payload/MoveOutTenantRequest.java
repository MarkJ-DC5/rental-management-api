package com.rental.rental_management_api.payload;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MoveOutTenantRequest {
    private LocalDate dateMovedOut;
}