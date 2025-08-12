package com.rental.rental_management_api.mapper;

import com.rental.rental_management_api.dto.RoomDTO;
import com.rental.rental_management_api.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring") // Makes it injectable in Spring
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    RoomDTO toDto(Room room);

    Room toEntity(RoomDTO roomDTO);

    List<RoomDTO> toDtoList(List<Room> rooms);
    List<Room> toEntityList(List<RoomDTO> roomDTOS);
}