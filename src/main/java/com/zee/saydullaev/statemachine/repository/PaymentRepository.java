package com.zee.saydullaev.statemachine.repository;

import com.zee.saydullaev.statemachine.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
