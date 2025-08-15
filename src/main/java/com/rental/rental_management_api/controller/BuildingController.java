package com.rental.rental_management_api.controller;

import com.rental.rental_management_api.dto.BuildingDTO;
import com.rental.rental_management_api.dto.RoomDTO;
import com.rental.rental_management_api.dto.TenantDTO;
import com.rental.rental_management_api.mapper.BuildingMapper;
import com.rental.rental_management_api.mapper.RoomMapper;
import com.rental.rental_management_api.mapper.TenantMapper;
import com.rental.rental_management_api.service.BuildingService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/buildings")
public class BuildingController {
    /*
    TODO
    Consider adding basic JavaDocs.
    If you want to support paging later, prepare for Pageable injection instead of just Sort
     */

    private final BuildingService service;

    private final BuildingMapper buildingMapper;
    private final RoomMapper roomMapper;
    private final TenantMapper tenantMapper;

    @GetMapping()
    public List<BuildingDTO> getAllBuildings(){
        return buildingMapper.toDtoList(service.getAllBuildings());
    }

    @GetMapping(path = "/{buildingId}")
    public BuildingDTO getBuildingById(@PathVariable Integer buildingId){
        return buildingMapper.toDto(service.getBuildingById(buildingId));
    }

    @GetMapping(path = "/{buildingId}/rooms")
    // buildings/1/rooms?sort=roomName,desc&sort=rent,asc
    public List<RoomDTO> getRoomsByBuildingId(
            @PathVariable Integer buildingId,
            @SortDefault(sort = "roomName", direction = Sort.Direction.ASC) Sort sort){
        return roomMapper.toDtoList(service.getRoomsByBuildingId(buildingId, sort));
    }

    @GetMapping(path = "/{buildingId}/tenants")
    public List<TenantDTO> getTenantsByBuildingId(
            @PathVariable Integer buildingId,
            @SortDefault(sort = "lastName", direction = Sort.Direction.ASC) Sort sort){
        return tenantMapper.toDtoList(service.getTenantsByBuildingID(buildingId, sort));
    }
}