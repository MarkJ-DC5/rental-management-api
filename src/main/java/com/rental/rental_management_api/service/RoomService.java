package com.rental.rental_management_api.service;

import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import com.rental.rental_management_api.exception.ImmutableFieldException;
import com.rental.rental_management_api.exception.ParentHasChildException;
import com.rental.rental_management_api.exception.ResourceNotFoundException;
import com.rental.rental_management_api.repository.RoomRepository;
import com.rental.rental_management_api.repository.TenantRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RoomService {
    // TODO: Handle edge cases or caching if performance becomes an issue.

    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    private Room getRoomOrThrow(Integer roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Room", roomId)
                );
    }

    public Room getRoomById(Integer roomId) {
        return getRoomOrThrow(roomId);
    }

    public List<Tenant> getTenantsByRoomId(Integer roomId, boolean primaryOnly) {
        Room room = getRoomOrThrow(roomId);
        List<Tenant> tenants = room.getTenants();

        log.debug("Retrieving tenants for Room " + roomId + " amd primaryOnly = " + primaryOnly);
        if (primaryOnly) {
            tenants = tenants
                    .stream()
                    .filter(Tenant::getIsPrimary)
                    .sorted(
                            Comparator.comparing(Tenant::getIsPrimary).reversed()
                                    .thenComparing(Tenant::getLastName, String.CASE_INSENSITIVE_ORDER)
                                    .thenComparing(Tenant::getFirstName, String.CASE_INSENSITIVE_ORDER)
                    )
                    .toList();


        }

        return tenants;
    }

    public Room updateRoom(Integer roomId, Room roomUpdate) {
        Room room = getRoomOrThrow(roomId);

        if (room.getBuilding().getBuildingId() != roomUpdate.getBuilding().getBuildingId()) {
            throw new ImmutableFieldException("Building ID", "Room");
        }

        room.setRoomName(roomUpdate.getRoomName());
        room.setRoomType(roomUpdate.getRoomType());
        room.setRoomId(roomUpdate.getRent());

        return roomRepository.save(room);
    }

    public void deleteRoom(Integer roomId) {
        Room room = getRoomOrThrow(roomId);

        if (room.getTenants().size() > 0) {
            throw new ParentHasChildException("Room", "tenant");
        }

        roomRepository.delete(room);
    }

    public Tenant saveTenant(Integer roomId, Tenant tenant) {
        Room room = getRoomOrThrow(roomId);
        tenant.setRoom(room);
        return tenantRepository.save(tenant);
    }

    public List<Tenant> updateTenants(Integer roomId, List<Tenant> tenantsUpdate) {
        long primaryCount = tenantsUpdate.stream()
                .filter(Tenant::getIsPrimary)
                .count();

        log.debug("Number of Primary Tenants: " + primaryCount);
        if (primaryCount != 1) {
            throw new IllegalArgumentException("Exactly one tenant must be marked as primary");
        }

        Room room = getRoomOrThrow(roomId);
        List<Tenant> tenants = room.getTenants();

        Map<Integer, Tenant> updatesById = tenantsUpdate.stream()
                .collect(
                        Collectors.toMap(Tenant::getTenantId, t -> t)
                );

        Set<Integer> originalIds = tenants.stream()
                .map(Tenant::getTenantId)
                .collect(Collectors.toSet());

        log.debug("Tenant IDs: " + originalIds);
        log.debug("Tenant IDs for Update: " + updatesById.keySet());
        if (!originalIds.equals(updatesById.keySet())) {
            throw new IllegalArgumentException("Tenant IDs do not match original tenants");
        }

        log.debug("Updating Tenants isPrimary and/or dateMovedOut...");
        tenants.forEach(original -> {
            Tenant updated = updatesById.get(original.getTenantId());
            original.setIsPrimary(updated.getIsPrimary());
            original.setDateMovedOut(updated.getDateMovedOut());
        });

        return tenants;
    }
}