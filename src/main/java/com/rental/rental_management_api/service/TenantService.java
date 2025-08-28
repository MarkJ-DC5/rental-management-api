package com.rental.rental_management_api.service;

import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import com.rental.rental_management_api.exception.BusinessConstraintException;
import com.rental.rental_management_api.exception.PrimaryTenantConstraintException;
import com.rental.rental_management_api.exception.ResourceNotFoundException;
import com.rental.rental_management_api.mapper.BuildingMapper;
import com.rental.rental_management_api.mapper.PageMapper;
import com.rental.rental_management_api.mapper.RoomMapper;
import com.rental.rental_management_api.mapper.TenantMapper;
import com.rental.rental_management_api.payload.AssignPrimaryTenantRequest;
import com.rental.rental_management_api.payload.MoveOutTenantRequest;
import com.rental.rental_management_api.payload.TenantDTO;
import com.rental.rental_management_api.repository.BuildingRepository;
import com.rental.rental_management_api.repository.RoomRepository;
import com.rental.rental_management_api.repository.TenantRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class TenantService {
    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final TenantMapper tenantMapper;
    private final PageMapper pageMapper;

    private final RoomService roomService;

    protected Tenant getTenantOrThrow(Integer tenantId) {
        return tenantRepository.findById(tenantId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Tenant", tenantId)
                );
    }

    public TenantDTO getTenantById(Integer tenantId) {
        return tenantMapper.toDto(getTenantOrThrow(tenantId));
    }

    public List<TenantDTO> getTenantsByRoomId(Integer roomId, boolean primaryOnly) {
        Room room = roomService.getRoomOrThrow(roomId);
        List<Tenant> tenants = room.getTenants();

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

        return tenantMapper.toDtoList(tenants);
    }

    public TenantDTO saveTenant(Integer roomId, TenantDTO tenantDTO) {
        Room room = roomService.getRoomOrThrow(roomId);

        Tenant tenant = tenantMapper.toEntity(tenantDTO);
        tenant.setTenantId(null);
        tenant.setDateMovedOut(null);
        tenant.setRoom(room);

        if (room.getPrimaryTenant() == null) {
            if (!tenant.getIsPrimary()) {
                throw new PrimaryTenantConstraintException(
                        "Room " + roomId + " has no primary tenant. The tenant being added must have isPrimary = true."
                );
            }
        } else {
            if (tenant.getIsPrimary()) {
                throw new PrimaryTenantConstraintException(
                        "Room " + roomId + " already has primary tenant. The tenant being added must have isPrimary =" +
                                " false."
                );
            }
        }

        return tenantMapper.toDto(tenantRepository.save(tenant));
    }

    public TenantDTO updateTenant(Integer tenantId, TenantDTO tenantDtoUpdate) {
        Tenant tenant = getTenantOrThrow(tenantId);

        tenant.setFirstName(tenantDtoUpdate.getFirstName());
        tenant.setLastName(tenantDtoUpdate.getLastName());
        tenant.setMiddleName(tenantDtoUpdate.getMiddleName());
        tenant.setBirthDate(tenantDtoUpdate.getBirthDate());
        tenant.setGender(tenantDtoUpdate.getGender());
        tenant.setContactNumber(tenantDtoUpdate.getContactNumber());

        return tenantMapper.toDto(tenant);
    }

    public TenantDTO changePrimaryTenant(Integer roomId, AssignPrimaryTenantRequest body) {
        Room room = roomService.getRoomOrThrow(roomId);

        Integer newPrimaryTenantID = body.getNewPrimaryTenantID();

        Tenant currentPrimaryTenant = room.getPrimaryTenant();
        if (currentPrimaryTenant.getTenantId().equals(newPrimaryTenantID)) {
            log.info("Tenant " + newPrimaryTenantID + " is already the primary tenant. No change will happen");
            return tenantMapper.toDto(currentPrimaryTenant);
        }

        List<Tenant> tenants = room.getTenants();

        Tenant newPrimaryTenant = tenants.stream()
                .filter(tenant ->
                        tenant.getTenantId().equals(newPrimaryTenantID)
                )
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", newPrimaryTenantID));

        currentPrimaryTenant.setIsPrimary(false);
        newPrimaryTenant.setIsPrimary(true);

        return tenantMapper.toDto(newPrimaryTenant);
    }

    public List<TenantDTO> moveOutTenants(Integer roomId, MoveOutTenantRequest request) {
        Room room = roomService.getRoomOrThrow(roomId);
        List<Tenant> tenants = room.getTenants();

        tenants.forEach(tenant -> tenant.setDateMovedOut(request.getDateMovedOut()));

        return tenantMapper.toDtoList(tenants);
    }

    public void deleteTenant(Integer tenantId) {
        Tenant tenant = getTenantOrThrow(tenantId);
        Room room = tenant.getRoom();

        if (room.getTenants().size() > 1 && tenant.getIsPrimary()) {
            throw new PrimaryTenantConstraintException(
                    "Tenant " + tenantId + " is the primary tenant of Room " + room.getRoomId() +
                            " and cannot be deleted while there are other tenants. Assign a different primary tenant first before deleting this tenant.");
        }

        if (tenant.getDateMovedOut() == null) {
            throw new BusinessConstraintException(
                    "Tenant " + tenantId + " is still active. Make them first inactive by setting the date moved out " +
                            "before deleting"
            );
        }


        tenantRepository.delete(tenant);
    }
}