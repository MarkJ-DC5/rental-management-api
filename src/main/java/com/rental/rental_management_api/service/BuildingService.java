package com.rental.rental_management_api.service;

import com.rental.rental_management_api.entity.Building;
import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import com.rental.rental_management_api.repository.BuildingRepository;
import com.rental.rental_management_api.repository.RoomRepository;
import com.rental.rental_management_api.repository.TenantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
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
                .orElseThrow(() -> new EntityNotFoundException(
                        "Building ID " + buildingId + " not found"));
    }

    public List<Building> getAllBuildings(){
        return buildingRepository.findAll(Sort.by("buildingName").ascending());
    }

    public Building getBuildingById(Integer buildingId){
        return getBuildingOrThrow(buildingId);
    }

    public List<Room> getRoomsByBuildingId(Integer buildingId, Sort sort){
        getBuildingOrThrow(buildingId);
        return roomRepository.findByBuilding_BuildingId(buildingId, sort);
    }

    public List<Tenant> getTenantsByBuildingID(Integer buildingId, Sort sort){
        getBuildingOrThrow(buildingId);
        return tenantRepository.findByRoom_Building_BuildingId(buildingId, sort);
    }
}