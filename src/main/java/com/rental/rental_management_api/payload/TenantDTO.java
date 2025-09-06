package com.rental.rental_management_api.payload;

import com.rental.rental_management_api.model.TenantGender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for a Tenant")
public class TenantDTO {
    @Schema(description = "Unique identifier of the room", example = "1")
    private Integer tenantId;

    @Schema(description = "Flag to indicate tenant is primary of a room", example = "true")
    private Boolean isPrimary;

    @NotBlank(message = "First Name cannot be blank")
    @Schema(description = "First name of a tenant", example = "Christoph")
    private String firstName;

    @NotBlank(message = "Last Name cannot be blank")
    @Schema(description = "Last name of a tenant", example = "Dela Cruz")
    private String lastName;

    @Schema(description = "Middle name of a tenant", example = "Pritchett")
    private String middleName;

    @NotNull(message = "Birth Date cannot be blank")
    @Schema(description = "Date of Birth of a tenant", example = "2000-08-20")
    private LocalDate birthDate;

    @Schema(description = "Age of a tenant", example = "25")
    private Integer age;

    @NotNull
    @Schema(description = "Gender of a tenant", example = "M")
    private TenantGender gender;

    @NotBlank(message = "Contact Number cannot be blank")
    @Pattern(regexp = "^09\\d{2}-\\d{3}-\\d{4}$",
            message = "Contact number must follow the format 09XX-XXX-XXXX"
    )
    @Schema(description = "Contact number of the tenant (Philippine mobile format)",
            example = "0956-325-7269"
    )
    private String contactNumber;

    @Schema(description = "Move-In Date of a tenant", example = "2025-08-20")
    private LocalDate dateMovedIn;

    @Schema(description = "Move-Out Date of a tenant", example = "2026-08-20")
    private LocalDate dateMovedOut;

    @Schema(description = "Room ID of a tenant", example = "1")
    private Integer roomId;
}