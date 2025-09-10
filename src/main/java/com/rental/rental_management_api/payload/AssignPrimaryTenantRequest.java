package com.rental.rental_management_api.payload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignPrimaryTenantRequest {
    @NotNull(message = "Tenant ID of the new Primary Tenant cannot be blank")
    private Integer newPrimaryTenantID;
}