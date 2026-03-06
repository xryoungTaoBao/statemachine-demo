package com.yubzhou.statemachine.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yubzhou.statemachine.order.entity.OrderStateHistory;

import java.util.List;

/**
 * Service for reading and writing order state history records.
 */
public interface OrderStateHistoryService extends IService<OrderStateHistory> {

    /** Return all history rows for a given order, oldest first. */
    List<OrderStateHistory> listByOrderId(Long orderId);

    /** Return the most recent N normal history rows for a given order. */
    List<OrderStateHistory> listRecentByOrderId(Long orderId, int limit);

    /** Persist a single transition record. */
    void saveHistory(OrderStateHistory history);
}
