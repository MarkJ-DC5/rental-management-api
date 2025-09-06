package com.rental.rental_management_api.payload;

import com.rental.rental_management_api.model.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for a Room")
public class RoomDTO {
    @Schema(description = "Unique identifier of the room", example = "1")
    private Integer roomId;

    @NotBlank(message = "Room Name cannot be blank")
    @Schema(description = "Name of the room", example = "Room 1")
    private String roomName;

    @NotNull(message = "Room Type cannot be blank")
    @Schema(description = "Type of the room", example = "Residential")
    private RoomType roomType;

    @Min(value = 1, message = "Rent must be greater than 0")
    @Schema(description = "Rent of the room", example = "3000")
    private Integer rent;

    @Schema(description = "Building ID of a room", example = "1")
    private Integer buildingId;
}