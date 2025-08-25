package com.rental.rental_management_api.controller;

import com.rental.rental_management_api.payload.RoomDTO;
import com.rental.rental_management_api.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@Tag(name = "2. Room", description = "Endpoints for room management")
@AllArgsConstructor
public class RoomController {

    private final RoomService service;

    @GetMapping(path = "/rooms/{roomId}")
    @Operation(summary = "Retrieve a room by its ID")
    public ResponseEntity<RoomDTO> getRoomById(
            @PathVariable Integer roomId
    ) {
        return ResponseEntity.ok(service.getRoomById(roomId));
    }

    @PostMapping(path = "/buildings/{buildingId}/rooms")
    @Operation(summary = "Create a New Room",
            description = "The Building ID must be provided in the path variable. If included in the request body, it" +
                    " will be ignored."
    )
    public ResponseEntity<RoomDTO> postRoom(
            @PathVariable Integer buildingId,
            @RequestBody @Valid RoomDTO roomDto
    ) {
        RoomDTO savedRoomDto = service.saveRoom(buildingId, roomDto);

        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{roomId}")
                        .buildAndExpand(savedRoomDto.getRoomId()).toUri();

        return ResponseEntity.created(location).body(savedRoomDto);
    }

    @PutMapping(path = "/rooms/{roomId}")
    @Operation(
            summary = "Update an existing room",
            description = "The room ID must be provided in the path variable. If included in the request body, it " +
                    "will be ignored. The Building ID cannot be changed by any means."
    )
    public ResponseEntity<RoomDTO> putRoom(
            @PathVariable Integer roomId,
            @RequestBody @Valid RoomDTO roomDTO) {
        return ResponseEntity.ok(service.updateRoom(roomId, roomDTO));
    }

    @DeleteMapping(path = "/rooms/{roomId}")
    @Operation(summary = "Delete a room by its ID, having no more linked tenants")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable Integer roomId) {
        service.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }


//

//
//    @PutMapping(path = "/{roomId}/tenants")
//    @Operation(summary = "Update tenant status: set primary or mark as moved out")
//    public ResponseEntity<List<TenantDTO>> updateTenants(
//            @PathVariable Integer roomId,
//            @RequestBody List<@Valid TenantDTO> tenantDtos) {
//        log.debug("Tenants DTO: " + tenantDtos);
//        List<Tenant> tenants = tenantMapper.toEntityList(tenantDtos);
//        log.debug("Tenants: " + tenants);
//
//        tenants = service.updateTenants(roomId, tenants);
//
//        return ResponseEntity.ok(tenantMapper.toDtoList(tenants));
//    }
}