package com.rental.rental_management_api.mapper;

import com.rental.rental_management_api.entity.Tenant;
import com.rental.rental_management_api.payload.TenantDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Mapper(componentModel = "spring") // Makes it injectable in Spring
public interface TenantMapper {

    @Mapping(target = "age", expression = "java(calculateAge(tenant.getBirthDate()))")
    @Mapping(target = "roomId", expression = "java(tenant.getRoom().getRoomId())")
    TenantDTO toDto(Tenant tenant);

    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "room", ignore = true)
    Tenant toEntity(TenantDTO tenantDto);

    List<TenantDTO> toDtoList(List<Tenant> tenants);

    List<Tenant> toEntityList(List<TenantDTO> tenantDtos);

    default Integer calculateAge(LocalDate birthdate) {
        if (birthdate == null) return null;
        return Period.between(birthdate, LocalDate.now()).getYears();
    }
}