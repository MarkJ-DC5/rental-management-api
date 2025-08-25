package com.rental.rental_management_api.service;

import com.rental.rental_management_api.entity.Building;
import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.exception.ParentHasChildException;
import com.rental.rental_management_api.exception.ResourceNotFoundException;
import com.rental.rental_management_api.mapper.BuildingMapper;
import com.rental.rental_management_api.mapper.PageMapper;
import com.rental.rental_management_api.mapper.RoomMapper;
import com.rental.rental_management_api.mapper.TenantMapper;
import com.rental.rental_management_api.payload.RoomDTO;
import com.rental.rental_management_api.repository.BuildingRepository;
import com.rental.rental_management_api.repository.RoomRepository;
import com.rental.rental_management_api.repository.TenantRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class RoomService {
    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final TenantMapper tenantMapper;
    private final PageMapper pageMapper;

    private final BuildingService buildingService;

    protected Room getRoomOrThrow(Integer roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Room", roomId)
                );
    }

    public RoomDTO getRoomById(Integer roomId) {
        return roomMapper.toDto(getRoomOrThrow(roomId));
    }

    public RoomDTO saveRoom(Integer buildingId, RoomDTO roomDto) {
        Building building = buildingService.getBuildingOrThrow(buildingId);

        Room room = roomMapper.toEntity(roomDto);
        room.setRoomId(null);
        room.setBuilding(building);

        return roomMapper.toDto(roomRepository.save(room));
    }

    public RoomDTO updateRoom(Integer roomId, RoomDTO roomDtoUpdate) {
        Room room = getRoomOrThrow(roomId);

        room.setRoomName(roomDtoUpdate.getRoomName());
        room.setRoomType(roomDtoUpdate.getRoomType());
        room.setRent(roomDtoUpdate.getRent());

        return roomMapper.toDto(room);
    }

    public void deleteRoom(Integer roomId) {
        Room room = getRoomOrThrow(roomId);

        // TODO: Handle case of inactive tenants
        if (room.getTenants().size() > 0) {
            throw new ParentHasChildException("Room", "tenant");
        }

        roomRepository.delete(room);
    }
}