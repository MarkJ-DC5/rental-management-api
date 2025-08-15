package com.rental.rental_management_api.mapper;

import com.rental.rental_management_api.payload.TenantDTO;
import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Mapper(componentModel = "spring") // Makes it injectable in Spring
public interface TenantMapper {
    TenantMapper INSTANCE = Mappers.getMapper(TenantMapper.class);

    @Mapping(target = "age", expression = "java(calculateAge(tenant.getBirthDate()))")
    @Mapping(target = "roomId", expression = "java(getRoomId(tenant.getRoom()))")
    TenantDTO toDto(Tenant tenant);

    Tenant toEntity(TenantDTO tenantDto);

    List<TenantDTO> toDtoList(List<Tenant> tenants);
    List<Tenant> toEntityList(List<TenantDTO> tenantDtos);

    default Integer calculateAge(LocalDate birthdate) {
        if (birthdate == null) return null;
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    default Integer getRoomId(Room room) {
        return room.getRoomId();
    }
}