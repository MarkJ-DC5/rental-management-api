package com.rental.rental_management_api.repository;

import com.rental.rental_management_api.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    Page<Room> findByBuilding_BuildingId(Integer buildingId, Pageable pageable);
}