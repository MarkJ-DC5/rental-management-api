package com.rental.rental_management_api.mapper;

import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.payload.RoomDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring") // Makes it injectable in Spring
public interface RoomMapper {

    @Mapping(target = "buildingId", expression = "java(room.getBuilding().getBuildingId())")
    RoomDTO toDto(Room room);

    @Mapping(target = "roomId", ignore = true)
    @Mapping(target = "building", ignore = true)
    @Mapping(target = "tenants", ignore = true)
    Room toEntity(RoomDTO roomDTO);

    List<RoomDTO> toDtoList(List<Room> rooms);

    List<Room> toEntityList(List<RoomDTO> roomDTOS);
}