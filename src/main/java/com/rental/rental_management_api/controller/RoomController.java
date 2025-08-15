package com.rental.rental_management_api.controller;

import com.rental.rental_management_api.entity.Building;
import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import com.rental.rental_management_api.mapper.BuildingMapper;
import com.rental.rental_management_api.mapper.RoomMapper;
import com.rental.rental_management_api.mapper.TenantMapper;
import com.rental.rental_management_api.payload.BuildingDTO;
import com.rental.rental_management_api.payload.RoomDTO;
import com.rental.rental_management_api.payload.TenantDTO;
import com.rental.rental_management_api.repository.BuildingRepository;
import com.rental.rental_management_api.repository.RoomRepository;
import com.rental.rental_management_api.repository.TenantRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

//@RestController
@AllArgsConstructor
public class RoomController {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    @GetMapping(path = "/buildings")
    public List<BuildingDTO> getAllBuildings(
            @SortDefault(sort = "buildingId", direction = Sort.Direction.ASC) Sort sort) {
        return buildingMapper.toDtoList(buildingRepository.findAll(sort));
    }

    @GetMapping(path = "/buildings/{buildingId}")
    public BuildingDTO getBuildingById(@PathVariable Integer buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Building ID: " + buildingId + " not found"
                ));

        return buildingMapper.toDto(building);
    }

//    @GetMapping(path = "/buildings/{buildingId}/rooms")
//    // buildings/1/rooms?sort=roomName,desc&sort=rent,asc
//    public List<RoomDTO> getRoomsByBuildingId(
//            @PathVariable Integer buildingId,
//            @SortDefault(sort = "roomName", direction = Sort.Direction.ASC) Sort sort){
//        return roomMapper.toDtoList(roomRepository.findByBuilding_BuildingId(buildingId, sort));
//    }

    @GetMapping(path = "rooms/{roomId}")
    public RoomDTO getRoomById(@PathVariable Integer roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room ID: " + roomId + " not found"
                ));

        return roomMapper.toDto(room);
    }

    @GetMapping(path = "rooms/{roomId}/tenants")
    public List<TenantDTO> getTenantsByRoomId(
            @PathVariable Integer roomId,
            @RequestParam(required = false, defaultValue = "false") boolean primaryOnly,
            @SortDefault(sort = "isPrimary", direction = Sort.Direction.DESC) Sort sort) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room ID: " + roomId + " not found"
                ));

        List<Tenant> tenants = room.getTenants();
        if (primaryOnly) {
            tenants = tenants.stream().filter(Tenant::getIsPrimary).toList();
        } else {
            tenants = tenants.stream()
                    .sorted(Comparator.comparing(Tenant::getIsPrimary).reversed()
                            .thenComparing(Tenant::getLastName)
                            .thenComparing(Tenant::getFirstName)
                            .thenComparing(Tenant::getBirthDate).reversed()
                    )
                    .toList();
        }

        return tenantMapper.toDtoList(tenants);
    }
}