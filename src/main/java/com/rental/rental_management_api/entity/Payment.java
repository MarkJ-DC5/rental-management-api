package com.rental.rental_management_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "for_month_of", nullable = false)
    private LocalDate forMonthOf;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "notes")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "tenant_id", referencedColumnName = "tenant_id", nullable = false)
    private Tenant tenant;

    public enum TransactionType {
        Rent,
        Utilities,
        Downpayment,
        Others
    }
}