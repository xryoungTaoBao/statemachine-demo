package com.yubzhou.statemachine.management.service;

import com.yubzhou.statemachine.common.exception.BusinessException;
import com.yubzhou.statemachine.order.dto.OrderEventRequest;
import com.yubzhou.statemachine.order.entity.Order;
import com.yubzhou.statemachine.order.service.OrderService;
import com.yubzhou.statemachine.statemachine.enums.OrderEvent;
import com.yubzhou.statemachine.statemachine.service.OrderStateMachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link OrderManagementService}.
 */
@Service
@RequiredArgsConstructor
public class OrderManagementServiceImpl implements OrderManagementService {

    private final OrderService orderService;
    private final OrderStateMachineService stateMachineService;

    @Override
    public void shipOrder(Long orderId, OrderEventRequest request) {
        sendEvent(orderId, OrderEvent.SHIP_ORDER, request);
    }

    @Override
    public void approveRefund(Long orderId, OrderEventRequest request) {
        sendEvent(orderId, OrderEvent.APPROVE_REFUND, request);
    }

    @Override
    public void completeRefund(Long orderId, OrderEventRequest request) {
        sendEvent(orderId, OrderEvent.COMPLETE_REFUND, request);
    }

    @Override
    public void rejectRefund(Long orderId, OrderEventRequest request) {
        sendEvent(orderId, OrderEvent.REJECT_REFUND, request);
    }

    private void sendEvent(Long orderId, OrderEvent event, OrderEventRequest request) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException("Order not found: " + orderId);
        }
        stateMachineService.sendEvent(order, event, request);
    }
}
