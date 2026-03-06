package com.yubzhou.statemachine.common.util;

import com.yubzhou.statemachine.statemachine.enums.OrderState;

import java.util.List;
import java.util.Map;

/**
 * Utilities related to order state transitions.
 */
public final class OrderStateUtils {

    private OrderStateUtils() {}

    /** States that are considered terminal (no further transitions possible). */
    private static final List<OrderState> TERMINAL_STATES =
            List.of(OrderState.COMPLETED, OrderState.CANCELLED, OrderState.REFUNDED);

    /** Map of state -> human-readable Chinese label. */
    private static final Map<OrderState, String> LABELS = Map.of(
            OrderState.PENDING_PAYMENT,  "待付款",
            OrderState.PENDING_SHIPMENT, "待发货",
            OrderState.SHIPPED,          "已发货",
            OrderState.COMPLETED,        "已完成",
            OrderState.CANCELLED,        "已取消",
            OrderState.REFUNDING,        "退款中",
            OrderState.REFUNDED,         "已退款"
    );

    public static boolean isTerminal(OrderState state) {
        return TERMINAL_STATES.contains(state);
    }

    public static String getLabel(OrderState state) {
        return LABELS.getOrDefault(state, state.name());
    }
}
