package com.rental.rental_management_api.repository;

import com.rental.rental_management_api.entity.Room;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByBuilding_BuildingId(Integer buildingId, Sort sort);
}