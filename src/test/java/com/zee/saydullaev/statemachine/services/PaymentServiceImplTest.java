package com.zee.saydullaev.statemachine.services;

import com.zee.saydullaev.statemachine.domain.Payment;
import com.zee.saydullaev.statemachine.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().id(123451L).amount(new BigDecimal("13.99")).build();
    }

    @Test
    @Transactional
    void preAuth() {
        Payment savedPayment = paymentService.newPayment(payment);
        paymentService.preAuth(savedPayment.getId());

        Payment preAuthorizedPayment = paymentRepository.getById(savedPayment.getId());

        System.out.println(preAuthorizedPayment);


    }
}