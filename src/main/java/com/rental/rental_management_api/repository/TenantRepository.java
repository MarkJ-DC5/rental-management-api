package com.rental.rental_management_api.repository;

import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenantRepository extends JpaRepository<Tenant, Integer> {
    Page<Tenant> findByRoom_Building_BuildingId(Integer buildingId, Pageable pageable);
}