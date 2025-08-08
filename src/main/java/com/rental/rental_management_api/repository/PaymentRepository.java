package com.rental.rental_management_api.repository;

import com.rental.rental_management_api.entity.Payment;
import com.rental.rental_management_api.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}