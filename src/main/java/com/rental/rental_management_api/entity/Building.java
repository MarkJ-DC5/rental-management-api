package com.rental.rental_management_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

//1hr 30mins
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString
public class Building {
    @Id
    @Column(name = "building_id")
    private String buildingId;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "barangay", nullable = false)
    private String barangay;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "province", nullable = false)
    private String province;

    @OneToMany(mappedBy = "building")
    private List<Room> rooms = new ArrayList<>();

    public Building(String buildingId, String street, String barangay, String city, String province) {
        this.buildingId = buildingId;
        this.street = street;
        this.barangay = barangay;
        this.city = city;
        this.province = province;
    }
}