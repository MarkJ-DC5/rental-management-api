package com.rental.rental_management_api.payload;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MoveOutTenantRequest {
    @NotNull(message = "Date moved out cannot be blank")
    private LocalDate dateMovedOut;
}