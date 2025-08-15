package com.rental.rental_management_api.payload;

import com.rental.rental_management_api.model.RoomType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RoomDTO {
    private Integer roomId;
    private String roomName;
    private RoomType roomType;
    private Integer rent;
}