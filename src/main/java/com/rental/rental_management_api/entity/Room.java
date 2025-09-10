package com.rental.rental_management_api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rental.rental_management_api.exception.PrimaryTenantConstraintException;
import com.rental.rental_management_api.model.RoomType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    @Column(name = "room_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Column(name = "rent", nullable = false)
    private Integer rent;

    @ManyToOne
    @JoinColumn(name = "building_id", referencedColumnName = "building_id", nullable = false)
    @JsonBackReference
    private Building building;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = false)
    @Where(clause = "date_moved_out IS NULL")
    private List<Tenant> tenants = new ArrayList<>();

    public Room(Integer roomId, String roomName, RoomType roomType, Integer rent, Building building) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomType = roomType;
        this.rent = rent;
        this.building = building;
    }

    public Tenant getPrimaryTenant() {
        if (tenants.isEmpty()) {
            return null;
        }

        long primaryTenantCount = tenants.stream()
                .filter(Tenant::getIsPrimary)
                .count();

        if (primaryTenantCount > 1) {
            throw new PrimaryTenantConstraintException(
                    "Room " + roomId + " contains multiple primary tenants and it's not allowed."
            );
        }

        if (primaryTenantCount == 0) {
            throw new PrimaryTenantConstraintException(
                    "Room " + roomId + " has tenants but no primary tenant assigned."
            );
        }

        return tenants.stream()
                .filter(Tenant::getIsPrimary)
                .findFirst()
                .orElseThrow(() ->
                        new PrimaryTenantConstraintException("Unexpected error: primary tenant not found.")
                );
    }

}
