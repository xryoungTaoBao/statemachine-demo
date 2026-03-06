package com.yubzhou.statemachine.config;

import com.yubzhou.statemachine.statemachine.enums.OrderEvent;
import com.yubzhou.statemachine.statemachine.enums.OrderState;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

/**
 * Spring Statemachine configuration – defines all states and transitions
 * for the order lifecycle.
 */
@Configuration
@EnableStateMachineFactory
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config) throws Exception {
        config.withConfiguration()
              .autoStartup(false);
    }

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states.withStates()
              .initial(OrderState.PENDING_PAYMENT)
              .end(OrderState.COMPLETED)
              .end(OrderState.CANCELLED)
              .end(OrderState.REFUNDED)
              .states(EnumSet.allOf(OrderState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        transitions
            // PAY_ORDER: PENDING_PAYMENT -> PENDING_SHIPMENT
            .withExternal()
                .source(OrderState.PENDING_PAYMENT).target(OrderState.PENDING_SHIPMENT)
                .event(OrderEvent.PAY_ORDER)
                .and()

            // PAYMENT_TIMEOUT: PENDING_PAYMENT -> CANCELLED
            .withExternal()
                .source(OrderState.PENDING_PAYMENT).target(OrderState.CANCELLED)
                .event(OrderEvent.PAYMENT_TIMEOUT)
                .and()

            // CANCEL_ORDER: PENDING_PAYMENT -> CANCELLED
            .withExternal()
                .source(OrderState.PENDING_PAYMENT).target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL_ORDER)
                .and()

            // CANCEL_ORDER: PENDING_SHIPMENT -> CANCELLED
            .withExternal()
                .source(OrderState.PENDING_SHIPMENT).target(OrderState.CANCELLED)
                .event(OrderEvent.CANCEL_ORDER)
                .and()

            // SHIP_ORDER: PENDING_SHIPMENT -> SHIPPED
            .withExternal()
                .source(OrderState.PENDING_SHIPMENT).target(OrderState.SHIPPED)
                .event(OrderEvent.SHIP_ORDER)
                .and()

            // CONFIRM_RECEIPT: SHIPPED -> COMPLETED
            .withExternal()
                .source(OrderState.SHIPPED).target(OrderState.COMPLETED)
                .event(OrderEvent.CONFIRM_RECEIPT)
                .and()

            // AUTO_CONFIRM_RECEIPT: SHIPPED -> COMPLETED
            .withExternal()
                .source(OrderState.SHIPPED).target(OrderState.COMPLETED)
                .event(OrderEvent.AUTO_CONFIRM_RECEIPT)
                .and()

            // REQUEST_REFUND: COMPLETED -> REFUNDING
            .withExternal()
                .source(OrderState.COMPLETED).target(OrderState.REFUNDING)
                .event(OrderEvent.REQUEST_REFUND)
                .and()

            // APPROVE_REFUND: REFUNDING -> REFUNDING (internal approval step)
            .withInternal()
                .source(OrderState.REFUNDING)
                .event(OrderEvent.APPROVE_REFUND)
                .and()

            // COMPLETE_REFUND: REFUNDING -> REFUNDED
            .withExternal()
                .source(OrderState.REFUNDING).target(OrderState.REFUNDED)
                .event(OrderEvent.COMPLETE_REFUND)
                .and()

            // REJECT_REFUND: REFUNDING -> COMPLETED
            .withExternal()
                .source(OrderState.REFUNDING).target(OrderState.COMPLETED)
                .event(OrderEvent.REJECT_REFUND);
    }
}
