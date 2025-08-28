package com.rental.rental_management_api.entity;

import com.rental.rental_management_api.model.TenantGender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tenant_id")
    private Integer tenantId;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private TenantGender gender;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Column(name = "date_moved_in", nullable = false)
    private LocalDate dateMovedIn;

    @Column(name = "date_moved_out")
    private LocalDate dateMovedOut;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "room_id", nullable = false)
    private Room room;
}