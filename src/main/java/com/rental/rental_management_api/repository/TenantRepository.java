package com.rental.rental_management_api.repository;

import com.rental.rental_management_api.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TenantRepository extends JpaRepository<Tenant, Integer> {
    @Query("""
                SELECT t
                FROM Tenant t
                WHERE t.room.building.buildingId = :buildingId
                AND (
                    :isActive IS NULL
                    OR (
                        (:isActive = TRUE AND t.dateMovedOut IS NULL)
                        OR
                        (:isActive = FALSE AND t.dateMovedOut IS NOT NULL)
                    )
                )
               AND t.isPrimary = :isPrimary
            """)
    Page<Tenant> findByBuildingIdAndStatus(@Param("buildingId") Integer buildingId,
                                           @Param("isActive") Boolean isActive,
                                           @Param("isPrimary") Boolean isPrimary,
                                           Pageable pageable);

    @Query("""
                SELECT t
                FROM Tenant t
                WHERE t.room.building.buildingId = :buildingId
                AND t.isPrimary = :isPrimary
            """)
    Page<Tenant> findByBuildingId(@Param("buildingId") Integer buildingId,
                                           @Param("isPrimary") Boolean isPrimary,
                                           Pageable pageable);

    Integer countByRoom_RoomId(Integer roomId);
}