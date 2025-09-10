package com.rental.rental_management_api.service;

import com.rental.rental_management_api.entity.Building;
import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import com.rental.rental_management_api.exception.ParentHasChildException;
import com.rental.rental_management_api.exception.ResourceNotFoundException;
import com.rental.rental_management_api.mapper.BuildingMapper;
import com.rental.rental_management_api.mapper.PageMapper;
import com.rental.rental_management_api.mapper.RoomMapper;
import com.rental.rental_management_api.mapper.TenantMapper;
import com.rental.rental_management_api.model.RoomStatus;
import com.rental.rental_management_api.model.TenantStatus;
import com.rental.rental_management_api.payload.BuildingDTO;
import com.rental.rental_management_api.payload.PageResponse;
import com.rental.rental_management_api.payload.RoomDTO;
import com.rental.rental_management_api.payload.TenantDTO;
import com.rental.rental_management_api.repository.BuildingRepository;
import com.rental.rental_management_api.repository.RoomRepository;
import com.rental.rental_management_api.repository.TenantRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BuildingService {
    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final TenantMapper tenantMapper;
    private final PageMapper pageMapper;

    protected Building getBuildingOrThrow(Integer buildingId) {
        return buildingRepository.findById(buildingId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Building", buildingId)
                );
    }

    public List<BuildingDTO> getAllBuildings() {
        List<Building> buildings = buildingRepository.findAll(Sort.by("buildingName").ascending());
        return buildingMapper.toDtoList(buildings);
    }

    public BuildingDTO getBuildingById(Integer buildingId) {
        return buildingMapper.toDto(getBuildingOrThrow(buildingId));
    }

    public BuildingDTO saveBuilding(BuildingDTO buildingDto) {
        Building building = buildingMapper.toEntity(buildingDto);

        if (building.getBuildingId() != null) {
            log.warn("Building ID was specified in Building DTO request for saving a new building. " +
                    "Specified Building ID will be disregarded");
            building.setBuildingId(null);
        }

        return buildingMapper.toDto(buildingRepository.save(building));
    }

    public BuildingDTO updateBuilding(Integer buildingId, BuildingDTO buildingDtoUpdate) {
        Building building = getBuildingOrThrow(buildingId);
        Building buildingUpdate = buildingMapper.toEntity(buildingDtoUpdate);

        if (buildingUpdate.getBuildingId() != null &&
                !building.getBuildingId().equals(buildingUpdate.getBuildingId())) {
            log.warn("Detected that a Building ID was specified in Building DTO request for updating building. " +
                    "It is also does not matched with the Building ID specified in the path variable. " +
                    "Disregarding the Building ID in the Building DTO request");
        }

        building.setBuildingName(buildingUpdate.getBuildingName());
        building.setStreet(buildingUpdate.getStreet());
        building.setBarangay(buildingUpdate.getBarangay());
        building.setCity(buildingUpdate.getCity());
        building.setProvince(buildingUpdate.getProvince());

        return buildingMapper.toDto(building);
    }

    public void deleteBuilding(Integer buildingId) {
        Building building = getBuildingOrThrow(buildingId);

        if (building.getRooms().size() > 0) {
            throw new ParentHasChildException("Building", "Room");
        }

        buildingRepository.delete(building);
    }

    public PageResponse<RoomDTO> getRoomsByBuildingId(Integer buildingId, Pageable pageable, RoomStatus status) {
        getBuildingOrThrow(buildingId);

        log.debug("Getting " + status + " Rooms...");
        Page<Room> roomsPage;
        switch (status) {
            case VACANT:
                roomsPage = roomRepository.findByBuildingIdAndStatus(buildingId, false, pageable);
                break;
            case OCCUPIED:
                roomsPage = roomRepository.findByBuildingIdAndStatus(buildingId, true, pageable);
                break;
            default:
                roomsPage = roomRepository.findByBuilding_BuildingId(buildingId, pageable);
                break;
        }

        List<RoomDTO> roomsDtos = roomMapper.toDtoList(roomsPage.getContent());

        return pageMapper.toPageResponse(roomsPage, roomsDtos);
    }

    public PageResponse<TenantDTO> getTenantsByBuildingID(Integer buildingId, Pageable pageable, TenantStatus status,
                                                          Boolean primaryOnly
    ) {
        getBuildingOrThrow(buildingId);

        log.debug("Getting " + status + " tenants...");
        Page<Tenant> tenantsPage;
        switch (status) {
            case ACTIVE:
                tenantsPage = tenantRepository.findByBuildingIdAndStatus(buildingId, true, primaryOnly, pageable);
                break;
            case INACTIVE:
                tenantsPage = tenantRepository.findByBuildingIdAndStatus(buildingId, false, primaryOnly, pageable);
                break;
            default:
                tenantsPage = tenantRepository.findByBuildingIdAndStatus(buildingId, null, primaryOnly, pageable);
                break;
        }

        List<TenantDTO> tenantDtos = tenantMapper.toDtoList(tenantsPage.getContent());

        return pageMapper.toPageResponse(tenantsPage, tenantDtos);
    }
}