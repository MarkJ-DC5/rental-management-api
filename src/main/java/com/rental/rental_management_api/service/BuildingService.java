package com.rental.rental_management_api.service;

import com.rental.rental_management_api.entity.Building;
import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import com.rental.rental_management_api.exception.ResourceNotFoundException;
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
    /*
    TODO
    Add method-level JavaDocs for clarity if this grows.
    You might want to add pagination for large data sets later (especially tenants).
    Handle edge cases or caching if performance becomes an issue.
     */
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

    public Page<Room> getRoomsByBuildingId(Integer buildingId, Pageable pageable) {
        getBuildingOrThrow(buildingId);
        return roomRepository.findByBuilding_BuildingId(buildingId, pageable);
    }

    public Page<Tenant> getTenantsByBuildingID(Integer buildingId, Pageable pageable) {
        getBuildingOrThrow(buildingId);
        return tenantRepository.findByRoom_Building_BuildingId(buildingId, pageable);
    }

    public Building saveBuilding(Building building) {
        return buildingRepository.save(building);
    }

    public Building updateBuilding(Integer buildingId, Building building) {
        getBuildingOrThrow(buildingId);
        building.setBuildingId(buildingId);
        return buildingRepository.save(building);
    }

    public void deleteBuilding(Integer buildingId) {
        Building building = getBuildingOrThrow(buildingId);
        buildingRepository.delete(building);
    }
}