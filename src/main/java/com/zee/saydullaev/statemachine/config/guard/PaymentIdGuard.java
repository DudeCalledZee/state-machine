package com.zee.saydullaev.statemachine.config.guard;

import com.zee.saydullaev.statemachine.domain.PaymentEvent;
import com.zee.saydullaev.statemachine.domain.PaymentState;
import com.zee.saydullaev.statemachine.services.PaymentServiceImpl;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
public class PaymentIdGuard implements  Guard<PaymentState, PaymentEvent> {

    @Override
    public boolean evaluate(StateContext<PaymentState, PaymentEvent> context) {
        return context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER) != null;
    }
}
