package com.rental.rental_management_api.repository;

import com.rental.rental_management_api.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Page<Transaction> findByTransactionDateBetween(LocalDate start,
                                                   LocalDate end, Pageable pageable);

    Page<Transaction> findByRoom_RoomIdAndTransactionDateBetween(Integer roomId, LocalDate start,
                                                                 LocalDate end, Pageable pageable);

    Page<Transaction> findByTenant_TenantIdAndTransactionDateBetween(Integer roomId, LocalDate start,
                                                                     LocalDate end, Pageable pageable);
}