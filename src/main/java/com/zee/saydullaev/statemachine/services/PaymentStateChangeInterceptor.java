package com.zee.saydullaev.statemachine.services;

import com.zee.saydullaev.statemachine.domain.Payment;
import com.zee.saydullaev.statemachine.domain.PaymentEvent;
import com.zee.saydullaev.statemachine.domain.PaymentState;
import com.zee.saydullaev.statemachine.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final PaymentRepository paymentRepository;

    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state,
                               Message<PaymentEvent> message,
                               Transition<PaymentState, PaymentEvent> transition,
                               StateMachine<PaymentState, PaymentEvent> stateMachine,
                               StateMachine<PaymentState, PaymentEvent> rootStateMachine) {

        Optional.ofNullable(message).ifPresent(paymentEventMessage -> {
            Optional.ofNullable(Long.class.cast(paymentEventMessage.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER, -1L)))
            .ifPresent(paymentId -> {
                Payment payment = paymentRepository.getById(paymentId);
                payment.setState(state.getId());
                paymentRepository.save(payment);
            });
        });
    }
}
