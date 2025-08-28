package com.rental.rental_management_api.controller;


import com.rental.rental_management_api.payload.AssignPrimaryTenantRequest;
import com.rental.rental_management_api.payload.MoveOutTenantRequest;
import com.rental.rental_management_api.payload.TenantDTO;
import com.rental.rental_management_api.service.TenantService;
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
@Tag(name = "3. Tenant", description = "Endpoints for tenant management")
public class TenantController {

    private final TenantService service;

    @GetMapping(path = "/rooms/{roomId}/tenants")
    @Operation(summary = "Retrieve tenant/s of a specific room")
    public ResponseEntity<List<TenantDTO>> getRoomById(
            @PathVariable Integer roomId,
            @RequestParam(defaultValue = "true") boolean primaryOnly
    ) {
        return ResponseEntity.ok(service.getTenantsByRoomId(roomId, primaryOnly));
    }

    @GetMapping(path = "/tenants/{tenantId}")
    @Operation(summary = "Retrieve a tenant by its ID.")
    public ResponseEntity<TenantDTO> getTenantById(
            @PathVariable Integer tenantId
    ) {
        return ResponseEntity.ok(service.getTenantById(tenantId));
    }

    @PostMapping(path = "/rooms/{roomId}/tenants")
    @Operation(summary = "Create a New Tenant.",
            description = "The Room ID must be provided in the path variable. If included in the request body, it" +
                    " will be ignored."
    )
    public ResponseEntity<TenantDTO> postTenant(
            @PathVariable Integer roomId,
            @RequestBody @Valid TenantDTO tenantDTO
    ) {
        TenantDTO savedTenant = service.saveTenant(roomId, tenantDTO);

        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{tenantId}")
                        .buildAndExpand(savedTenant.getTenantId()).toUri();

        return ResponseEntity.created(location).body(savedTenant);
    }

    @PutMapping(path = "/tenants/{tenantId}")
    @Operation(summary = "Update the personal info of an existing tenant.",
            description = "Updating assigned Room, Primary Status, and Move-in/Move-out Date is not allowed and will " +
                    "be ignored.")
    public ResponseEntity<TenantDTO> putTenant(
            @PathVariable Integer tenantId,
            @RequestBody @Valid TenantDTO tenantDto) {
        return ResponseEntity.ok(service.updateTenant(tenantId, tenantDto));
    }

    @PutMapping(path = "/rooms/{roomdId}/tenants/change-primary")
    @Operation(summary = "Change the assigned primary tenant of a room.")
    public ResponseEntity<TenantDTO> changePrimaryTenant(
            @PathVariable Integer roomdId,
            @RequestBody @Valid AssignPrimaryTenantRequest body) {
        return ResponseEntity.ok(service.changePrimaryTenant(roomdId, body));
    }

    @PutMapping(path = "/rooms/{roomdId}/tenants/move-out")
    @Operation(summary = "Set the move-out date of all tenants in a room.",
            description = "The action will set the status of the tenants to inactive and making the room vacant.")
    public ResponseEntity<List<TenantDTO>> moveOutTenants(
            @PathVariable Integer roomdId,
            @RequestBody @Valid MoveOutTenantRequest body) {
        return ResponseEntity.ok(service.moveOutTenants(roomdId, body));
    }


    @DeleteMapping(path = "/tenants/{tenantId}")
    @Operation(summary = "Delete a tenant by its ID.")
    public ResponseEntity<Void> deleteTenant(
            @PathVariable Integer tenantId) {
        service.deleteTenant(tenantId);
        return ResponseEntity.noContent().build();
    }
}