package com.rental.rental_management_api.dto;

import com.rental.rental_management_api.model.RoomType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BuildingDTO {
    private Integer buildingId;

    @NotBlank(message = "Building Name cannot be blank")
    private String buildingName;

    @Pattern(regexp = "^([^,]+,){3}[^,]+$", message = "Address must have exactly 4 parts separated by commas")
    private String address;
}