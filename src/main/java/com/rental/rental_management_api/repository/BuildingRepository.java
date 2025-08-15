package com.rental.rental_management_api.repository;

import com.rental.rental_management_api.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuildingRepository extends JpaRepository<Building, Integer> {
}