package com.zee.saydullaev.statemachine.config.actions;

import com.zee.saydullaev.statemachine.domain.PaymentEvent;
import com.zee.saydullaev.statemachine.domain.PaymentState;
import com.zee.saydullaev.statemachine.services.PaymentServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;

import java.util.Random;

public class AuthAction {

    @Bean
    private Action<PaymentState, PaymentEvent> authAction() {
        return context -> {
            System.out.println("Auth was called");

            if(new Random().nextInt(10 ) < 8) {
                System.out.println("Auth Approved");
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED)
                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER)).build());
            } else {
                System.out.println("DECLINED! NO CREDIT");
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER)).build());
            }
        };
    }

}
