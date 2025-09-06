package com.rental.rental_management_api.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for a Building")
public class BuildingDTO {
    @Schema(description = "Unique identifier of the building", example = "1")
    private Integer buildingId;

    @NotBlank(message = "Building Name cannot be blank")
    @Schema(description = "Name of the building", example = "Green Apartment")
    private String buildingName;

    @NotBlank(message = "Building Address cannot be blank")
    @Pattern(regexp = "^([^,]+,){3}[^,]+$", message = "Address must have exactly 4 parts separated by commas")
    @Schema(description = "Address of the building", example = "street, barangay, city, province")
    private String address;
}