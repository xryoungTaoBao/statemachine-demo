package com.yubzhou.statemachine.statemachine.interceptor;

import com.yubzhou.statemachine.order.entity.Order;
import com.yubzhou.statemachine.order.mapper.OrderMapper;
import com.yubzhou.statemachine.statemachine.constant.StateMachineContextKeys;
import com.yubzhou.statemachine.statemachine.enums.OrderEvent;
import com.yubzhou.statemachine.statemachine.enums.OrderState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Interceptor that persists the new state to the {@code t_order} table after
 * every successful state transition.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatePersistInterceptor
        extends StateMachineInterceptorAdapter<OrderState, OrderEvent> {

    private final OrderMapper orderMapper;

    @Override
    public void postStateChange(State<OrderState, OrderEvent> state,
                                 org.springframework.messaging.Message<OrderEvent> message,
                                 Transition<OrderState, OrderEvent> transition,
                                 StateMachine<OrderState, OrderEvent> stateMachine,
                                 StateMachine<OrderState, OrderEvent> rootStateMachine) {

        Long orderId = (Long) stateMachine.getExtendedState()
                                          .getVariables()
                                          .get(StateMachineContextKeys.ORDER_ID);
        if (orderId == null) {
            return;
        }

        OrderState newState = state.getId();
        Order update = new Order().setId(orderId).setState(newState.name());

        // Set timestamp fields based on new state
        switch (newState) {
            case PENDING_SHIPMENT -> update.setPaymentTime(LocalDateTime.now());
            case SHIPPED          -> update.setShipTime(LocalDateTime.now());
            case COMPLETED        -> update.setReceiveTime(LocalDateTime.now());
            case CANCELLED        -> update.setCancelTime(LocalDateTime.now());
            case REFUNDED         -> update.setRefundTime(LocalDateTime.now());
            default               -> { /* no extra timestamp */ }
        }

        int rows = orderMapper.updateById(update);
        log.debug("Persisted state {} for orderId={} ({} rows updated)", newState, orderId, rows);
    }
}
