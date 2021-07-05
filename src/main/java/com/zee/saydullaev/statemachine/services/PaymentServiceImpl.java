package com.zee.saydullaev.statemachine.services;

import com.zee.saydullaev.statemachine.domain.Payment;
import com.zee.saydullaev.statemachine.domain.PaymentEvent;
import com.zee.saydullaev.statemachine.domain.PaymentState;
import com.zee.saydullaev.statemachine.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    public static final String PAYMENT_ID_HEADER = "payment_id";

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEvent(paymentId, stateMachine, PaymentEvent.PRE_AUTHORIZE);
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEvent(paymentId, stateMachine, PaymentEvent.AUTH_APPROVED);

        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEvent(paymentId, stateMachine, PaymentEvent.AUTH_DECLINED);

        return null;
    }

    private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> stateMachine, PaymentEvent event) {
        Object payload;
        Message ms = MessageBuilder
                .withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();

        stateMachine.sendEvent(ms);
    }

    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
        Payment payment = paymentRepository.getById(paymentId);

        StateMachine<PaymentState, PaymentEvent> stateMachine = stateMachineFactory.getStateMachine(Long.toString(payment.getId()));

        stateMachine.stop();

        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(paymentServicePaymentEventStateMachineAccess -> {
                    paymentServicePaymentEventStateMachineAccess.addStateMachineInterceptor(paymentStateChangeInterceptor);
                    paymentServicePaymentEventStateMachineAccess.resetStateMachine(new DefaultStateMachineContext(payment.getState(), null, null, null));
                });

        stateMachine.start();

        return stateMachine;
    }

}
