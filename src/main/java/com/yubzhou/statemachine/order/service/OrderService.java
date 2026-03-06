package com.yubzhou.statemachine.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yubzhou.statemachine.order.dto.CreateOrderRequest;
import com.yubzhou.statemachine.order.dto.OrderEventRequest;
import com.yubzhou.statemachine.order.dto.OrderVO;
import com.yubzhou.statemachine.order.entity.Order;
import com.yubzhou.statemachine.statemachine.enums.OrderEvent;

import java.util.List;

/**
 * Order service interface.
 */
public interface OrderService extends IService<Order> {

    /** Create a new order and initialize its state machine. */
    OrderVO createOrder(CreateOrderRequest request);

    /** Send an event to the order state machine. */
    OrderVO sendEvent(Long orderId, OrderEvent event, OrderEventRequest request);

    /** Get an order by ID, throwing if not found. */
    OrderVO getOrderById(Long orderId);

    /** List orders by state. */
    List<OrderVO> listByState(String state);

    /** List all orders for a user. */
    List<OrderVO> listByUserId(Long userId);
}
