package com.rental.rental_management_api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "building_id")
    private Integer buildingId;

    @Column(name = "building_name", nullable = false)
    private String buildingName;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "barangay", nullable = false)
    private String barangay;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "province", nullable = false)
    private String province;

    @OneToMany(mappedBy = "building", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Room> rooms = new ArrayList<>();

    public Building(String buildingName, String street, String barangay, String city, String province) {
        this.buildingId = null;
        this.buildingName = buildingName;
        this.street = street;
        this.barangay = barangay;
        this.city = city;
        this.province = province;
    }
}