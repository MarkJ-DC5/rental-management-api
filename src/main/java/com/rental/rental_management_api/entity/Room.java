package com.rental.rental_management_api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rental.rental_management_api.model.RoomType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
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

    //TODO: Cascade Rule
    @ManyToOne
    @JoinColumn(name = "building_id", referencedColumnName = "building_id", nullable = false)
    @JsonBackReference
    private Building building;

    @OneToMany(mappedBy = "room")
    private List<Tenant> tenants = new ArrayList<>();

    public Room(Integer roomId, String roomName, RoomType roomType, Integer rent, Building building) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomType = roomType;
        this.rent = rent;
        this.building = building;
    }
}
