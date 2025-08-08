package com.rental.rental_management_api.repository;

import com.rental.rental_management_api.entity.Room;
import com.rental.rental_management_api.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Integer> {
}