package com.rental.rental_management_api.controller;

import com.rental.rental_management_api.entity.Building;
import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import com.rental.rental_management_api.mapper.BuildingMapper;
import com.rental.rental_management_api.mapper.PageMapper;
import com.rental.rental_management_api.mapper.RoomMapper;
import com.rental.rental_management_api.mapper.TenantMapper;
import com.rental.rental_management_api.payload.BuildingDTO;
import com.rental.rental_management_api.payload.PageResponse;
import com.rental.rental_management_api.payload.RoomDTO;
import com.rental.rental_management_api.payload.TenantDTO;
import com.rental.rental_management_api.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/buildings")
@Tag(name = "Building", description = "Endpoints for building management")
public class BuildingController {

    private final BuildingService service;

    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final TenantMapper tenantMapper;
    private final PageMapper pageMapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieve all buildings")
    public ResponseEntity<List<BuildingDTO>> getAllBuildings() {
        return ResponseEntity.ok(buildingMapper.toDtoList(service.getAllBuildings()));
    }

    @GetMapping(path = "/{buildingId}")
    @Operation(summary = "Retrieve a building by its ID")
    public ResponseEntity<BuildingDTO> getBuildingById(
            @PathVariable Integer buildingId
    ) {
        return ResponseEntity.ok(buildingMapper.toDto(service.getBuildingById(buildingId)));
    }

    @GetMapping(path = "/{buildingId}/rooms")
    @Operation(summary = "Retrieve rooms of a specific building")
    // http://localhost:8080/buildings/1/rooms?page0&size=10&sort=rent,asc&sort=roomName,asc
    public ResponseEntity<PageResponse<RoomDTO>> getRoomsByBuildingId(
            @PathVariable Integer buildingId,
            @ParameterObject @SortDefault(sort = "roomName", direction = Sort.Direction.ASC) Pageable pageable
    ) {

        Page<Room> rooomsPage = service.getRoomsByBuildingId(buildingId, pageable);
        List<RoomDTO> roomsDtos = roomMapper.toDtoList(rooomsPage.getContent());

        return ResponseEntity.ok(pageMapper.toPageResponse(rooomsPage, roomsDtos));

    }

    @GetMapping(path = "/{buildingId}/tenants")
    @Operation(summary = "Retrieve tenants of a specific building")
    public ResponseEntity<PageResponse<TenantDTO>> getTenantsByBuildingId(
            @PathVariable Integer buildingId,
            @ParameterObject @SortDefault(sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Tenant> tenantsPage = service.getTenantsByBuildingID(buildingId, pageable);
        List<TenantDTO> tenantDtos = tenantMapper.toDtoList(tenantsPage.getContent());

        return ResponseEntity.ok(pageMapper.toPageResponse(tenantsPage, tenantDtos));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new building")
    public ResponseEntity<BuildingDTO> postBuilding(
            @Valid @RequestBody BuildingDTO buildingDTO
    ) {
        Building building = buildingMapper.toEntity(buildingDTO);
        Building savedBuilding = service.saveBuilding(building);

        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{buildingId}")
                        .buildAndExpand(savedBuilding.getBuildingId()).toUri();

        return ResponseEntity.created(location).body(buildingMapper.toDto(savedBuilding));
    }

    @PutMapping(path = "/{buildingId}")
    @Operation(summary = "Update an existing building")
    public ResponseEntity<BuildingDTO> putBuilding(
            @PathVariable Integer buildingId,
            @Valid @RequestBody BuildingDTO buildingDTO) {
        Building building = buildingMapper.toEntity(buildingDTO);
        building = service.updateBuilding(buildingId, building);
        return ResponseEntity.ok(buildingMapper.toDto(building));
    }

    @DeleteMapping(path = "/{buildingId}")
    @Operation(summary = "Delete a building by its ID")
    public ResponseEntity<Void> deleteBuilding(
            @PathVariable Integer buildingId) {
        service.deleteBuilding(buildingId);
        return ResponseEntity.noContent().build();
    }
}