package com.rental.rental_management_api.payload;

import com.rental.rental_management_api.model.RoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RoomDTO {
    @Schema(description = "Unique identifier of the room", example = "1")
    private Integer roomId;

    @NotBlank(message = "Room Name cannot be blank")
    @Schema(description = "Name of the room", example = "Room 1")
    private String roomName;

    @NotBlank(message = "Room Type cannot be blank")
    @Schema(description = "Type of the riin", example = "Residential")
    private RoomType roomType;

    @NotBlank(message = "Rent cannot be blank")
    @Schema(description = "Rent of the room", example = "3000")
    private Integer rent;
}