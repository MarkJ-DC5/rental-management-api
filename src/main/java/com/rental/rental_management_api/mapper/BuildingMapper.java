package com.rental.rental_management_api.mapper;

import com.rental.rental_management_api.entity.Building;
import com.rental.rental_management_api.payload.BuildingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring") // Makes it injectable in Spring
public interface BuildingMapper {
    BuildingMapper INSTANCE = Mappers.getMapper(BuildingMapper.class);

    @Mapping(target = "address", expression =
            "java(building.getStreet() + \", \" + building.getBarangay() + \", \" + " +
                    "building.getCity() + \", \" + " +
                    "building.getProvince())")
    BuildingDTO toDto(Building building);

    @Mapping(target = "street", expression = "java(buildingDTO.getAddress().split(\", \")[0])")
    @Mapping(target = "barangay", expression = "java(buildingDTO.getAddress().split(\", \")[1])")
    @Mapping(target = "city", expression = "java(buildingDTO.getAddress().split(\", \")[2])")
    @Mapping(target = "province", expression = "java(buildingDTO.getAddress().split(\", \")[3])")
    Building toEntity(BuildingDTO buildingDTO);

    List<BuildingDTO> toDtoList(List<Building> buildings);

    List<Building> toEntityList(List<BuildingDTO> buildingDtos);
}