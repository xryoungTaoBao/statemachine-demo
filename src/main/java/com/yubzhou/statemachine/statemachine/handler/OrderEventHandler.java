package com.yubzhou.statemachine.statemachine.handler;

import com.yubzhou.statemachine.common.exception.BusinessException;
import com.yubzhou.statemachine.order.dto.OrderEventRequest;
import com.yubzhou.statemachine.order.entity.Order;
import com.yubzhou.statemachine.order.service.OrderService;
import com.yubzhou.statemachine.statemachine.enums.OrderEvent;
import com.yubzhou.statemachine.statemachine.enums.OrderState;
import com.yubzhou.statemachine.statemachine.service.OrderStateMachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * High-level handler that validates preconditions, delegates event sending to
 * the state machine service, and coordinates rollback / retry logic.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventHandler {

    private final OrderService orderService;
    private final OrderStateMachineService stateMachineService;

    /** States that allow a cancellation. */
    private static final List<OrderState> CANCELLABLE_STATES =
            List.of(OrderState.CREATED, OrderState.PENDING);

    /**
     * Send any event to the state machine with pre-validation.
     *
     * @param orderId the target order ID
     * @param event   the event to fire
     * @param request optional context
     */
    public void handle(Long orderId, OrderEvent event, OrderEventRequest request) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("Order not found: " + orderId);
        }

        OrderState currentState = OrderState.valueOf(order.getState());
        log.debug("Handling event {} for order {} (current state: {})", event, orderId, currentState);

        if (event == OrderEvent.CANCEL && !CANCELLABLE_STATES.contains(currentState)) {
            throw new BusinessException("Order " + orderId + " cannot be cancelled in state " + currentState);
        }

        stateMachineService.sendEvent(order, event, request);
    }
}
