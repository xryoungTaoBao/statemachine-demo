package com.yubzhou.statemachine.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yubzhou.statemachine.order.dto.OrderEventRequest;
import com.yubzhou.statemachine.order.entity.Order;
import com.yubzhou.statemachine.order.service.OrderEventLogService;
import com.yubzhou.statemachine.order.service.OrderService;
import com.yubzhou.statemachine.statemachine.enums.OrderEvent;
import com.yubzhou.statemachine.statemachine.enums.OrderState;
import com.yubzhou.statemachine.statemachine.service.OrderStateMachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled tasks for automated order lifecycle management:
 * <ul>
 *   <li>Auto-cancel orders whose payment deadline has passed.</li>
 *   <li>Auto-confirm receipt for orders shipped beyond the timeout window.</li>
 *   <li>Purge old event-log rows.</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderService orderService;
    private final OrderStateMachineService stateMachineService;
    private final OrderEventLogService eventLogService;

    @Value("${scheduler.payment-timeout.enabled:true}")
    private boolean paymentTimeoutEnabled;

    @Value("${scheduler.auto-receive.enabled:true}")
    private boolean autoReceiveEnabled;

    @Value("${scheduler.event-log-cleanup.enabled:true}")
    private boolean eventLogCleanupEnabled;

    @Value("${scheduler.event-log-cleanup.retention-days:90}")
    private int retentionDays;

    @Value("${statemachine.order.auto-receive-days:15}")
    private int autoReceiveDays;

    /** Cancel unpaid orders whose timeout_at is in the past. */
    @Scheduled(cron = "${scheduler.payment-timeout.cron:0 * * * * ?}")
    public void cancelTimedOutOrders() {
        if (!paymentTimeoutEnabled) return;

        List<Order> timedOut = orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getState, OrderState.PENDING.name())
                .lt(Order::getTimeoutAt, LocalDateTime.now()));

        if (timedOut.isEmpty()) return;
        log.info("Found {} orders to auto-cancel (payment timeout)", timedOut.size());

        OrderEventRequest req = new OrderEventRequest();
        req.setOperatorType("SYSTEM");
        req.setRemark("Auto-cancelled due to payment timeout");

        for (Order order : timedOut) {
            try {
                stateMachineService.sendEvent(order, OrderEvent.TIMEOUT, req);
            } catch (Exception e) {
                log.error("Failed to auto-cancel order {}: {}", order.getOrderNo(), e.getMessage());
            }
        }
    }

    /** Auto-confirm receipt for orders shipped more than {@code autoReceiveDays} days ago. */
    @Scheduled(cron = "${scheduler.auto-receive.cron:0 0 * * * ?}")
    public void autoConfirmReceipt() {
        if (!autoReceiveEnabled) return;

        LocalDateTime cutoff = LocalDateTime.now().minusDays(autoReceiveDays);
        List<Order> shipped = orderService.list(new LambdaQueryWrapper<Order>()
                .eq(Order::getState, OrderState.SHIPPED.name())
                .lt(Order::getShipTime, cutoff));

        if (shipped.isEmpty()) return;
        log.info("Found {} orders to auto-confirm receipt", shipped.size());

        OrderEventRequest req = new OrderEventRequest();
        req.setOperatorType("SYSTEM");
        req.setRemark("Auto-confirmed receipt after " + autoReceiveDays + " days");

        for (Order order : shipped) {
            try {
                stateMachineService.sendEvent(order, OrderEvent.RECEIVE, req);
            } catch (Exception e) {
                log.error("Failed to auto-confirm receipt for order {}: {}", order.getOrderNo(), e.getMessage());
            }
        }
    }

    /** Delete event-log entries older than the configured retention period. */
    @Scheduled(cron = "${scheduler.event-log-cleanup.cron:0 0 2 * * ?}")
    public void cleanupEventLogs() {
        if (!eventLogCleanupEnabled) return;
        log.info("Cleaning up event logs older than {} days", retentionDays);
        eventLogService.deleteOlderThan(retentionDays);
    }
}
