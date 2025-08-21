package com.rental.rental_management_api.service;

import com.rental.rental_management_api.entity.Building;
import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import com.rental.rental_management_api.exception.ParentHasChildException;
import com.rental.rental_management_api.exception.ResourceNotFoundException;
import com.rental.rental_management_api.model.RoomStatus;
import com.rental.rental_management_api.repository.BuildingRepository;
import com.rental.rental_management_api.repository.RoomRepository;
import com.rental.rental_management_api.repository.TenantRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BuildingService {
    // TODO: Handle edge cases or caching if performance becomes an issue.

    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    private Building getBuildingOrThrow(Integer buildingId) {
        return buildingRepository.findById(buildingId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Building", buildingId)
                );
    }

    public List<Building> getAllBuildings() {
        return buildingRepository.findAll(Sort.by("buildingName").ascending());
    }

    public Building getBuildingById(Integer buildingId) {
        return getBuildingOrThrow(buildingId);
    }

    public Page<Room> getRoomsByBuildingId(Integer buildingId, Pageable pageable, RoomStatus status) {
        getBuildingOrThrow(buildingId);

        switch (status) {
            case vacant:
                return roomRepository.findByBuildingIDAndStatus(buildingId, true, pageable);
            case occupied:
                return roomRepository.findByBuildingIDAndStatus(buildingId, false, pageable);
            default:
                return roomRepository.findByBuilding_BuildingId(buildingId, pageable);
        }
    }

    public Page<Tenant> getTenantsByBuildingID(Integer buildingId, Pageable pageable) {
        getBuildingOrThrow(buildingId);
        return tenantRepository.findByRoom_Building_BuildingId(buildingId, pageable);
    }

    public Building saveBuilding(Building building) {
        return buildingRepository.save(building);
    }

    public Building updateBuilding(Integer buildingId, Building buildingUpdate) {
        Building building = getBuildingOrThrow(buildingId);
        building.setBuildingName(buildingUpdate.getBuildingName());
        building.setStreet(buildingUpdate.getStreet());
        building.setBarangay(buildingUpdate.getBarangay());
        building.setCity(buildingUpdate.getCity());
        building.setProvince(buildingUpdate.getProvince());

        return buildingRepository.save(building);
    }

    public void deleteBuilding(Integer buildingId) {
        Building building = getBuildingOrThrow(buildingId);

        if (building.getRooms().size() > 0) {
            throw new ParentHasChildException("Building", "Room");
        }

        buildingRepository.delete(building);
    }

    public Room saveRoom(Integer buildingId, Room room) {
        Building building = getBuildingOrThrow(buildingId);

        room.setBuilding(building);
        return roomRepository.save(room);
    }
}