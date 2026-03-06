package com.yubzhou.statemachine.management.service;

import com.yubzhou.statemachine.order.dto.OrderEventRequest;

/**
 * Back-office service interface for order management.
 */
public interface OrderManagementService {

    void shipOrder(Long orderId, OrderEventRequest request);

    void approveRefund(Long orderId, OrderEventRequest request);

    void completeRefund(Long orderId, OrderEventRequest request);

    void rejectRefund(Long orderId, OrderEventRequest request);
}
