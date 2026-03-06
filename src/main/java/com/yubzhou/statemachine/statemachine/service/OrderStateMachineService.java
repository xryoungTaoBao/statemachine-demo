package com.yubzhou.statemachine.statemachine.service;

import com.yubzhou.statemachine.order.dto.OrderEventRequest;
import com.yubzhou.statemachine.order.entity.Order;
import com.yubzhou.statemachine.statemachine.enums.OrderEvent;
import com.yubzhou.statemachine.statemachine.enums.OrderState;
import org.springframework.statemachine.StateMachine;

/**
 * Core service for managing order state machines.
 */
public interface OrderStateMachineService {

    /**
     * Send an event to the state machine for a given order.
     *
     * @param order   the order to transition
     * @param event   the event to send
     * @param request optional context for the event
     */
    void sendEvent(Order order, OrderEvent event, OrderEventRequest request);

    /**
     * Acquire (restore or create) the state machine for the given order.
     */
    StateMachine<OrderState, OrderEvent> acquireStateMachine(Long orderId, OrderState currentState);

    /**
     * Release the state machine back to the pool / persist context.
     */
    void releaseStateMachine(Long orderId, StateMachine<OrderState, OrderEvent> stateMachine);
}
