package com.rental.rental_management_api.controller;

import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import com.rental.rental_management_api.mapper.BuildingMapper;
import com.rental.rental_management_api.mapper.PageMapper;
import com.rental.rental_management_api.mapper.RoomMapper;
import com.rental.rental_management_api.mapper.TenantMapper;
import com.rental.rental_management_api.payload.RoomDTO;
import com.rental.rental_management_api.payload.TenantDTO;
import com.rental.rental_management_api.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/rooms")
@Tag(name = "Room", description = "Endpoints for room management")
public class RoomController {

    private final RoomService service;

    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final TenantMapper tenantMapper;
    private final PageMapper pageMapper;

    @GetMapping(path = "/{roomId}")
    @Operation(summary = "Retrieve a room by its ID")
    public ResponseEntity<RoomDTO> getRoomById(
            @PathVariable Integer roomId
    ) {
        return ResponseEntity.ok(roomMapper.toDto(service.getRoomById(roomId)));
    }

    @GetMapping(path = "/{roomId}/tenants")
    @Operation(summary = "Retrieve tenant/s of a specific room")
    public ResponseEntity<List<TenantDTO>> getRoomById(
            @PathVariable Integer roomId,
            @RequestParam(defaultValue = "true") boolean primaryOnly
    ) {
        return ResponseEntity.ok(tenantMapper.toDtoList(service.getTenantsByRoomId(roomId, primaryOnly)));
    }

    @PutMapping(path = "/{roomId}")
    @Operation(summary = "Update the info of an existing room. Update of Building is not allowed")
    public ResponseEntity<RoomDTO> putRoom(
            @PathVariable Integer roomId,
            @RequestBody @Valid RoomDTO roomDTO) {
        Room room = roomMapper.toEntity(roomDTO);
        room = service.updateRoom(roomId, room);
        return ResponseEntity.ok(roomMapper.toDto(room));
    }

    @DeleteMapping(path = "/{roomId}")
    @Operation(summary = "Delete a room by its ID, having no more linked tenants")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Integer roomId) {
        service.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{roomId}/tenants")
    @Operation(summary = "Create a New Tenant")
    public ResponseEntity<TenantDTO> postTenant(
            @PathVariable Integer roomId,
            @RequestBody @Valid TenantDTO tenantDTO
    ) {
        Tenant tenant = tenantMapper.toEntity(tenantDTO);
        Tenant savedTenant = service.saveTenant(roomId, tenant);

        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{tenantId}")
                        .buildAndExpand(savedTenant.getTenantId()).toUri();

        return ResponseEntity.created(location).body(tenantMapper.toDto(savedTenant));
    }

    @PutMapping(path = "/{roomId}/tenants")
    @Operation(summary = "Update tenant status: set primary or mark as moved out")
    public ResponseEntity<List<TenantDTO>> updateTenants(
            @PathVariable Integer roomId,
            @RequestBody List<@Valid TenantDTO> tenantDtos) {
        List<Tenant> tenants = tenantMapper.toEntityList(tenantDtos);
        tenants = service.updateTenants(roomId, tenants);

        return ResponseEntity.ok(tenantMapper.toDtoList(tenants));
    }
}