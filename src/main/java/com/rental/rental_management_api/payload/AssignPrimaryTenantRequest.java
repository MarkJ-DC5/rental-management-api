package com.rental.rental_management_api.payload;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AssignPrimaryTenantRequest {
    private int newPrimaryTenantID;
}