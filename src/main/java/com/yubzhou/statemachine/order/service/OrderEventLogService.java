package com.yubzhou.statemachine.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yubzhou.statemachine.order.entity.OrderEventLog;

import java.util.List;

/**
 * Service for reading and writing order event logs.
 */
public interface OrderEventLogService extends IService<OrderEventLog> {

    /** Return all event log rows for a given order, newest first. */
    List<OrderEventLog> listByOrderId(Long orderId);

    /** Persist a single event log entry. */
    void saveLog(OrderEventLog log);

    /** Delete log entries older than the given number of days. */
    void deleteOlderThan(int retentionDays);
}
