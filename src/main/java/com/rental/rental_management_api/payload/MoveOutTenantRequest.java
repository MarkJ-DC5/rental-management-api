package com.rental.rental_management_api.payload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoveOutTenantRequest {
    @NotNull(message = "Date moved out cannot be blank")
    private LocalDate dateMovedOut;
}