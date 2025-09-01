package com.rental.rental_management_api.controller;

import com.rental.rental_management_api.model.RoomStatus;
import com.rental.rental_management_api.model.TenantStatus;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/buildings")
@Tag(name = "1. Building", description = "Endpoints for building management")
@AllArgsConstructor
public class BuildingController {

    private final BuildingService service;

    @GetMapping()
    @Operation(summary = "Retrieve all buildings.")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<BuildingDTO>> getAllBuildings() {
        return ResponseEntity.ok(service.getAllBuildings());
    }

    @GetMapping(path = "/{buildingId}")
    @Operation(summary = "Retrieve a building by its ID.")
    public ResponseEntity<BuildingDTO> getBuildingById(
            @PathVariable Integer buildingId
    ) {
        return ResponseEntity.ok(service.getBuildingById(buildingId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new building.")
    public ResponseEntity<BuildingDTO> postBuilding(
            @Valid @RequestBody BuildingDTO buildingDTO
    ) {
        BuildingDTO savedBuilding = service.saveBuilding(buildingDTO);

        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{buildingId}")
                        .buildAndExpand(savedBuilding.getBuildingId()).toUri();

        return ResponseEntity.created(location).body(savedBuilding);
    }

    @PutMapping(path = "/{buildingId}")
    @Operation(
            summary = "Update an existing building",
            description = "The building ID must be provided in the path variable. If included in the request body, it will be ignored."
    )
    public ResponseEntity<BuildingDTO> putBuilding(
            @PathVariable Integer buildingId,
            @Valid @RequestBody BuildingDTO buildingDTO) {
        BuildingDTO buildingDto = service.updateBuilding(buildingId, buildingDTO);
        return ResponseEntity.ok(buildingDto);
    }

    @DeleteMapping(path = "/{buildingId}")
    @Operation(summary = "Delete a building by its ID, having no more linked rooms")
    public ResponseEntity<Void> deleteBuilding(
            @PathVariable Integer buildingId) {
        service.deleteBuilding(buildingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{buildingId}/rooms")
    @Operation(summary = "Retrieve rooms of a specific building.",
            description = "Supports pagination, sorting (e.g., by roomName, rent), and filtering by room status."
    )
    // http://localhost:8080/buildings/1/rooms?page0&size=10&sort=rent,asc&sort=roomName,asc
    public ResponseEntity<PageResponse<RoomDTO>> getRoomsByBuildingId(
            @PathVariable Integer buildingId,
            @ParameterObject @SortDefault(sort = "roomName", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(defaultValue = "all") RoomStatus status
    ) {
        return ResponseEntity.ok(service.getRoomsByBuildingId(buildingId, pageable, status));

    }

    @GetMapping(path = "/{buildingId}/tenants")
    @Operation(
            summary = "Retrieve tenants of a specific building.",
            description = "Supports pagination, sorting (e.g., by dateMovedIn, firstName), and filtering by tenant " +
                    "status (active = currently living, inactive = moved out, all) and by whether the tenant is the" +
                    " " +
                    "primary tenant of a room."
    )
    public ResponseEntity<PageResponse<TenantDTO>> getTenantsByBuildingId(
            @PathVariable Integer buildingId,
            @ParameterObject @SortDefault(sort = "room.roomId", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(defaultValue = "all") TenantStatus status,
            @RequestParam(defaultValue = "true") Boolean primaryOnly
    ) {
        return ResponseEntity.ok(service.getTenantsByBuildingID(buildingId, pageable, status, primaryOnly));
    }
}