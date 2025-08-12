package com.rental.rental_management_api.dto;

import com.rental.rental_management_api.model.RoomType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BuildingDTO {
    private Integer buildingId;
    private String buildingName;
    private String address;
}