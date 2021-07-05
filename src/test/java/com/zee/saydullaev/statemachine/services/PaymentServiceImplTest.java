package com.zee.saydullaev.statemachine.services;

import com.zee.saydullaev.statemachine.domain.Payment;
import com.zee.saydullaev.statemachine.domain.PaymentEvent;
import com.zee.saydullaev.statemachine.domain.PaymentState;
import com.zee.saydullaev.statemachine.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import javax.transaction.Transactional;
import java.math.BigDecimal;

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

    @RepeatedTest(10)
    @Transactional
    void testAuth() {
        Payment savedPayment = paymentService.newPayment(payment);
        StateMachine<PaymentState, PaymentEvent> preAuthSM = paymentService.preAuth(savedPayment.getId());

        if (preAuthSM.getState().getId() == PaymentState.PRE_AUTH){
            System.out.println("PAYMENT IS PRE AUTHORIZED");
            StateMachine<PaymentState, PaymentEvent> stateMachine = paymentService.authorizePayment(savedPayment.getId());

            System.out.println("Result of Auth: " + stateMachine.getState().getId());
        } else {
            System.out.println("PAYMENT FAILED pre-auth...");
        }
    }

}
