package com.rental.rental_management_api.repository;

import com.rental.rental_management_api.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    Page<Room> findByBuilding_BuildingId(Integer buildingId, Pageable pageable);

    @Query("""
                SELECT r
                FROM Room r
                WHERE r.building.buildingId = :buildingId
                AND (
                    (:hasTenant = TRUE AND SIZE(r.tenants) > 0)
                    OR
                    (:hasTenant = False AND SIZE(r.tenants) = 0)
               )
            """)
    Page<Room> findByBuildingIdAndStatus(@Param("buildingId") Integer buildingId,
                                         @Param("hasTenant") Boolean hasTenant,
                                         Pageable pageable);
}